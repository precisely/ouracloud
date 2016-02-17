// Need to update this local config to use mongo configuration

dataSource {
	url = "jdbc:mysql://curiousdb/DBNAME?zeroDateTimeBehavior=convertToNull"
	username = "DBUSERNAME"
	password = "DBPASSWORD"
}
grails.serverURL = "https://APPHOSTNAME/"

grails {
	mail {
		host = "email-smtp.REGION.amazonaws.com"
		port = 587
		username = "SMTP_USERNAME"
		password = "SMTP_PASSWORD"
	}
}