package us.wearecurio

import grails.test.spock.IntegrationSpec
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.springframework.context.MessageSource
import spock.lang.Shared
import us.wearecurio.users.User
import us.wearecurio.users.UserService

class BaseIntegrationSpec extends IntegrationSpec {

	@Shared GrailsApplication grailsApplication
	MessageSource messageSource
	UserService userService

	User userInstance

	private static List domainList

	/**
	 * This method run only once before executing a test class where this class has been extended.
	 */
	def setupSpec() {
		domainList = grailsApplication.domainClasses*.clazz
	}

	def setup() {
		// Workaround to cleanup MongoDB before any test starts since MongoDB does not support rollback.
		domainList.each {
			it.collection.remove([:])   // Using MongoDB's lower level call to delete records for faster deletion
		}

		userInstance = createUser(1)
	}

	def cleanup() {
	}

	User createUser(int i, Map args = [:]) {
		Map defaultArgs = [username: "testuser$i", email: "testuser$i@ouraring.com", password: "12345$i"]

		args = defaultArgs + args		// Merge the incoming arguments with the default one
		User userInstance = userService.create(args)
		assert userInstance.id != null
		return userInstance
	}

	String resolveMessage(String code, List args) {
		return messageSource.getMessage(code, args as Object[], null)
	}

	/**
	 * Utility method to flush the current session so that all the data written via the source code flushed to the
	 * database. This method is useful when the main source code are not flushing the instance and we have to verify
	 * the values in our test cases.
	 */
	void flushSession() {
		// Workaround to flush the current session for MongoDB by flushing an existing instance
		userInstance.save(flush: true)
	}
}