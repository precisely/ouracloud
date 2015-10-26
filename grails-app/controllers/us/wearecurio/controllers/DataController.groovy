package us.wearecurio.controllers

import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.annotation.Secured
import org.codehaus.groovy.grails.web.converters.exceptions.ConverterException
import org.springframework.http.HttpStatus
import us.wearecurio.BaseController
import us.wearecurio.model.SummaryData
import us.wearecurio.model.SummaryDataType
import us.wearecurio.services.DataService
import us.wearecurio.users.User

/**
 * The main controller which will be used to save, update, delete or get the summary data with oauth2 authentication.
 *
 * @author Shashank Agrawal
 * @since 0.0.1
 * @see UrlMappings.groovy file for endpoint mapping
 */
@Secured(["ROLE_USER"])
class DataController implements BaseController {

	static allowedMethods = [get: "GET", save: "POST", update: "PUT", delete: "DELETE", sync: "POST"]

	DataService dataService
	SpringSecurityService springSecurityService

	/**
	 * Delete any summary data record of the given type associated with the authorization user.
	 * For example:<br>Following API call will delete a sleep data record with ID 2
	 * @Request
	 * <pre>
	 * 		DELETE 		/api/sleep/2
	 *
	 * @Response
	 * 		No content - 204
	 *
	 * <p>
	 * Following will delete an activity data with event timestamp value as "1400132931"
	 * @Request
	 * <pre>
	 * 		DELETE 		/api/sleep/1400132931
	 *
	 * @Response
	 * 		No content - 204
	 */
	def delete(Long id, String dataType) {
		User currentUserInstance = springSecurityService.getCurrentUser()
		log.debug "$currentUserInstance trying to delete $dataType with id $id"

		SummaryData summaryDataInstance = dataService.get(currentUserInstance, id, SummaryDataType.lookup(dataType))

		if (!summaryDataInstance) {
			respondNotFound([message: g.message([code: "summary.data.not.found", args: [id, dataType]])])
			return
		}
		log.debug "Deleting record $summaryDataInstance"

		summaryDataInstance.delete(flush: true)
		render(status: HttpStatus.NO_CONTENT)
	}

	/**
	 * Get a single summary data record of the given type associated with the authorized user.
	 * For example:<br>Any of the following API call will return the same thing
	 * @Request
	 * <pre>
	 * GET		/api/activity/1
	 * GET		/api/activity/1441195200
	 *
	 * @Response
	 * <pre>
	 	{
			"id": 1,
			"version": 17,
			"eventTime": 1441195200,
			"timeZone": "-2.5",
			"dateCreated": "2015-09-30T12:23:15Z",
			"lastUpdated": "2015-09-30T13:43:20Z",
			"data": {
				"total_cal": "2422",
				"eq_meters": "5dd240",
				"non_wear_m": "72",
				"active_cal": "369",
				"steps": "651"
			},
			"userID": 1,
			"type": "ACTIVITY"
		}
	 */
	def get(Long id, String dataType) {
		User currentUserInstance = springSecurityService.getCurrentUser()
		SummaryData summaryDataInstance = dataService.get(currentUserInstance, id, SummaryDataType.lookup(dataType))

		if (!summaryDataInstance) {
			respondNotFound([message: g.message([code: "summary.data.not.found", args: [id, dataType]])])
			return
		}

		respond(summaryDataInstance)
	}

	/**
	 * Get the list of summary data records.
	 */
	def index(Integer max, String dataType) {
		params.max = Math.min(max ?: 10, 100)

		User currentUserInstance = springSecurityService.getCurrentUser()

		List<SummaryData> summaryDataInstanceList = SummaryData.createCriteria().list(params) {
			eq("user", currentUserInstance)

			if (dataType && dataType != "all") {
				eq("type", SummaryDataType.lookup(dataType))
			}
			if (params.timestamp) {
				eq("eventTime", params.long("timestamp"))
			}
			if (params.startTimestamp && params.endTimestamp) {
				between("eventTime", params.long("params.startTimestamp"), params.long("params.endTimestamp"))
			}
		}

		respond([data: summaryDataInstanceList, totalCount: summaryDataInstanceList.totalCount])
	}

	/**
	 * Save a new record of given type for the authorized user. If a record with given timestamp and type is already
	 * exists then the same record will be updated instead of creating a new record.
	 * @Request
	 * <pre>
	 * POST		/api/exercise
	 * <b>Request Body</b>
	 	{
			"start_time_utc": "1441213920",
			"time_zone": "2",
			"duration_m": "53",
			"classification": "moderate"
		}
	 *
	 * @Response
	 * Response will be same as the {@link #get get} API.
	 */
	def save(String dataType) {
		Map requestData = request.JSON
		User currentUserInstance = springSecurityService.getCurrentUser()
		log.debug "$currentUserInstance trying to save $dataType with $requestData"

		SummaryDataType.lookup(dataType)		// Just validate dataType

		SummaryData summaryDataInstance = dataService."save${dataType.capitalize()}Data"(currentUserInstance, requestData)

		if (summaryDataInstance.hasErrors()) {
			respond(summaryDataInstance.errors)
			return
		}

		respond(summaryDataInstance)
	}

	/**
	 * Batch store data received from the OuraRing device which should contain all the JSON data available in
	 * the request body.
	 */
	def sync() {
		User currentUserInstance = springSecurityService.getCurrentUser()
		Map requestData = request.JSON
		log.debug "Syncing $currentUserInstance data"

		List<SummaryData> summaryDataList = dataService.sync(currentUserInstance, requestData)

		// If there is any record with failed validation
		if (summaryDataList) {
			// Then respond the errors to the client
			respond(summaryDataList)
		} else {
			respond([success: true])
		}
	}

	/**
	 * Update a record with given type and id.
	 * @Request
	 * <pre>
	 * PUT		/api/exercise/1441213920
	 * <b>Request Body</b>
		{
			"start_time_utc": "1441213920",
			"time_zone": "2",
			"duration_m": "53",
			"classification": "moderate"
	 	}
	 */
	def update(Long id, String dataType) {
		Map requestData = request.JSON
		User currentUserInstance = springSecurityService.getCurrentUser()
		log.debug "$currentUserInstance trying to update $dataType with id $id and data $requestData"

		SummaryData summaryDataInstance = dataService.get(currentUserInstance, id, SummaryDataType.lookup(dataType))

		if (!summaryDataInstance) {
			respondNotFound([message: g.message([code: "summary.data.not.found", args: [id, dataType]])])
			return
		}

		dataService.update(summaryDataInstance, requestData)

		if (summaryDataInstance.hasErrors()) {
			respond(summaryDataInstance.errors)
			return
		}

		respond(summaryDataInstance)
	}

	/**
	 * Handle any JSON parse exception thrown by Grails internally when trying to parse the request body.
	 * @see http://grails.github.io/grails-doc/2.5.0/guide/theWebLayer.html#controllerExceptionHandling
	 */
	Object handleJSONParseException(ConverterException e) {
		log.info "JSONException with message: $e.message"
		respondNotAcceptable([error: e.message, error_description: e.cause.message])
		return
	}
}