package us.wearecurio.jobs

import us.wearecurio.services.PubSubNotificationService

class PubSubNotificationJob {

	PubSubNotificationService pubSubNotificationService

	static triggers = {
		simple name: 'PubSubNotification Trigger', startDelay: 2000, repeatInterval: 10000
	}

	def execute() {
		println "Job run!"
		pubSubNotificationService.triggerPubSubNotification()
	}
}