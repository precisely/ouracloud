package us.wearecurio.model

import us.wearecurio.users.User

import java.util.concurrent.TimeUnit

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
		processAfterLaunch nullable: true
	}

	/**
	 * This is used to store uncommon data for all types i.e. type specific data like awake minute for sleep event.
	 * This might be changed to the different extending domain classes for better data organization. (See domain
	 * level documentation for more detail)
	 */
	Map<String, Object> data = [:]

	/**
	 * Date when the a particular record is created. This is non bindable by default.
	 * @see "Automatic timestamping in  http://grails.github.io/grails-doc/2.5.0/guide/GORM.html#eventsAutoTimestamping"
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
	 * @see "Automatic timestamping in  http://grails.github.io/grails-doc/2.5.0/guide/GORM.html#eventsAutoTimestamping"
	 */
	Date lastUpdated

	SummaryDataType type

	String timeZone

	User user

	Boolean processAfterLaunch

	@Override
	String toString() {
		return "SummaryData{id=$id, type=$type}"
	}

	def beforeUpdate() {
		cleanupTimeZone()
	}

	def beforeInsert() {
		cleanupTimeZone()
	}

	void cleanupTimeZone() {
		if (!timeZone) {
			return
		}

		// Some of the data receives timezone as "null" value
		if (timeZone == "null") {
			this.timeZone = ""
			return
		}

		// Timezone of the location where event was registered, e.g. "-2.5" means UTC minus two point half hours
		if (timeZone.isNumber()) {
			// Then we need to convert it into the Java/Joda timezone readable format like "-02:30"

			long millis = timeZone.toDouble() * 60 * 60 * 1000
			long hours = TimeUnit.MILLISECONDS.toHours(millis)
			long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1)
			char sign = ((hours >= 0) && (minutes >= 0)) ? "+" : "-"

			this.timeZone = String.format("%s%02d:%02d", sign, Math.abs(hours), Math.abs(minutes))
		}
	}
}

enum SummaryDataType {

	ACTIVITY(1, "activity_summary", "time_utc"),
	EXERCISE(2, "exercise_summary", "start_time_utc"),
	SLEEP(3, "sleep_summary", "bedtime_start_utc"),
	UNKNOWN(4, "", "")

	final int id
	// The key name which contains the list of data for a specific type coming from the Oura ring
	final String listDataKey
	// The key name which contains the timestamp of the event for a specific event data
	final String eventTimeKey

	SummaryDataType(int id, String listDataKey, String eventTimeKey) {
		this.id = id
		this.listDataKey = listDataKey
		this.eventTimeKey = eventTimeKey
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