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

	private JSONObject createTestData() {
		JSONObject data = JSON.parse(new File("./test/integration/test-files/summary/data.json").text)
		dataService.sync(userInstance, data)
		return data
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
		controller.response.json != null
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
		JSONObject data = createTestData()
		flushSession()

		assert SummaryData.count() == 7

		when: "The sync action is called with almost the same summary data but with few modifications"
		data["activity_summary"][0]["active_cal"] = "531"
		data["exercise_summary"][2]["classification"] = "vigorous"

		controller.request.json = data.toString()
		controller.request.method = "POST"
		controller.sync()

		then: "No new record should be created and other data will be updated"
		controller.response.status == HttpStatus.OK.value()
		controller.response.json != null
		controller.response.json.success == true
		SummaryData.count() == 7

		SummaryData summaryDataInstance1 = SummaryData.findByEventTimeAndType(1441195200l, SummaryDataType.ACTIVITY)
		summaryDataInstance1.refresh().data["active_cal"] == "531"

		SummaryData summaryDataInstance2 = SummaryData.findByEventTimeAndType(1400132931l, SummaryDataType.EXERCISE)
		summaryDataInstance2.refresh().data["classification"] == "vigorous"
	}

	void "test sync action when there is a validation failure on one of the event"() {
		given: "Summary data with one of them has invalid data"
		JSONObject data = JSON.parse(new File("./test/integration/test-files/summary/data.json").text)

		data["activity_summary"][0].time_utc = -2		// Passing invalid amount since UNIX timestamp can't be -2

		when: "The sync action is called with the above summary data"
		controller.response.reset()
		controller.request.json = data.toString(0)
		controller.request.method = "POST"
		controller.sync()

		then: "All the summary data should be imported and 6 instances should be created"
		controller.response.status == HttpStatus.OK.value()
		controller.response.json != null
		controller.response.json.size() == 1
		controller.response.json[0].id == JSONObject.NULL
		controller.response.json[0].eventTime == -2
		controller.response.json[0].data.active_cal == "369"
		controller.response.json[0].errors != null
		controller.response.json[0].errors[0]["field"] == "eventTime"
		controller.response.json[0].errors[0]["rejected-value"] == -2

		List<SummaryData> summaryDataList = SummaryData.list([sort: "id", order: "asc"])
		summaryDataList.size() == 6
	}

	void "test get endpoint for invalid data type"() {
		when: "The get endpoint is hit for an in-correct data type"
		controller.params.id = 1441213920
		controller.params.dataType = "example"
		controller.get()

		then: "Non 200 response should be returned"
		controller.response.status == HttpStatus.NOT_ACCEPTABLE.value()
		controller.response.json["error_description"].contains("Invalid data type. Allowed values are") == true
	}

	void "test get endpoint"() {
		given: "Some SummaryData instances"
		createTestData()

		when: "The get endpoint is hit for a correct data type and id"
		controller.params.id = 1441213920
		controller.params.dataType = "activity"
		controller.get()

		then: "It should return the correct data"
		controller.response.status == HttpStatus.OK.value()
		controller.response.json != null
		controller.response.json["id"] == SummaryData.findByEventTimeAndType(1441213920l, SummaryDataType.ACTIVITY).id
		controller.response.json["userID"] == userInstance.id
		controller.response.json["eventTime"] == 1441213920l
		controller.response.json["timeZone"] == "+2.5"
		controller.response.json["version"] == 0
		controller.response.json["data"]["non_wear_m"] == "72"
		controller.response.json["data"]["steps"] == "1"
		controller.response.json["data"]["eq_meters"] == "5240"
		controller.response.json["data"]["active_cal"] == "369"

		when: "The get endpoint is hit for the event id but different datatype"
		controller.response.reset()
		controller.params.id = 1441213920
		controller.params.dataType = "sleep"
		controller.get()

		then: "The no record should be found"
		controller.response.status == HttpStatus.NOT_FOUND.value()
	}

	void "test delete endpoint"() {
		given: "Some SummaryData instances"
		createTestData()

		when: "The delete endpoint is hit for a correct data type and id as event timestamp"
		controller.params.id = 1441213920
		controller.params.dataType = "activity"
		controller.request.method = "DELETE"
		controller.delete()

		then: "It should delete the record"
		controller.response.status == HttpStatus.NO_CONTENT.value()
		SummaryData.findByEventTimeAndType(1441213920l, SummaryDataType.ACTIVITY) == null
		SummaryData.count() == 6

		when: "The delete endpoint is hit to delete the record with id as Grails domain ID"
		SummaryData firstRecord = SummaryData.first()

		controller.response.reset()
		controller.params.id = firstRecord.id
		controller.params.dataType = firstRecord.type.name()		// Tests for data type in uppercase
		controller.request.method = "DELETE"
		controller.delete()

		then: "The record should be deleted"
		controller.response.status == HttpStatus.NO_CONTENT.value()
		SummaryData.get(firstRecord.id) == null
		SummaryData.count() == 5
	}

	void "test delete endpoint for invalid data type"() {
		when: "The get endpoint is hit for an in-correct data type"
		controller.request.method = "DELETE"
		controller.params.dataType = "ALL"
		controller.params.id = 1441213920
		controller.delete()

		then: "Non 200 response should be returned"
		controller.response.status == HttpStatus.NOT_ACCEPTABLE.value()
		controller.response.json["error_description"].contains("Invalid data type. Allowed values are") == true
	}
}