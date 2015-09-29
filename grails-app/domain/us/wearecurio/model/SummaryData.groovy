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
		eventTime min: 0l
	}

	/**
	 * This is used to store uncommon data for all types i.e. type specific data like awake minute for sleep event.
	 * This might be changed to the different extending domain classes for better data organization. (See domain
	 * level documentation for more detail)
	 */
	Map<String, Object> data = [:]

	Long eventTime

	SummaryDataType type

	Integer timeZone

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
}