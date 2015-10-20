package us.wearecurio.users
import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.annotation.Secured
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.logout.LogoutHandler
import us.wearecurio.controllers.ResetPasswordCommand
import us.wearecurio.utility.Utils

/**
 * Controller for updating various profile related information for current logged in user for GSP based pages.
 *
 * @author Shashank Agrawal
 * @since 0.0.1
 */
@Secured(["ROLE_USER"])
class UserProfileController {

	static allowedMethods = [show: "GET", updatePassword: "POST", update: "POST", delete: "POST"]

	List<LogoutHandler> logoutHandlers			// Dependency injection for all logout handlers
	SpringSecurityService springSecurityService

	def delete() {
		User userInstance = springSecurityService.getCurrentUser()
		log.info "$userInstance deleting the account"

		userInstance.enabled = false
		Utils.save(userInstance, true)

		// Logging out the current user programmatically (http://stackoverflow.com/a/9814939/2405040)
		logoutHandlers.each { handler ->
			handler.logout(request, response, SecurityContextHolder.context.authentication)
		}

		flash.message = g.message([code: "profile.deleted"])
		redirect(uri: "/")
	}

	def show() {
		User userInstance = springSecurityService.getCurrentUser()

		[userInstance: userInstance]
	}

	def update() {
		User userInstance = springSecurityService.getCurrentUser()
		log.debug "$userInstance updating profile with $params"

		bindData(userInstance, params, [includes: ["email"]])
		userInstance.validate()

		if (userInstance.hasErrors()) {
			render(view: "show", model: [userInstance: userInstance])
			return
		}

		Utils.save(userInstance, true)

		flash.message = g.message([code: "profile.update.success"])
		redirect(action: "show")
	}

	def updatePassword(ResetPasswordCommand command) {
		User userInstance = springSecurityService.getCurrentUser()
		log.debug "$userInstance updating password"

		boolean isOldPasswordCorrect = springSecurityService.passwordEncoder.isPasswordValid(userInstance.password,
				params.oldPassword, null)

		if (!isOldPasswordCorrect) {
			log.debug "Incorrect old password for $userInstance"
			flash.message = g.message([code: "password.not.correct"])
			render(view: "show", model: [userInstance: userInstance])
			return
		}

		command.username = userInstance.username
		command.validate()

		if (command.hasErrors()) {
			flash.message = g.message([error: command.errors.getAllErrors()[0]])
			render(view: "show", model: [userInstance: userInstance])
			return
		}

		userInstance.password = command.password
		Utils.save(userInstance, true)
		flash.message = g.message([code: "password.reset.success"])
		redirect(action: "show")
	}
}