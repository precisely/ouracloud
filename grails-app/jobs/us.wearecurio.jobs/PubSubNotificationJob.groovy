package us.wearecurio.jobs

import us.wearecurio.services.PubSubNotificationService

class PubSubNotificationJob {

	PubSubNotificationService pubSubNotificationService

	// This job will run in every two minutes after a start delay of 2 seconds
	static triggers = {
		simple name: "PubSubNotification Trigger", startDelay: 2000, repeatInterval: 120000
	}

	def execute() {
		println "Running pubSubNotification job"
		pubSubNotificationService.triggerPubSubNotification()
	}
}