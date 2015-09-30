package us.wearecurio.services

import grails.transaction.Transactional
import org.springframework.context.MessageSource
import us.wearecurio.model.SummaryData
import us.wearecurio.model.SummaryDataType
import us.wearecurio.users.User
import us.wearecurio.utility.Utils

/**
 * Grails service which is used to perform all operation related to summary data.
 * @since 0.0.1
 * @author Shashank Agrawal
 */
@Transactional
class DataService {

	/**
	 * List of keys which will be stored as {@link us.wearecurio.model.SummaryData#data data}
	 * in the {@link us.wearecurio.model.SummaryData SummaryData} domain
	 */
	static private List<String> ACTIVITY_DATA_KEYS = ["non_wear_m", "steps", "eq_meters", "active_cal", "total_cal"]
	static private List<String> EXERCISE_DATA_KEYS = ["duration_m", "classification"]
	static private List<String> SLEEP_DATA_KEYS = ["bedtime_m", "sleep_score", "awake_m", "rem_m", "light_m", "deep_m"]

	MessageSource messageSource

	/**
	 * Search an instance of {@link SummaryData} for given user and for given type with the provided <code>id</code>.
	 * The <code>id</code> can be either the Grails domain ID or the event timestamp in Unix timstamp format.
	 * @param userInstance Instance of {@link User} to search instance for
	 * @param id Either Grails domain id or data event timestamp
	 * @param type type of the summary data
	 * @return The instance of {@link SummaryData} as described above.
	 */
	SummaryData get(User userInstance, Long id, SummaryDataType type) {
		return SummaryData.withCriteria(uniqueResult: true) {
			eq("user", userInstance)
			eq("type", type)
			or {
				eq("eventTime", id)
				eq("id", id)
			}
		}
	}

	/**
	 * Helper method to convert a given event timestamp value (in Unix timestamp format) to the {@link Long} format.
	 * @param timestamp Timestamp value can be either in String or directly in long format so keeping type as {@ Object}
	 * @return Parsed/converted value in {@link Long}
	 * @throws NumberFormatException If value is not parseable to the Long
	 */
	Long getTimestamp(def timestamp) throws NumberFormatException {
		try {
			return (timestamp ?: "").toLong()
		} catch(NumberFormatException e) {
			String message = messageSource.getMessage("summary.data.invalid.timestamp", [timestamp] as Object[], null)
			throw new NumberFormatException(message)
		}
	}

	/**
	 * The generic action used to save instance of {@link us.wearecurio.model.SummaryData SummaryData} for given user
	 * of a given timestamp and the type. This method first checks if the system already have the summary data for
	 * the give type and it's timestamp. If the data already exists, it will update the same record otherwise creates
	 * a new record for the same.
	 *
	 * @param userInstance Instance of {@link us.wearecurio.users.User User} to save data for
	 * @param eventTime Timestamp of the event summary in UTC
	 * @param type Type of the event
	 * @param summaryData Summary data for the given event
	 * @return Instance of created or updated {@link us.wearecurio.model.SummaryData SummaryData}
	 */
	SummaryData save(User userInstance, Long eventTime, SummaryDataType type, Map summaryData) {
		log.debug "Saving $type data for $userInstance at event time $eventTime"

		// First search if a data for the same timestamp and type is already exists
		SummaryData summaryDataInstance = get(userInstance, eventTime, type)

		if (!summaryDataInstance) {
			log.debug "No existing record found for $eventTime and $type"
			summaryDataInstance = new SummaryData([user: userInstance, type: type, eventTime: eventTime])
		} else {
			log.debug "Existing $summaryDataInstance found"
		}

		return update(summaryDataInstance, summaryData)
	}

	SummaryData update(SummaryData summaryDataInstance, Map summaryData) {
		// Dynamically get the above defined constant name based on the summary data type
		List<String> extraDataKeys = this[summaryDataInstance.type.name() + "_DATA_KEYS"]

		Map data = extraDataKeys.collectEntries { [(it): summaryData[it]] }

		summaryDataInstance.properties = [data: data, timeZone: summaryData["time_zone"].toString()]
		Utils.save(summaryDataInstance)

		return summaryDataInstance
	}

	SummaryData saveActivityData(User userInstance, Map data) {
		log.debug "Save activity data $data for $userInstance"
		return save(userInstance, getTimestamp(data["time_utc"]), SummaryDataType.ACTIVITY, data)
	}

	SummaryData saveExerciseData(User userInstance, Map data) {
		log.debug "Save exercise data $data for $userInstance"
		return save(userInstance, getTimestamp(data["start_time_utc"]), SummaryDataType.EXERCISE, data)
	}

	SummaryData saveSleepData(User userInstance, Map data) {
		log.debug "Save sleep data $data for $userInstance"
		return save(userInstance, getTimestamp(data["bedtime_start_utc"]), SummaryDataType.SLEEP, data)
	}

	void sync(User userInstance, Map data) {
		log.debug "$userInstance sync data with $data"

		data["activity_summary"].each { Map summaryData ->
			saveActivityData(userInstance, summaryData)
		}

		data["exercise_summary"].each { Map summaryData ->
			saveExerciseData(userInstance, summaryData)
		}

		data["sleep_summary"].each { Map summaryData ->
			saveSleepData(userInstance, summaryData)
		}
	}
}