package us.wearecurio.services

import grails.transaction.Transactional
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

	static private List<String> ACTIVITY_DATA_KEYS = ["non_wear_m", "steps", "eq_meters", "active_cal", "total_cal"]
	static private List<String> EXERCISE_DATA_KEYS = ["duration_m", "classification"]
	static private List<String> SLEEP_DATA_KEYS = ["bedtime_m", "sleep_score", "awake_m", "rem_m", "light_m", "deep_m"]

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
	 * @param extraDataKeys List of keys which will be stored as {@link us.wearecurio.model.SummaryData#data data}
	 * in the {@link us.wearecurio.model.SummaryData SummaryData} domain
	 * @return Instance of created or updated {@link us.wearecurio.model.SummaryData SummaryData}
	 */
	SummaryData saveData(User userInstance, Long eventTime, SummaryDataType type, Map summaryData, List<String>
			extraDataKeys) {
		log.debug "Saving $type data for $userInstance at event time $eventTime"

		// First search if a data for the same timestamp and type is already exists
		SummaryData summaryDataInstance = SummaryData.withCriteria(uniqueResult: true) {
			eq("user", userInstance)
			eq("type", type)
			eq("eventTime", eventTime)
		}

		if (!summaryDataInstance) {
			log.debug "No existing record found for $eventTime and $type"
			summaryDataInstance = new SummaryData([user: userInstance, type: type, eventTime: eventTime])
		}

		Map data = extraDataKeys.collectEntries { [(it): summaryData[it]] }

		summaryDataInstance.properties = [data: data, timeZone: summaryData["time_zone"]]
		Utils.save(summaryDataInstance)

		return summaryDataInstance
	}

	SummaryData saveActivityData(User userInstance, Map data) {
		log.debug "Save activity data $data for $userInstance"
		return saveData(userInstance, data["time_utc"].toLong(), SummaryDataType.ACTIVITY, data, ACTIVITY_DATA_KEYS)
	}

	SummaryData saveExerciseData(User userInstance, Map data) {
		log.debug "Save exercise data $data for $userInstance"
		return saveData(userInstance, data["start_time_utc"].toLong(), SummaryDataType.ACTIVITY, data, EXERCISE_DATA_KEYS)
	}

	SummaryData saveSleepData(User userInstance, Map data) {
		log.debug "Save sleep data $data for $userInstance"
		return saveData(userInstance, data["bedtime_start_utc"].toLong(), SummaryDataType.ACTIVITY, data, SLEEP_DATA_KEYS)
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