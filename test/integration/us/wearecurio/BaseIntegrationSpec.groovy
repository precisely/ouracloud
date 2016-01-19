package us.wearecurio
import grails.test.spock.IntegrationSpec
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.springframework.context.MessageSource
import spock.lang.Shared
import us.wearecurio.model.PubSubNotification
import us.wearecurio.model.SummaryDataType
import us.wearecurio.oauth.Client
import us.wearecurio.oauth.ClientEnvironment
import us.wearecurio.users.User
import us.wearecurio.users.UserService
import us.wearecurio.utility.Utils

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

	Client createClient(int i = 0, Map args = [:]) {
		Map defaultArgs = [clientId: "clientID$i", clientSecret: "secret-key$i", clientServerURL:
				"http://localhost:8080", name: "Test App $i", clientHookURL: "http://localhost:8080", environment:
				ClientEnvironment.TEST]

		args = defaultArgs + args		// Merge the incoming arguments with the default one
		Client clientInstance = new Client(args)
		assert Utils.save(clientInstance, true) == true

		return clientInstance
	}

	PubSubNotification createNotification(User user, SummaryDataType type, Client client, Map args = [:]) {
		PubSubNotification pubSubNotificationInstance = new PubSubNotification([user: user, type: type, client: client])
		pubSubNotificationInstance.properties = args
		assert Utils.save(pubSubNotificationInstance, true) == true

		return pubSubNotificationInstance
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