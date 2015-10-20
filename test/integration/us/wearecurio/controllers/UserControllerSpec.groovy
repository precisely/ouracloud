package us.wearecurio.controllers

import grails.plugin.springsecurity.SpringSecurityService
import org.springframework.http.HttpStatus
import us.wearecurio.BaseIntegrationSpec
import us.wearecurio.users.Role
import us.wearecurio.users.User
import us.wearecurio.users.UserRole

class UserControllerSpec extends BaseIntegrationSpec {

	private UserController controller

	SpringSecurityService springSecurityService

	def setup() {
		controller = new UserController()
		controller.springSecurityService = [isLoggedIn: { ->
			return false
		}] as SpringSecurityService
	}

	void "test signup when no username is provided"() {
		when: "The signup endpoint is hit with no username in it"
		controller.request.method = "POST"
		controller.save()

		then: "Non 200 response should be returned"
		controller.response.status == HttpStatus.NOT_ACCEPTABLE.value()
		controller.response.json["error_description"] == resolveMessage("user.signup.username.missing", null)
	}

	void "test signup when the user already exists with the given username"() {
		given: "An existing user"
		userInstance.username == "xyz"

		when: "The signup endpoint is hit with the same username"
		controller.request.method = "POST"
		controller.request.json = [username: userInstance.username, email: "new@ouraring.com", password: "example1234"]
		controller.save()

		then: "Non 200 response should be returned with failure message"
		controller.response.status == HttpStatus.UNPROCESSABLE_ENTITY.value()
		controller.response.json["errors"] != null
		controller.response.json["errors"][0].message == resolveMessage("user.username.unique", [userInstance.username])
	}

	void "test new user signup"() {
		when: "The signup endpoint is hit with the new username & password"
		controller.request.method = "POST"
		controller.request.json = [username: "johndoe", email: "newuser@ouraring.com", password: "example1234"]
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

	void "test GSP signup for the get request"() {
		when: "The action is called with the GET request"
		controller.request.method = "GET"
		controller.signup()

		then: "It should render the GSP"
		controller.response.status == 200
	}

	void "test GSP signup"() {
		given: "Signup parameters"
		controller.request.method = "POST"
		controller.params.putAll([username: "johndoe", email: "newuser@ouraring.com", password: "example1234"])

		when: "The action is called"
		controller.signup()
		flushSession()

		then: "A new user should be created"
		User lastUserInstance = User.last()
		lastUserInstance.email == "newuser@ouraring.com"
		lastUserInstance.username == "johndoe"
		springSecurityService.passwordEncoder.isPasswordValid(lastUserInstance.password, "example1234", null)

		controller.response.redirectUrl == "/my-account"
	}

	void "test GSP signup when validation fails"() {
		given: "Signup parameters with the missing email"
		controller.request.method = "POST"
		controller.params.putAll([username: "johndoe", password: "example1234"])

		when: "The action is called"
		controller.signup()
		flushSession()

		then: "No user should be created"
		User.count() == 1		// Only one which is created in the BaseIntegrationTest
		controller.modelAndView.getViewName() == "/user/signup"
		User userInstance = controller.modelAndView.getModel().userInstance
		userInstance != null
		userInstance.hasErrors() == true
		userInstance.errors.hasFieldErrors("email") == true
	}
}