package us.wearecurio.controllers
import grails.converters.JSON
import grails.plugin.springsecurity.SpringSecurityService
import org.codehaus.groovy.grails.web.json.JSONObject
import org.springframework.http.HttpStatus
import us.wearecurio.BaseIntegrationSpec
import us.wearecurio.model.PubSubNotification
import us.wearecurio.model.SummaryData
import us.wearecurio.model.SummaryDataType
import us.wearecurio.oauth.Client
import us.wearecurio.oauth.ClientEnvironment
import us.wearecurio.services.DataService

class DataControllerSpec extends BaseIntegrationSpec {

	private DataController controller

	DataService dataService
	SpringSecurityService springSecurityService
	Client clientInstance, nonHookURLClientInstance

	def setup() {
		controller = new DataController()
		controller.springSecurityService = [getCurrentUser: { ->
			return userInstance
		}] as SpringSecurityService

		clientInstance = new Client([clientId: "client-id", clientSecret: "secret-key",
				clientServerURL: "localhost:8080", name: "test-app", clientHookURL: "localhost:8080",
				environment: ClientEnvironment.current])
		clientInstance.save()

		nonHookURLClientInstance = new Client([clientId: "client2-id", clientSecret: "secret-key",
				clientServerURL: "localhost:8080", name: "test-app2", environment: ClientEnvironment.current])
		nonHookURLClientInstance.save()
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

		then: "All the summary data should be imported and 7 instances should be created and 6 pubsubnotification instance should be created"
		controller.response.status == HttpStatus.OK.value()
		controller.response.json != null
		controller.response.json.success == true

		List<SummaryData> summaryDataList = SummaryData.list([sort: "id", order: "asc"])
		summaryDataList.size() == 7

		summaryDataList[0].type == SummaryDataType.ACTIVITY
		summaryDataList[0].user.id == userInstance.id
		summaryDataList[0].eventTime == 1441195200l
		summaryDataList[0].timeZone == "+02:30"
		summaryDataList[0].data["non_wear_m"] == "72"
		summaryDataList[0].data["steps"] == "6551"
		summaryDataList[0].data["eq_meters"] == "5240"
		summaryDataList[0].data["active_cal"] == "369"
		summaryDataList[0].data["total_cal"] == "2422"
		// Map will not contain any timestamp or timezone data
		summaryDataList[0].data.size() == 5
		summaryDataList[0].processAfterLaunch == true

		summaryDataList[1].user.id == userInstance.id
		// Making sure the timestamp value can be same for different type
		summaryDataList[1].type == SummaryDataType.ACTIVITY
		summaryDataList[1].eventTime == 1441213920l
		summaryDataList[1].timeZone == "+02:30"
		summaryDataList[1].data["steps"] == "1"
		summaryDataList[1].data["total_cal"] == null
		summaryDataList[1].processAfterLaunch == true

		summaryDataList[2].type == SummaryDataType.EXERCISE
		summaryDataList[2].user.id == userInstance.id
		summaryDataList[2].eventTime == 1441213920l
		summaryDataList[2].timeZone == "+02:00"
		summaryDataList[2].data["duration_m"] == "53"
		summaryDataList[2].data["classification"] == "moderate"
		summaryDataList[2].processAfterLaunch == true

		summaryDataList[3].type == SummaryDataType.EXERCISE
		summaryDataList[3].user.id == userInstance.id
		summaryDataList[3].eventTime == 1441312920l
		summaryDataList[3].timeZone == "-01:00"
		summaryDataList[3].data["classification"] == "vigorous"
		summaryDataList[3].processAfterLaunch == true

		summaryDataList[4].type == SummaryDataType.EXERCISE
		summaryDataList[4].user.id == userInstance.id
		summaryDataList[4].eventTime == 1400132931l
		summaryDataList[4].timeZone == "+03:30"
		summaryDataList[4].data["classification"] == "light"
		summaryDataList[4].processAfterLaunch == true

		summaryDataList[5].type == SummaryDataType.SLEEP
		summaryDataList[5].user.id == userInstance.id
		summaryDataList[5].eventTime == 1441151652l
		summaryDataList[5].timeZone == "+05:30"
		summaryDataList[5].data["bedtime_m"] == "503"
		summaryDataList[5].data["sleep_score"] == "81"
		summaryDataList[5].data["awake_m"] == "10"
		summaryDataList[5].data["rem_m"] == "150"
		summaryDataList[5].data["light_m"] == "139"
		summaryDataList[5].data["deep_m"] == "234"
		// Map will not contain any timestamp or timezone data
		summaryDataList[5].data.size() == 6
		summaryDataList[5].processAfterLaunch == null

		summaryDataList[6].type == SummaryDataType.SLEEP
		summaryDataList[6].user.id == userInstance.id
		summaryDataList[6].eventTime == 1441236720l
		summaryDataList[5].processAfterLaunch == null

		List<PubSubNotification> pubSubNotificationList = PubSubNotification.getAll()
		pubSubNotificationList.size() == 2
		/*pubSubNotificationList[0].date == (new Date(1441195200l*1000)).clearTime()
		pubSubNotificationList[0].type == SummaryDataType.ACTIVITY

		pubSubNotificationList[1].date == (new Date(1441213920l*1000)).clearTime()
		pubSubNotificationList[1].type == SummaryDataType.EXERCISE

		pubSubNotificationList[2].date == (new Date(1441312920l*1000)).clearTime()
		pubSubNotificationList[2].type == SummaryDataType.EXERCISE

		pubSubNotificationList[3].date == (new Date(1400132931l*1000)).clearTime()
		pubSubNotificationList[3].type == SummaryDataType.EXERCISE*/

		pubSubNotificationList[0].date == (new Date(1441151652l*1000)).clearTime()
		pubSubNotificationList[0].type == SummaryDataType.SLEEP

		pubSubNotificationList[1].date == (new Date(1441236720l*1000)).clearTime()
		pubSubNotificationList[1].type == SummaryDataType.SLEEP
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
		List<PubSubNotification> pubSubNotificationList = PubSubNotification.getAll()
		// No notification should be created as we are creating notifications only for sleep data.
		pubSubNotificationList.size() == 2

		/*pubSubNotificationList[0].date == (new Date(1441195200l*1000)).clearTime()
		pubSubNotificationList[0].type == SummaryDataType.ACTIVITY

		pubSubNotificationList[1].date == (new Date(1441213920l*1000)).clearTime()
		pubSubNotificationList[1].type == SummaryDataType.EXERCISE*/
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

		then: "All the summary data should be imported and 6 instances should be created for each SummaryData and PubSubNotification"
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
		List<PubSubNotification> pubSubNotificationList = PubSubNotification.getAll()
		pubSubNotificationList.size() == 2
	}

