package us.wearecurio.model

import us.wearecurio.users.User

class PubSubNotification {

	static constraints = {
		lastAttempted nullable: true
	}

	User user
	SummaryDataType type
	Date date
	boolean sent
	int attemptCount
	Date lastAttempted
}
