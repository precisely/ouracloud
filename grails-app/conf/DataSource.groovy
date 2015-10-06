grails {
	cassandra {
		port = 9042
		contactPoints = "localhost"
		dbCreate = "recreate-drop-unused"
		keyspace {
			action = "create"
		}
	}
}

// environment specific settings
environments {
	development {
		grails {
			cassandra {
				keyspace.name = "ouracloud_dev"
			}
		}
	}
	test {
		grails {
			cassandra {
				keyspace.name = "ouracloud_test"
			}
		}
	}
	production {
		grails {
			cassandra {
				keyspace.name = "ouracloud"
			}
		}
	}
}