	void "test sync action for all data when two clients have clientHookURL"() {
		when: "The sync action is called with the summary data for all types with two clients having clientHookURL"
		nonHookURLClientInstance.clientHookURL = "localhost:8080"
		nonHookURLClientInstance.save()
		controller.response.reset()
		controller.request.json = new File("./test/integration/test-files/summary/data.json").text
		controller.request.method = "POST"
		controller.sync()

		then: "All the summary data should be imported and 7 instances should be created and 12 pubsubnotification instance should be created"
		controller.response.status == HttpStatus.OK.value()
		controller.response.json != null
		controller.response.json.success == true

		List<SummaryData> summaryDataList = SummaryData.list([sort: "id", order: "asc"])
		summaryDataList.size() == 7

		summaryDataList[0].type == SummaryDataType.ACTIVITY
		summaryDataList[0].user.id == userInstance.id
		summaryDataList[0].eventTime == 1441195200l
		summaryDataList[0].timeZone == "+02:30"
		summaryDataList[0].data["non_wear_m"] == "72"
		summaryDataList[0].data["steps"] == "6551"
		summaryDataList[0].data["eq_meters"] == "5240"
		summaryDataList[0].data["active_cal"] == "369"
		summaryDataList[0].data["total_cal"] == "2422"

		summaryDataList[1].user.id == userInstance.id
		// Making sure the timestamp value can be same for different type
		summaryDataList[1].type == SummaryDataType.ACTIVITY
		summaryDataList[1].eventTime == 1441213920l
		summaryDataList[1].timeZone == "+02:30"
		summaryDataList[1].data["steps"] == "1"
		summaryDataList[1].data["total_cal"] == null

		summaryDataList[2].type == SummaryDataType.EXERCISE
		summaryDataList[2].user.id == userInstance.id
		summaryDataList[2].eventTime == 1441213920l
		summaryDataList[2].timeZone == "+02:00"
		summaryDataList[2].data["duration_m"] == "53"
		summaryDataList[2].data["classification"] == "moderate"

		List<PubSubNotification> pubSubNotificationList = PubSubNotification.getAll()
		pubSubNotificationList.size() == 4
		/*pubSubNotificationList[0].date == (new Date(1441195200l*1000)).clearTime()
		pubSubNotificationList[0].type == SummaryDataType.ACTIVITY
		pubSubNotificationList[0].client == clientInstance

		pubSubNotificationList[1].date == (new Date(1441195200l*1000)).clearTime()
		pubSubNotificationList[1].type == SummaryDataType.ACTIVITY
		pubSubNotificationList[1].client == nonHookURLClientInstance*/

		pubSubNotificationList[0].date == (new Date(1441151652l*1000)).clearTime()
		pubSubNotificationList[0].type == SummaryDataType.SLEEP
		pubSubNotificationList[0].client == clientInstance

		pubSubNotificationList[1].date == (new Date(1441151652l*1000)).clearTime()
		pubSubNotificationList[1].type == SummaryDataType.SLEEP
		pubSubNotificationList[1].client == nonHookURLClientInstance

	}

