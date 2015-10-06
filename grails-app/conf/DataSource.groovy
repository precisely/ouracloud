grails {
	mongo {
		host = "localhost"
		port = 27017
		databaseName = "ouracloud_dev"
		username = "ouracloud"
		password = "aeb0jhg0j83bru"
		options {
			autoConnectRetry = true
			connectTimeout = 3000
			connectionsPerHost = 40
			socketTimeout = 120000
			threadsAllowedToBlockForConnectionMultiplier = 5
			maxAutoConnectRetryTime=5
			maxWaitTime=120000
		}
	}
}

environments {
	test {
		grails {
			mongo {
				databaseName = "ouracloud_test"
			}
		}
	}
	production {
		grails {
			mongo {
				databaseName = "ouracloud"
			}
		}
	}
}