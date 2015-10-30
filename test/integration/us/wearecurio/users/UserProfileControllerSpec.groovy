package us.wearecurio.users

import grails.plugin.springsecurity.SpringSecurityService
import us.wearecurio.BaseIntegrationSpec

class UserProfileControllerSpec extends BaseIntegrationSpec {

	private UserProfileController controller

	SpringSecurityService springSecurityService

	def setup() {
		controller = new UserProfileController()
		controller.springSecurityService = [getCurrentUser: { ->
			return userInstance
		}] as SpringSecurityService
	}

	void "test update profile to check exclusion of username"() {
		String oldPassword = userInstance.password

		when: "The user account is updated with email, username and password"
		controller.request.method == "POST"
		controller.params.putAll([email: "newemail@ouraring.com", username: "johndoe", password: "xyz12345"])
		controller.update()
		flushSession()

		then: "Only email should be updated"
		controller.response.redirectUrl == "/my-account"
		userInstance.refresh().email == "newemail@ouraring.com"
		userInstance.username != "johndoe"
		userInstance.password == oldPassword
		userInstance.version == 1
	}

	void "test update password"() {
		String oldPassword = userInstance.password

		when: "Update password is passed with old password, new password and confirm new password"
		controller.request.method == "POST"
		controller.params.putAll([oldPassword: "123451",		// See BaseIntegrationTestCase for this password
				password: "xyz1234", password2: "xyz1234"
		])
		controller.updatePassword()
		flushSession()

		then: "Password should be updated"
		controller.response.redirectUrl == "/my-account"
		userInstance.refresh().password != oldPassword
		springSecurityService.passwordEncoder.isPasswordValid(userInstance.password, "xyz1234", null)
	}
}