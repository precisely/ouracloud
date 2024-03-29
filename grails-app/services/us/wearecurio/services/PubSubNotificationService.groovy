package us.wearecurio.services

import grails.converters.JSON
import groovyx.net.http.ContentType
import groovyx.net.http.Method
import us.wearecurio.model.PubSubNotification
import us.wearecurio.model.SummaryData
import us.wearecurio.model.SummaryDataType
import us.wearecurio.oauth.Client
import us.wearecurio.oauth.ClientEnvironment
import us.wearecurio.users.User
import us.wearecurio.utility.Utils

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
			eq("sent", false)
			lt("attemptCount", 3)
			or {
				gt("lastAttempted", fiveMinutesAgo)
				isNull("lastAttempted")
				eq("lastAttempted", [$exists: false])
			}
		}

		pubSubNotificationInstanceList.each { pubSubNotificationInstance ->
			if (!checkBucket.containsKey(pubSubNotificationInstance.id)) {
				log.debug "Sending $pubSubNotificationInstance"
				checkBucket[pubSubNotificationInstance.id] = true

				Client clientInstance = pubSubNotificationInstance.client

				/*
				 * Although, notifications are not created for Clients other than current environment but this is an
				 * edge condition check to prevent sending notification to client with different environment when the
				 * production data dump may be restored locally or to different server.
				 */
				if (clientInstance.environment != ClientEnvironment.getCurrent()) {
					log.warn "Found notification for $clientInstance with different environment"
					return
				}

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

				Utils.save(pubSubNotificationInstance, true)
				checkBucket.remove(pubSubNotificationInstance.id)
			}
		}
	}

	void createPubSubNotification(User userInstance, SummaryData summaryDataInstance) {
		if (summaryDataInstance.type != SummaryDataType.SLEEP) {
			return
		}

		Date eventClearDate = (new Date(summaryDataInstance.eventTime * 1000)).clearTime()
		// TODO Reverse the query based on the client later to avoid increasing number of instances
		List<PubSubNotification> pubSubNotificationInstances = PubSubNotification.withCriteria {
			eq ("user", userInstance)
			eq ("date", eventClearDate)
			eq ("type", summaryDataInstance.type)
			eq ("sent", false)
		}

		List<Client> clientInstanceList = Client.withCriteria {
			eq("clientHookURL", [$exists: true])
			isNotNull("clientHookURL")
			// Only create notification for clients registered for current environment
			eq("environment", ClientEnvironment.getCurrent())
		}

		clientInstanceList.each { clientInstance ->
			PubSubNotification pubSubNotificationInstance =  pubSubNotificationInstances.find {
				it.client == clientInstance
			}

			if (!pubSubNotificationInstance) {
				log.debug "Creating notification for $summaryDataInstance for $clientInstance"
				pubSubNotificationInstance = new PubSubNotification([user: userInstance, type: summaryDataInstance.type,
						date: eventClearDate, client: clientInstance])
				Utils.save(pubSubNotificationInstance, true)
			}
		}
	}
}
