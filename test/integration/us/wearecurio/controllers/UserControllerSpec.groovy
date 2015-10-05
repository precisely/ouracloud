package us.wearecurio.controllers

import grails.plugin.springsecurity.SpringSecurityService
import org.springframework.http.HttpStatus
import us.wearecurio.BaseIntegrationSpec
import us.wearecurio.users.Role
import us.wearecurio.users.User
import us.wearecurio.users.UserRole

class UserControllerSpec extends BaseIntegrationSpec {

	private UserController controller

	def setup() {
		controller = new UserController()
	}

	void "test signup when no username is provided"() {
		when: "The signup endpoint is hit with no username in it"
		controller.request.method = "POST"
		controller.save()

		then: "Non 200 response should be returned"
		controller.response.status == HttpStatus.NOT_ACCEPTABLE.value()
		controller.response.json["message"] == resolveMessage("user.signup.username.missing", null)
	}

	void "test signup when the user already exists with the given username"() {
		given: "An existing user"
		userInstance.username == "xyz"

		when: "The signup endpoint is hit with the same username"
		controller.request.method = "POST"
		controller.request.json = [username: userInstance.username, password: "example1234"]
		controller.save()

		then: "Non 200 response should be returned with failure message"
		controller.response.status == HttpStatus.UNPROCESSABLE_ENTITY.value()
		controller.response.json["errors"] != null
		controller.response.json["errors"][0].message != resolveMessage("user.username.unique", [userInstance.username])
	}

	void "test new user signup"() {
		when: "The signup endpoint is hit with the new username & password"
		controller.request.method = "POST"
		controller.request.json = [username: "johndoe", password: "example1234"]
		controller.save()

		then: "User account should be created and a default role should be given"
		User newUserInstance = User.last()
		newUserInstance != null
		newUserInstance.id != userInstance.id
		controller.response.status == HttpStatus.OK.value()
		controller.response.json["id"] == newUserInstance.id
		controller.response.json["username"] == "johndoe"
		controller.response.json["version"] == 0
		controller.response.json["dateCreated"] != null
		controller.response.json["lastUpdated"] != null
		controller.response.json["class"] == null

		UserRole.exists(newUserInstance.id, Role.look("ROLE_USER").id)
	}

	void "test get user endpoint"() {
		given: "Mocked SpringSecurityService method"
		controller.springSecurityService = [getCurrentUser: { ->
			return userInstance
		}] as SpringSecurityService

		when: "The get endpoint is hit"
		controller.request.method = "GET"
		controller.get()

		then: "The user's data should be returned"
		controller.response.status == HttpStatus.OK.value()
		controller.response.json["id"] == userInstance.id
		controller.response.json["username"] == userInstance.username
		controller.response.json["class"] == null
	}
}