package us.wearecurio.services

import grails.converters.JSON
import groovyx.net.http.ContentType
import groovyx.net.http.Method
import us.wearecurio.model.PubSubNotification
import us.wearecurio.model.SummaryData
import us.wearecurio.oauth.Client
import us.wearecurio.users.User

class PubSubNotificationService {

	HttpService httpService

	static Map<String, String> checkBucket

	// Max bucket size is 1000
	static {
		checkBucket = new LinkedHashMap() {
			@Override
			protected boolean removeEldestEntry(Map.Entry eldest) {
				return size() > 1000
			}
		}
	}

	void triggerPubSubNotification() {
		Date fiveMinutesAgo = new Date (new Date().getTime() - (5 * 60 * 1000000))
		List<PubSubNotification> pubSubNotificationInstanceList = PubSubNotification.withCriteria {
			and {
				eq "sent", false
				lt "attemptCount", 3
				or {
					gt "lastAttempted", fiveMinutesAgo
					isNull "lastAttempted"
					eq("lastAttempted", [$exists: false])
				}
			}
		}

		List<Client> clientInstanceList = Client.getAll()
		pubSubNotificationInstanceList.each { pubSubNotificationInstance ->
			if (!checkBucket.containsKey(pubSubNotificationInstance.id)) {
				log.debug "Sending $pubSubNotificationInstance"
				checkBucket[pubSubNotificationInstance.id] = true

				clientInstanceList.each { clientInstance ->
					String body = new JSON([type: pubSubNotificationInstance.type.toString().toLowerCase(),
							date: pubSubNotificationInstance.date, userId: pubSubNotificationInstance.user?.id]).toString()

					def response = httpService.performRequest(clientInstance.clientHookURL,
							Method.POST, [body : body, requestContentType: ContentType.JSON, headers: ["Accept": "application/json"]])

					if (response.isSuccess() && (response.getCode() == 204)) {
						pubSubNotificationInstance.sent = true
					} else {
						pubSubNotificationInstance.attemptCount++
						pubSubNotificationInstance.lastAttempted = new Date()
					}
					pubSubNotificationInstance.save(flush: true)
					checkBucket.remove(pubSubNotificationInstance.id)
				}
			}
		}
	}

	void createPubSubNotification(User userInstance, SummaryData summaryDataInstance) {
		Date eventClearDate = (new Date(summaryDataInstance.eventTime * 1000)).clearTime()
		List<PubSubNotification> pubSubNotificationInstances = PubSubNotification.withCriteria {
			eq ("user", userInstance)
			eq ("date", eventClearDate)
			eq ("type", summaryDataInstance.type)
			eq ("sent", false)
		}

		if (!pubSubNotificationInstances) {
			PubSubNotification pubSubNotificationInstance = new PubSubNotification([user: userInstance, type: summaryDataInstance.type,
					 date: eventClearDate])
			pubSubNotificationInstance.save(flush: true)
		}
	}
}
