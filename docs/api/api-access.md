# API Endpoints

Every request API call must have authentication token in the header:

```
Authorization:       Bearer 1d49fc35-2af6-477e-8fd4-ab0353a4a76f
```

## Data Endpoint

Events are identified by the event type (like activity, exercise, sleep etc) and the event timestamp. If a record 
already exists for the same event type and the event timestamp then the existing record will be updated.

### Sync all data

Endpoint used to store/update all the data received from the Oura app for authenticated user which should be available
in the request body as the JSON format.

**Request**    

**Request Method:** `POST`
**Request Header:** `Authorization`:    `Bearer 1d49fc35-2af6-477e-8fd4-ab0353a4a76f`
**Request URI:** `/api/sync`
**Request Body:**

```json
{
	"activity_summary": [
		{
			"time_utc": "1441195200",
			"time_zone": "2.5",
			"steps": "6551",
			"active_cal": "369",
		}
	],
	"exercise_summary": [
		{
			"start_time_utc": "1441213920",
			"time_zone": "2",
			"duration_m": "53",
			"classification": "moderate"
		}
	],
	"sleep_summary": [
		{
			"bedtime_start_utc": "1441151652",
			"time_zone": "5.5",
			"bedtime_m": "503",
			"sleep_score": "81",
			"awake_m": "10",
			"rem_m": "150",
			"light_m": "139",
			"deep_m": "234"
		}
	]
}
```

**Response**

**Response Success:**
**Response Code:** `200`

```json
[{
    "id": 1,
    "version": 0,
    "eventTime": 1441195200,
    "timeZone": "2.5",
    "dateCreated": "2015-10-19T12:11:20Z",
    "lastUpdated": "2015-10-19T12:11:20Z",
    "data": {"non_wear_m": null, "steps": "6551", "eq_meters": null, "active_cal": "369", "total_cal": null},
    "userID": 1,
    "type": "ACTIVITY"
}, {
    "id": 2,
    "version": 0,
    "eventTime": 1441213920,
    "timeZone": "2",
    "dateCreated": "2015-10-19T12:11:20Z",
    "lastUpdated": "2015-10-19T12:11:20Z",
    "data": {"duration_m": "53", "classification": "moderate"},
    "userID": 1,
    "type": "EXERCISE"
}, {
    "id": 3,
    "version": 0,
    "eventTime": 1441151652,
    "timeZone": "5.5",
    "dateCreated": "2015-10-19T12:11:20Z",
    "lastUpdated": "2015-10-19T12:11:20Z",
    "data": {
        "bedtime_m": "503",
        "sleep_score": "81",
        "awake_m": "10",
        "rem_m": "150",
        "light_m": "139",
        "deep_m": "234"
    },
    "userID": 1,
    "type": "SLEEP"
}]
```

**Response Failure:**
**Response Code:** `406`

When a unparseable JSON request body is passed:
```json
{
    "error": "Error parsing JSON",
    "error_description": "Unterminated string at character 99 of {\n\t\"activity_summary\": [\n\t\t{\n\t\t\t\"time_utc\": \"1441195200\",\n\t\t\t\"time_zone\": \"2.5\",\n\t\t\t\"steps\": \"6551\n\t\t}\n\t]\n\t\n}"
}
```

When a non number of invalid epoch timestamp value is passed for any event:
```json
{
    "error": "illegal argument",
    "error_description": "Invalid event timestamp value \"1441213920d\". It must be in Unix timestamp format."
}
```

When a validation fails for a particular record: (Other records will be persisted/updated)
```json
[{
    "id": 1,
    "version": 9,
    "eventTime": 1441195200,
    "timeZone": "2.5",
    "dateCreated": "2015-10-19T12:11:20Z",
    "lastUpdated": "2015-10-19T12:38:06Z",
    "data": {"non_wear_m": null, "steps": "6551", "eq_meters": null, "active_cal": "369", "total_cal": null},
    "userID": 1,
    "type": "ACTIVITY"
}, {
    "errors": [{
        "field": "eventTime",
        "rejected-value": -2,
        "message": "\"eventTime\" with value \"-2\" can not be less than a minimum value \"0\""
    }]
}]
```

### Get all data

Used to get a list of various data for authenticated user with some optional filters:

**Request**    

**Request Method:** `GET`
**Request URI:** `/api/<dataType>`
**Request Header:** `Authorization`:    `Bearer 1d49fc35-2af6-477e-8fd4-ab0353a4a76f`
**Request Parameters:**

Parameter Name | Description
-------------- | -----------
dataType   | (**REQUIRED**) Type of event (case insensitive). Available values: **activity**, **sleep**, **all**, **exercise** to get data for
timestamp      | (**OPTIONAL**) Get events data with event the given timestamp value
startTimestamp | (**OPTIONAL**) Get events data after the given timestamp
endTimestamp | (**OPTIONAL**) Get all the events data available before the given timestamp
max | (**OPTIONAL**) Limit the number of records with allowed maximum value of 100 (for pagination)
offset | (**OPTIONAL**) Skip the number of records (for pagination)

**Response**

**Response Success:**
**Response Code:** `200`

`GET		/api/all?timestamp=1441213920`

```json
{
    "instanceList": [
        {
            "id": 2,
            "version": 8,
            "eventTime": 1441213920,
            "timeZone": "2",
            "dateCreated": "2015-10-19T12:11:20Z",
            "lastUpdated": "2015-10-19T12:33:32Z",
            "data": {
                "duration_m": "53",
                "classification": "moderate"
            },
            "userID": 1,
            "type": "EXERCISE"
        }
    ],
    "totalCount": 1
}
```

`GET		/api/sleep?startTimestamp=1441151652&endTimestamp=1441213920`

```json
{
    "instanceList": [],
    "totalCount": 0
}
```

**Response Failure:**
**Response Code:** `406`

When a invalid data type is passed: `GET		/api/xyz?timstamp=1441213920`

```json
{
    "error": "illegal argument",
    "error_description": "Invalid data type. Allowed values are ACTIVITY, EXERCISE, SLEEP"
}
```