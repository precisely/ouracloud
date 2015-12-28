package us.wearecurio.model

import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.authentication.encoding.PBKDF2PasswordEncoder
import grails.test.mixin.Mock
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import spock.lang.Specification
import us.wearecurio.services.SecurityService
import us.wearecurio.users.User
/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@Mock([SummaryData, User, SecurityService, SpringSecurityService])
@TestMixin(GrailsUnitTestMixin)
class SummaryDataSpec extends Specification {

	static doWithSpring = {
		passwordEncoder(PBKDF2PasswordEncoder)
	}

	User userInstance

	def setup() {
		userInstance = new User([email: "testuser@ouraring.com", password: "xyz", username: "testuser@ouraring.com"])
		userInstance.save(flush: true)
	}

	void "test cleanup timezone for null string"() {
		when: "A new summary data is saved with timezone as the null string"
		SummaryData summaryDataInstance = new SummaryData([eventTime: 1451377237, user: userInstance, type:
				SummaryDataType.ACTIVITY, timeZone: "null"])
		summaryDataInstance.save(flush: true)

		then: "Timezone should become null"
		summaryDataInstance.timeZone == ""
	}

	void "test cleanup timezone with positive number of hours"() {
		when: "A new summary data is saved"
		SummaryData summaryDataInstance = new SummaryData([eventTime: 1451377237, user: userInstance, type:
				SummaryDataType.ACTIVITY, timeZone: "+2.5"])
		summaryDataInstance.save(flush: true)

		then: "Timezone should be formatted"
		summaryDataInstance.timeZone == "+02:30"

		when: "Another timezone string is saved with space and without the positive sign"
		summaryDataInstance.timeZone = " 2.1"
		summaryDataInstance.save(flush: true)

		then: "Timezone should be be formatted"
		summaryDataInstance.timeZone == "+02:06"

		when: "Another timezone string is saved"
		summaryDataInstance.timeZone = "+2.99"
		summaryDataInstance.save(flush: true)

		then: "Timezone should be be formatted"
		summaryDataInstance.timeZone == "+02:59"

		when: "Another timezone string with zero amount"
		summaryDataInstance.timeZone = "+0.0"
		summaryDataInstance.save(flush: true)

		then: "Timezone should be be formatted"
		summaryDataInstance.timeZone == "+00:00"
	}

	void "test cleanup timezone with negative number of hours"() {
		when: "A new summary data is saved"
		SummaryData summaryDataInstance = new SummaryData([eventTime: 1451377237, user: userInstance, type:
				SummaryDataType.ACTIVITY, timeZone: "-0.5"])
		summaryDataInstance.save(flush: true)

		then: "Timezone should be formatted"
		summaryDataInstance.timeZone == "-00:30"

		when: "Another timezone string is saved with space"
		summaryDataInstance.timeZone = " -2"
		summaryDataInstance.save(flush: true)

		then: "Timezone should be be formatted"
		summaryDataInstance.timeZone == "-02:00"

		when: "Another timezone string is saved with trailing zero"
		summaryDataInstance.timeZone = "-2.0"
		summaryDataInstance.save(flush: true)

		then: "Timezone should be be formatted"
		summaryDataInstance.timeZone == "-02:00"
	}
}