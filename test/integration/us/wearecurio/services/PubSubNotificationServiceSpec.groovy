package us.wearecurio.services

import us.wearecurio.BaseIntegrationSpec
import us.wearecurio.model.PubSubNotification
import us.wearecurio.model.SummaryDataType
import us.wearecurio.oauth.Client

import groovyx.net.http.Method

class PubSubNotificationServiceSpec extends BaseIntegrationSpec {

	PubSubNotificationService pubSubNotificationService
	PubSubNotification exercisePubSubNotificationInstance
	PubSubNotification activityPubSubNotificationInstance

	def setup() {
		Client clientInstance = new Client([clientId: "client-id", clientSecret: "secret-key",
				clientServerURL: "localhost:8080", name: "test-app", clientHookURL: "localhost:8080"])
		clientInstance.save()
		exercisePubSubNotificationInstance = new PubSubNotification([user: userInstance, type: SummaryDataType.EXERCISE,
				 date: ((new Date(1441195200l * 1000)).clearTime()), client: clientInstance])
		exercisePubSubNotificationInstance.save()

		activityPubSubNotificationInstance = new PubSubNotification([user: userInstance, type: SummaryDataType.ACTIVITY,
				 date: ((new Date(1441195200l * 1000)).clearTime()), client: clientInstance])
		activityPubSubNotificationInstance.save()
	}

	void "test triggerPubSubNotification when it fails to notify the client server"() {
		pubSubNotificationService.httpService = [ performRequest: { String requestURL, Method method, Map args ->
			def result
			result.getMetaClass().isSuccess = {return false}
			result.getMetaClass().getCode = {return 400}
			return result
		}] as HttpService

		given: "Two notification instances and one client instance"
		flushSession()

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
		flushSession()

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
		PubSubNotification sleepPubSubNotificationInstance = new PubSubNotification([user: userInstance, type: SummaryDataType.SLEEP,
				date: ((new Date(1441195200l * 1000)).clearTime()), sent: false, attemptCount: 3, lastAttempted: (new Date(1442195200l * 1000))])
		sleepPubSubNotificationInstance.save()
		flushSession()

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
}
