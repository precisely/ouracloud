package us.wearecurio.services

import grails.converters.JSON
import grails.plugin.springsecurity.oauthprovider.GormTokenStoreService
import grails.util.Holders
import groovyx.net.http.Method
import us.wearecurio.model.PubSubNotification
import us.wearecurio.oauth.Client

class PubSubNotificationService {

	HttpService httpService

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

		// TODO: replace findAllWhere with getAll
		List<Client> clientInstanceList = Client.getAll()
		pubSubNotificationInstanceList.each { pubSubNotificationInstance ->
 			clientInstanceList.each { clientInstance ->
				def response = httpService.performRequest(clientInstance.clientServerURL + "/home/notifyOura",
						Method.POST, [body : new JSON([type: pubSubNotificationInstance.type.toString().toLowerCase(), date: pubSubNotificationInstance.date, userId: pubSubNotificationInstance.user?.id]).toString()])

				if (response.isSuccess()) {
					pubSubNotificationInstance.sent = true
					pubSubNotificationInstance.save()
				} else {
					pubSubNotificationInstance.attemptCount++
					pubSubNotificationInstance.lastAttempted = new Date()
					pubSubNotificationInstance.save()
				}
			}
		}

	}
}
