package us.wearecurio.model

import us.wearecurio.oauth.Client
import us.wearecurio.users.User

class PubSubNotification {

	static constraints = {
		lastAttempted nullable: true
	}

	/**
	 * Date when the a particular record is created. This is non bindable by default.
	 * @see Automatic timestamping in  http://grails.github.io/grails-doc/2.5.0/guide/GORM.html#eventsAutoTimestamping
	 */
	Date dateCreated

	/**
	 * Date when the a particular record is last updated. This is non bindable by default.
	 * @see Automatic timestamping in  http://grails.github.io/grails-doc/2.5.0/guide/GORM.html#eventsAutoTimestamping
	 */
	Date lastUpdated

	User user
	SummaryDataType type
	Date date
	boolean sent
	int attemptCount
	Date lastAttempted
	Client client

	String toString() {
		"PubSubNotification{id=$id type=$type}"
	}
}