	void "test sync action for all data including unknown data"() {
		when: "The sync action is called with the summary data including some unknown data"
		controller.request.json = new File("./test/integration/test-files/summary/unknown-data.json").text
		controller.request.method = "POST"
		controller.sync()

		then: "All the data should be imported, 9 instances should be created and 6 PubSubNotification should be created"
		controller.response.status == HttpStatus.OK.value()
		controller.response.json != null
		controller.response.json.success == true

		List<SummaryData> summaryDataList = SummaryData.list([sort: "id", order: "asc"])
		summaryDataList.size() == 9

		summaryDataList[0].type == SummaryDataType.ACTIVITY
		summaryDataList[0].user.id == userInstance.id
		summaryDataList[0].eventTime == 1441195200l
		summaryDataList[0].timeZone == "+02:30"
		summaryDataList[0].data["non_wear_m"] == "72"
		summaryDataList[0].data["steps"] == "6551"
		summaryDataList[0].data["eq_meters"] == "5240"
		summaryDataList[0].data["active_cal"] == "369"
		summaryDataList[0].data["total_cal"] == "2422"
		summaryDataList[0].processAfterLaunch == true

		summaryDataList[1].user.id == userInstance.id
		// Making sure the timestamp value can be same for different type
		summaryDataList[1].type == SummaryDataType.ACTIVITY
		summaryDataList[1].eventTime == 1441213920l
		summaryDataList[1].timeZone == "+02:30"
		summaryDataList[1].data["steps"] == "1"
		summaryDataList[1].data["total_cal"] == null
		summaryDataList[1].processAfterLaunch == true

		summaryDataList[2].type == SummaryDataType.EXERCISE
		summaryDataList[2].user.id == userInstance.id
		summaryDataList[2].eventTime == 1441213920l
		summaryDataList[2].timeZone == "+02:00"
		summaryDataList[2].data["duration_m"] == "53"
		summaryDataList[2].data["classification"] == "moderate"
		summaryDataList[2].processAfterLaunch == true

		summaryDataList[3].type == SummaryDataType.EXERCISE
		summaryDataList[3].user.id == userInstance.id
		summaryDataList[3].eventTime == 1441312920l
		summaryDataList[3].data["classification"] == "vigorous"
		summaryDataList[3].data["duration_m"] == "28"
		// Map will not contain any timestamp or timezone data
		summaryDataList[3].data.size() == 2
		summaryDataList[3].processAfterLaunch == true

		summaryDataList[4].type == SummaryDataType.EXERCISE
		summaryDataList[4].user.id == userInstance.id
		summaryDataList[4].eventTime == 1400132931l
		summaryDataList[4].timeZone == "+03:30"
		summaryDataList[4].data["classification"] == "light"
		summaryDataList[4].data["duration_m"] == "31"
		summaryDataList[4].data.size() == 2
		summaryDataList[4].processAfterLaunch == true

		summaryDataList[5].type == SummaryDataType.SLEEP
		summaryDataList[5].user.id == userInstance.id
		summaryDataList[5].eventTime == 1441151652l
		summaryDataList[5].timeZone == "+05:30"
		summaryDataList[5].data["bedtime_m"] == "503"
		summaryDataList[5].data["sleep_score"] == "81"
		summaryDataList[5].data["awake_m"] == "10"
		summaryDataList[5].data["rem_m"] == "150"
		summaryDataList[5].data["light_m"] == "139"
		summaryDataList[5].data["deep_m"] == "234"
		summaryDataList[5].data.size() == 6
		summaryDataList[5].processAfterLaunch == null

		summaryDataList[6].type == SummaryDataType.SLEEP
		summaryDataList[6].user.id == userInstance.id
		summaryDataList[6].eventTime == 1441236720l
		summaryDataList[6].processAfterLaunch == null

		summaryDataList[7].type == SummaryDataType.UNKNOWN
		summaryDataList[7].user.id == userInstance.id
		summaryDataList[7].eventTime == 1441151653l
		summaryDataList[7].timeZone == "+05:30"
		summaryDataList[7].data["pulse"] == "72"
		summaryDataList[7].data["bpDiastolic"] == "81"
		summaryDataList[7].data.size() == 2
		summaryDataList[7].processAfterLaunch == null

		summaryDataList[8].type == SummaryDataType.UNKNOWN
		summaryDataList[8].user.id == userInstance.id
		summaryDataList[8].eventTime == 1441236720l
		summaryDataList[8].timeZone == "-02:00"
		summaryDataList[8].data["pulse"] == "75"
		summaryDataList[8].data["bpSystolic"] == "31"
		summaryDataList[8].data.size() == 2
		summaryDataList[8].processAfterLaunch == null

		// Notification should not be created for UNKNOWN type data
		List<PubSubNotification> pubSubNotificationList = PubSubNotification.getAll()
		pubSubNotificationList.size() == 2
		// Commenting out this for now as we are not sending any data other than Sleep.
		/*pubSubNotificationList[0].date == (new Date(1441195200l * 1000)).clearTime()
		pubSubNotificationList[0].type == SummaryDataType.ACTIVITY

		pubSubNotificationList[1].date == (new Date(1441213920l * 1000)).clearTime()
		pubSubNotificationList[1].type == SummaryDataType.EXERCISE

		pubSubNotificationList[2].date == (new Date(1441312920l * 1000)).clearTime()
		pubSubNotificationList[2].type == SummaryDataType.EXERCISE

		pubSubNotificationList[3].date == (new Date(1400132931l * 1000)).clearTime()
		pubSubNotificationList[3].type == SummaryDataType.EXERCISE*/

		pubSubNotificationList[0].date == (new Date(1441151652l * 1000)).clearTime()
		pubSubNotificationList[0].type == SummaryDataType.SLEEP

		pubSubNotificationList[1].date == (new Date(1441236720l * 1000)).clearTime()
		pubSubNotificationList[1].type == SummaryDataType.SLEEP
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
		controller.response.json["timeZone"] == "+02:30"
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