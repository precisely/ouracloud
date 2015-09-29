package us.wearecurio.controllers

import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.annotation.Secured
import us.wearecurio.model.SummaryData
import us.wearecurio.services.DataService
import us.wearecurio.users.User

/**
 * The main controller which will be used to save, update, delete or get the summary data with oauth2 authentication.
 * @since 0.0.1
 * @author Shashank Agrawal
 * @see UrlMappings.groovy file for end-point mapping
 */
@Secured(["ROLE_USER"])
class DataController {

	static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE", sync: "POST"]
	static responseFormats = ["json"]

	DataService dataService
	SpringSecurityService springSecurityService

	def index(Integer max, String dataType) {
		params.max = Math.min(max ?: 10, 100)
		respond SummaryData.list(params), model:[summaryDataInstanceCount: SummaryData.count()]
	}

	/**
	 * Endpoint to store data received from the OuraRing device which should contain all the JSON data available in
	 * the request body.
	 */
	def sync() {
		User currentUserInstance = springSecurityService.getCurrentUser()
		Map requestData = request.JSON

		dataService.sync(currentUserInstance, requestData)

		respond([success: true])
	}
}