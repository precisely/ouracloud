package us.wearecurio

import grails.test.spock.IntegrationSpec
import org.springframework.context.MessageSource
import us.wearecurio.users.User

class BaseIntegrationSpec extends IntegrationSpec {

	MessageSource messageSource
	//SessionFactory sessionFactory

	User userInstance

	def setup() {
		userInstance = User.look("testuser", "xyz")
		assert userInstance.id != null
	}

	def cleanup() {
	}

	/**
	 * Utility method to flush the current session so that all the data written via the source code flushed to the
	 * database. This method is useful when the main source code are not flushing the instance and we have to verify
	 * the values in our test cases.
	 */
	void flushSession() {
		//sessionFactory.currentSession?.flush()
	}

	String resolveMessage(String code, List args) {
		return messageSource.getMessage(code, args as Object[], null)
	}
}