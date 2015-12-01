package us.wearecurio.services

import grails.converters.JSON
import grails.util.Holders
import us.wearecurio.model.PubSubNotification

class PubSubNotificationService {

	HttpBuilderService httpBuilderService

	void triggerPubSubNotification() {
		Date fiveMinutesAgo = new Date (new Date().getTime() - (5 * 60 * 1000))
		List<PubSubNotification> pubSubNotificationInstanceList = PubSubNotification.withCriteria {
				and {
					eq "sent", false
					lt "attemptCount", 3
					or {
						gt "lastAttempted", fiveMinutesAgo
						isNull "lastAttempted"
					}
				}
			}

		pubSubNotificationInstanceList.findAll {
			def response = httpBuilderService.performRestRequest(Holders.grailsApplication.config.curiousServerURL + "/home/notifyOura",
					"POST", [body : new JSON([type: it.type, date: it.date, userId: it.user?.id]).toString()])

			if (response.isSuccess()) {
				it.sent = true
				it.save()
			} else {
				it.attemptCount++
				it.lastAttempted = new Date()
				it.save()
			}
		}

	}
}
