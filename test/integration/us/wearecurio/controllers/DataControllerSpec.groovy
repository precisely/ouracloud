package us.wearecurio.controllers

import grails.converters.JSON
import grails.plugin.springsecurity.SpringSecurityService
import org.codehaus.groovy.grails.web.json.JSONObject
import org.springframework.http.HttpStatus
import us.wearecurio.BaseIntegrationSpec
import us.wearecurio.model.SummaryData
import us.wearecurio.model.SummaryDataType
import us.wearecurio.services.DataService

class DataControllerSpec extends BaseIntegrationSpec {

	private DataController controller

	DataService dataService
	SpringSecurityService springSecurityService

	def setup() {
		controller = new DataController()
		controller.springSecurityService = [getCurrentUser: { ->
			return userInstance
		}] as SpringSecurityService
	}

	def cleanup() {
		controller.springSecurityService = springSecurityService
	}

	void "test sync action for all data"() {
		when: "The sync action is called with no data"
		controller.request.method = "POST"
		controller.sync()

		then: "No instance should be created"
		controller.response.status == HttpStatus.OK.value()
		controller.response.json.success == true
		SummaryData.count() == 0

		when: "The sync action is called with the summary data for all types"
		controller.response.reset()
		controller.request.json = new File("./test/integration/test-files/summary/data.json").text
		controller.request.method = "POST"
		controller.sync()

		then: "All the summary data should be imported and 7 instances should be created"
		controller.response.status == HttpStatus.OK.value()
		controller.response.json.success == true

		List<SummaryData> summaryDataList = SummaryData.list([sort: "id", order: "asc"])
		summaryDataList.size() == 7

		summaryDataList[0].type == SummaryDataType.ACTIVITY
		summaryDataList[0].user.id == userInstance.id
		summaryDataList[0].eventTime == 1441195200l
		summaryDataList[0].timeZone == "2.5"
		summaryDataList[0].data["non_wear_m"] == "72"
		summaryDataList[0].data["steps"] == "6551"
		summaryDataList[0].data["eq_meters"] == "5240"
		summaryDataList[0].data["active_cal"] == "369"
		summaryDataList[0].data["total_cal"] == "2422"

		summaryDataList[1].user.id == userInstance.id
		// Making sure the timestamp value can be same for different type
		summaryDataList[1].type == SummaryDataType.ACTIVITY
		summaryDataList[1].eventTime == 1441213920l
		summaryDataList[1].timeZone == "+2.5"
		summaryDataList[1].data["steps"] == "1"
		summaryDataList[1].data["total_cal"] == null

		summaryDataList[2].type == SummaryDataType.EXERCISE
		summaryDataList[2].user.id == userInstance.id
		summaryDataList[2].eventTime == 1441213920l
		summaryDataList[2].timeZone == "2"
		summaryDataList[2].data["duration_m"] == "53"
		summaryDataList[2].data["classification"] == "moderate"

		summaryDataList[3].type == SummaryDataType.EXERCISE
		summaryDataList[3].user.id == userInstance.id
		summaryDataList[3].eventTime == 1441312920l
		summaryDataList[3].timeZone == "-1"
		summaryDataList[3].data["classification"] == "vigorous"

		summaryDataList[4].type == SummaryDataType.EXERCISE
		summaryDataList[4].user.id == userInstance.id
		summaryDataList[4].eventTime == 1400132931l
		summaryDataList[4].timeZone == "3.5"
		summaryDataList[4].data["classification"] == "light"

		summaryDataList[5].type == SummaryDataType.SLEEP
		summaryDataList[5].user.id == userInstance.id
		summaryDataList[5].eventTime == 1441151652l
		summaryDataList[5].timeZone == "5.5"
		summaryDataList[5].data["bedtime_m"] == "503"
		summaryDataList[5].data["sleep_score"] == "81"
		summaryDataList[5].data["awake_m"] == "10"
		summaryDataList[5].data["rem_m"] == "150"
		summaryDataList[5].data["light_m"] == "139"
		summaryDataList[5].data["deep_m"] == "234"

		summaryDataList[6].type == SummaryDataType.SLEEP
		summaryDataList[6].user.id == userInstance.id
		summaryDataList[6].eventTime == 1441236720l
	}

	void "test sync action when same data is passed again"() {
		given: "Already stored data"
		JSONObject data = JSON.parse(new File("./test/integration/test-files/summary/data.json").text)
		dataService.sync(userInstance, data)
		flushSession()

		assert SummaryData.count() == 7

		when: "The sync action is called with the same summary data modifying some additional data"
		data["activity_summary"][0]["active_cal"] = "531"
		data["exercise_summary"][2]["classification"] = "vigorous"

		controller.request.json = data.toString()
		controller.request.method = "POST"
		controller.sync()

		then: "No new record should be created and other data will be updated"
		controller.response.status == HttpStatus.OK.value()
		controller.response.json.success == true
		SummaryData.count() == 7

		SummaryData summaryDataInstance1 = SummaryData.findByEventTimeAndType(1441195200l, SummaryDataType.ACTIVITY)
		summaryDataInstance1.refresh().data["active_cal"] == "531"

		SummaryData summaryDataInstance2 = SummaryData.findByEventTimeAndType(1400132931l, SummaryDataType.EXERCISE)
		summaryDataInstance2.refresh().data["classification"] == "vigorous"
	}
}
