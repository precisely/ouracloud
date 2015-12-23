package us.wearecurio.model

import us.wearecurio.users.User

/**
 * Domain class used for storing all the summary data for different events that have been accumulated from the Oura
 * Ring device. Currently supported events are "activity", "exercise" and "sleep".
 * All common data from the different types of the events are stored directly in this domain as top level fields and
 * the rest of the additional data are stored as a map of data in this domain class.
 *
 * @since 0.0.1
 * @author Shashank Agrawal
 * @see {@link us.wearecurio.model.SummaryDataType SummaryDataType} for types of events
 * @see {@link us.wearecurio.services.DataType DataType} for usage
 */
class SummaryData {

	static constraints = {
		eventTime min: 0l, index: true		// Index the columns in MongoDB for faster search
		type index: true
		user index: true
	}

	/**
	 * This is used to store uncommon data for all types i.e. type specific data like awake minute for sleep event.
	 * This might be changed to the different extending domain classes for better data organization. (See domain
	 * level documentation for more detail)
	 */
	Map<String, Object> data = [:]

	/**
	 * Date when the a particular record is created. This is non bindable by default.
	 * @see Automatic timestamping in  http://grails.github.io/grails-doc/2.5.0/guide/GORM.html#eventsAutoTimestamping
	 */
	Date dateCreated

	/**
	 * Unix timestamp sent by the Oura app for the event. Considering this field as a unique timestamp field
	 * for a particular {@link SummaryDataType type}.
	 *  These timestamps are EPOCH time in seconds http://stackoverflow.com/a/4676213/2405040
	 */
	Long eventTime

	/**
	 * Date when the a particular record is last updated. This is non bindable by default.
	 * @see Automatic timestamping in  http://grails.github.io/grails-doc/2.5.0/guide/GORM.html#eventsAutoTimestamping
	 */
	Date lastUpdated

	SummaryDataType type

	String timeZone

	User user

	@Override
	String toString() {
		return "SummaryData{id=$id, type=$type}"
	}
}

enum SummaryDataType {

	ACTIVITY(1),
	EXERCISE(2),
	SLEEP(3)

	final int id

	SummaryDataType(int id) {
		this.id = id
	}

	static SummaryDataType lookup(String name) {
		try {
			return SummaryDataType.valueOf(name?.toUpperCase() ?: "")
		} catch (IllegalArgumentException e) {
			String message = "Invalid data type. Allowed values are " + this.values()*.name().join(", ")
			throw new IllegalArgumentException(message)
		}
	}
}