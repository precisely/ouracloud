package us.wearecurio.services

import groovyx.net.http.Method
import us.wearecurio.BaseIntegrationSpec
import us.wearecurio.model.PubSubNotification
import us.wearecurio.model.SummaryData
import us.wearecurio.model.SummaryDataType
import us.wearecurio.oauth.Client
import us.wearecurio.oauth.ClientEnvironment
import us.wearecurio.utility.Utils

class PubSubNotificationServiceSpec extends BaseIntegrationSpec {

	PubSubNotificationService pubSubNotificationService

	void "test triggerPubSubNotification when it fails to notify the client server"() {
		pubSubNotificationService.httpService = [ performRequest: { String requestURL, Method method, Map args ->
			def result
			result.getMetaClass().isSuccess = {return false}
			result.getMetaClass().getCode = {return 400}
			return result
		}] as HttpService

		given: "Two notification instances and one client instance"
		Client clientInstance = createClient()
		PubSubNotification exercisePubSubNotificationInstance = createNotification(userInstance,
				SummaryDataType.EXERCISE, clientInstance, [date: ((new Date(1441195200l * 1000)).clearTime())])
		PubSubNotification activityPubSubNotificationInstance = createNotification(userInstance,
				SummaryDataType.ACTIVITY, clientInstance, [date: ((new Date(1441195200l * 1000)).clearTime())])

		assert exercisePubSubNotificationInstance.attemptCount == 0
		assert activityPubSubNotificationInstance.attemptCount == 0
		assert !exercisePubSubNotificationInstance.lastAttempted
		assert !activityPubSubNotificationInstance.lastAttempted

		when: "Notifications exist with 0 attempt count"
		pubSubNotificationService.triggerPubSubNotification()

		then: "Attempt count should be incremented and lastAttempted should not be null"
		exercisePubSubNotificationInstance.attemptCount == 1
		exercisePubSubNotificationInstance.lastAttempted

		activityPubSubNotificationInstance.attemptCount == 1
		activityPubSubNotificationInstance.lastAttempted
	}

	void "test triggerPubSubNotification when it succeeds to notify the client server"() {
		pubSubNotificationService.httpService = [ performRequest: { String requestURL, Method method, Map args ->
			def result
			result.getMetaClass().isSuccess = {return true}
			result.getMetaClass().getCode = {return 204}
			return result
		}] as HttpService

		given: "Two notification instances and one client instance"
		Client clientInstance = createClient()
		PubSubNotification exercisePubSubNotificationInstance = createNotification(userInstance,
				SummaryDataType.EXERCISE, clientInstance, [date: ((new Date(1441195200l * 1000)).clearTime())])
		PubSubNotification activityPubSubNotificationInstance = createNotification(userInstance,
				SummaryDataType.ACTIVITY, clientInstance, [date: ((new Date(1441195200l * 1000)).clearTime())])

		assert exercisePubSubNotificationInstance.attemptCount == 0
		assert activityPubSubNotificationInstance.attemptCount == 0
		assert !exercisePubSubNotificationInstance.sent
		assert !exercisePubSubNotificationInstance.lastAttempted
		assert !activityPubSubNotificationInstance.lastAttempted
		assert !activityPubSubNotificationInstance.sent

		when: "Notifications exist with 0 attempt count"
		pubSubNotificationService.triggerPubSubNotification()

		then: "Attempt count should not be incremented, lastAttempted should be null and sent should be true"
		exercisePubSubNotificationInstance.attemptCount == 0
		!exercisePubSubNotificationInstance.lastAttempted
		exercisePubSubNotificationInstance.sent

		activityPubSubNotificationInstance.attemptCount == 0
		!activityPubSubNotificationInstance.lastAttempted
		activityPubSubNotificationInstance.sent
	}

