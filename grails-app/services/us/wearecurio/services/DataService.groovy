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

	// Min EPOCH time after which Oura ring has started posting the data
	static private Long MIN_START_EPOCH_TIME = new Date("01/01/2015").time / 1000

	MessageSource messageSource

	PubSubNotificationService pubSubNotificationService
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

	SummaryData save(User userInstance, SummaryDataType type, Map<String, Object> summaryData) {
		Object timestamp = summaryData.get(type.eventTimeKey)
		return save(userInstance, getTimestamp(timestamp), type, summaryData)
	}

	SummaryData update(SummaryData summaryDataInstance, Map summaryData) {
		Map data = getAdditionalData(summaryData, summaryDataInstance.type)

		List<SummaryDataType> processLaterTypes = [SummaryDataType.ACTIVITY, SummaryDataType.EXERCISE]
		Boolean processAfterLaunch = processLaterTypes.contains(summaryDataInstance.type) ? true : null

		summaryDataInstance.properties = [data: data, timeZone: summaryData["time_zone"].toString(),
				processAfterLaunch: processAfterLaunch]
		Utils.save(summaryDataInstance)

		return summaryDataInstance
	}

	/**
	 * Get the additional data which needs to be stored as {@link us.wearecurio.model.SummaryData#data data}
	 * in SummaryData domain which is basically all the incoming data from the Oura ring excluding the timezone
	 * and the time stamp values.
	 * @param summaryData Raw summary data from the Oura ring
	 * @param type type of summary data
	 * @return additional data as described
	 */
	Map<String, Object> getAdditionalData(Map summaryData, SummaryDataType type) {
		List<String> excludeKeys = ["time_zone", type.eventTimeKey]

		// Collect all data excluding time zone and the event time field since they are stored as separate field
		return summaryData.findAll { !excludeKeys.contains(it.key) }
	}

	SummaryData saveActivityData(User userInstance, Map data) {
		log.debug "Save activity data $data for $userInstance"
		return save(userInstance, SummaryDataType.ACTIVITY, data)
	}

	SummaryData saveExerciseData(User userInstance, Map data) {
		log.debug "Save exercise data $data for $userInstance"
		return save(userInstance, SummaryDataType.EXERCISE, data)
	}

	SummaryData saveSleepData(User userInstance, Map data) {
		log.debug "Save sleep data $data for $userInstance"
		return save(userInstance, SummaryDataType.SLEEP, data)
	}

	SummaryData saveUnknownData(User userInstance, Map data) {
		log.debug "Save unknown data $data for $userInstance"
		Map.Entry timestampEntry = findOutTimestamp(data)

		// Remove the timestamp key from the map so that it doesn't get stored to the "data" in SummaryData
		Object timestamp = data.remove(timestampEntry.getKey())
		Long eventTime = getTimestamp(timestamp)
		SummaryDataType type = SummaryDataType.UNKNOWN

		// First search if a data for the same timestamp and type is already exists
		SummaryData summaryDataInstance = get(userInstance, eventTime, type)

		if (!summaryDataInstance) {
			log.debug "No existing record found for $eventTime and $type"
			summaryDataInstance = new SummaryData([user: userInstance, type: type, eventTime: eventTime])
		} else {
			log.debug "Existing unknown $summaryDataInstance found"
			List<String> newAdditionalDataKeys = getAdditionalData(data, type).keySet().sort()
			List<String> existingAdditionalDataKeys = summaryDataInstance.data.keySet().sort()

			/*
			 * There can be two types of unknown data which might come in the future i.e. temperature and heart.
			 * Currently, we don't have the data schema for both the new types so we are storing all the data in
			 * SummaryData based on the event time.
			 *
			 * When we search for existing record with type (i.e. unknown) and the event time, there might be a chance
			 * that the two heart & temperature records might have the same event time. So here, we are matching
			 * the existing keys to differentiate between heart & temperature.
			 */
			if (!newAdditionalDataKeys.equals(existingAdditionalDataKeys)) {
				summaryDataInstance = new SummaryData([user: userInstance, type: type, eventTime: eventTime])
			}
		}

		return update(summaryDataInstance, data)
	}

	/**
	 * Find that key which can be the timestamp by looking all values which are number and greater than the
	 * EPOCH value of "1st Jan 2015".
	 * @param summaryData The unknown summary data.
	 * @return The specific entry which has the timestamp value of the event
	 */
	Map.Entry findOutTimestamp(Map<String, Object> summaryData) {
		return summaryData.find { key, value ->
			return value && value.toString().isNumber() && (value.toLong() > MIN_START_EPOCH_TIME)
		}
	}

	List<SummaryData> sync(User userInstance, Map apiData) {
		log.debug "$userInstance sync data with $apiData"

		SummaryData summaryDataInstance
		List<SummaryData> summaryDataInstanceList = []

		List<String> knownSummaryDataKeys = [SummaryDataType.ACTIVITY, SummaryDataType.EXERCISE,
				SummaryDataType.SLEEP]*.listDataKey

		// Will be like "activity_summary", "exercise_summary", "sleep_summary"
		knownSummaryDataKeys.each { key ->
			apiData[key].each { Map summaryData ->
				if (key == SummaryDataType.ACTIVITY.listDataKey) {
					summaryDataInstance = saveActivityData(userInstance, summaryData)
				} else if (key == SummaryDataType.EXERCISE.listDataKey) {
					summaryDataInstance = saveExerciseData(userInstance, summaryData)
				} else if (key == SummaryDataType.SLEEP.listDataKey) {
					summaryDataInstance = saveSleepData(userInstance, summaryData)
				}

				if (summaryDataInstance && summaryDataInstance.hasErrors()) {
					summaryDataInstanceList << summaryDataInstance
				} else if (summaryDataInstance) {
					pubSubNotificationService.createPubSubNotification(userInstance, summaryDataInstance)
				}
			}
		}

		Set<String> allKeys = apiData.keySet()
		/*
		 * We need to find out if other data i.e. heart & temperature data started coming in from the Oura ring by
		 * checking other keys which ends with "_summary" and are not the known keys. This is required since
		 * user will sync the data only once and we can have the new data in our system and later we can write the
		 * migration to actually process the data.
		 */
		Set<String> unknownSummaryDataKeys = allKeys.findAll {
			it.endsWith("_summary") && !knownSummaryDataKeys.contains(it)
		}

		// Do not break known data sync if there is any inconsistency in unknown data
		try {
			unknownSummaryDataKeys.each { key ->
				if (apiData[key] && (apiData[key] instanceof List)) {
					apiData[key].each { Map summaryData ->
						// No PubSubNotifications should be created
						summaryDataInstance = saveUnknownData(userInstance, summaryData)

						if (summaryDataInstance && summaryDataInstance.hasErrors()) {
							summaryDataInstanceList << summaryDataInstance
						}
					}
				}
			}
		} catch(e) {
			log.error "Error saving unknown data", e
		}

		return summaryDataInstanceList
	}
}