	void "test triggerPubSubNotification when one of the notification is expired"() {
		pubSubNotificationService.httpService = [ performRequest: { String requestURL, Method method, Map args ->
			def result
			result.getMetaClass().isSuccess = {return true}
			result.getMetaClass().getCode = {return 204}
			return result
		}] as HttpService

		given: "Two notification instances and one client instance"
		Client clientInstance = createClient()
		PubSubNotification exercisePubSubNotificationInstance = createNotification(userInstance,
				SummaryDataType.EXERCISE, clientInstance, [date: (new Date(1441195200l * 1000)).clearTime()])
		PubSubNotification activityPubSubNotificationInstance = createNotification(userInstance,
				SummaryDataType.ACTIVITY, clientInstance, [date: (new Date(1441195200l * 1000)).clearTime()])

		PubSubNotification sleepPubSubNotificationInstance = createNotification(userInstance,
				SummaryDataType.SLEEP, clientInstance, [date: (new Date(1441195200l * 1000)).clearTime(),
				sent: false, attemptCount: 3, lastAttempted: (new Date(1442195200l * 1000))])

		assert exercisePubSubNotificationInstance.attemptCount == 0
		assert activityPubSubNotificationInstance.attemptCount == 0
		assert !exercisePubSubNotificationInstance.sent
		assert !exercisePubSubNotificationInstance.lastAttempted
		assert !activityPubSubNotificationInstance.lastAttempted
		assert !activityPubSubNotificationInstance.sent
		assert sleepPubSubNotificationInstance.attemptCount == 3
		assert sleepPubSubNotificationInstance.lastAttempted == (new Date(1442195200l * 1000))

		when: "One notifications is expired"
		pubSubNotificationService.triggerPubSubNotification()

		then: "expired notification should not be processed"
		exercisePubSubNotificationInstance.attemptCount == 0
		!exercisePubSubNotificationInstance.lastAttempted
		exercisePubSubNotificationInstance.sent

		activityPubSubNotificationInstance.attemptCount == 0
		!activityPubSubNotificationInstance.lastAttempted
		activityPubSubNotificationInstance.sent

		sleepPubSubNotificationInstance.lastAttempted == (new Date(1442195200l * 1000))
		sleepPubSubNotificationInstance.attemptCount == 3
	}

	void "test creating notifications for current environment based clients only"() {
		given: "Client with test environment and no hook URL"
		Client clientInstance1 = new Client(name: "Oura Cloud Mobile App", clientId: "client1", authorizedGrantTypes:
				["password"], authorities: ["ROLE_CLIENT"], scopes: ["read", "write"],
				environment: ClientEnvironment.TEST)
		assert Utils.save(clientInstance1) == true

		and: "Client with test environment and with hook URL"
		Client clientInstance2 = new Client(name: "Curious Test", clientId: "client2", authorizedGrantTypes:
				["password"], authorities: ["ROLE_CLIENT"], scopes: ["read", "write"],
				clientHookURL: "http://example.com/home/notifyOura",
				environment: ClientEnvironment.TEST)
		assert Utils.save(clientInstance2) == true

		and: "Client with production environment and with hook URL"
		Client clientInstance3 = new Client(name: "Curious", clientId: "client3", authorizedGrantTypes:
				["password"], authorities: ["ROLE_CLIENT"], scopes: ["read", "write"],
				clientHookURL: "http://example.com/home/notifyOura",
				environment: ClientEnvironment.PRODUCTION)
		assert Utils.save(clientInstance3) == true

		and: "Client with development environment and with hook URL"
		Client clientInstance4 = new Client(name: "Curious Dev", clientId: "client4", authorizedGrantTypes:
				["password"], authorities: ["ROLE_CLIENT"], scopes: ["read", "write"],
				clientHookURL: "http://example.com/home/notifyOura",
				environment: ClientEnvironment.DEVELOPMENT)
		assert Utils.save(clientInstance4, true) == true

		assert Client.count() == 4

		and: "One SummaryData instance"
		SummaryData summaryDataInstance = new SummaryData([eventTime: 1451377237, user: userInstance, type:
				SummaryDataType.SLEEP, timeZone: "null"])
		assert Utils.save(summaryDataInstance, true) == true

		when: "Notifications are created for this instance"
		pubSubNotificationService.createPubSubNotification(userInstance, summaryDataInstance)

		then: "Only one notification should be created for the second client"
		PubSubNotification.count() == 1
		PubSubNotification pubSubNotificationInstance = PubSubNotification.first()
		pubSubNotificationInstance.client.id == clientInstance2.id
	}
}
