package us.wearecurio.controllers

import grails.validation.Validateable
import org.geeks.browserdetection.UserAgentIdentService
import org.springframework.security.access.annotation.Secured
import org.springframework.security.authentication.AccountExpiredException
import org.springframework.security.authentication.CredentialsExpiredException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.LockedException
import org.springframework.security.web.WebAttributes
import us.wearecurio.users.RegistrationCode
import us.wearecurio.users.User
import us.wearecurio.utility.Utils

/**
 * Controller for managing all login and reset password related GSP based operations.
 *
 * @author Shashank Agrawal
 * @since 0.0.1
 */
@Secured("permitAll")
class LoginController extends grails.plugin.springsecurity.LoginController {

	UserAgentIdentService userAgentIdentService

	def authfail() {
		String msg = ""
		def exception = session[WebAttributes.AUTHENTICATION_EXCEPTION]
		if (exception) {
			if (exception instanceof AccountExpiredException) {
				msg = g.message(code: "springSecurity.errors.login.expired")
			} else if (exception instanceof CredentialsExpiredException) {
				msg = g.message(code: "springSecurity.errors.login.passwordExpired")
			} else if (exception instanceof DisabledException) {
				msg = g.message(code: "springSecurity.errors.login.disabled")
			} else if (exception instanceof LockedException) {
				msg = g.message(code: "springSecurity.errors.login.locked")
			} else {
				msg = g.message(code: "springSecurity.errors.login.fail")
			}
		}

		flash.message = msg
		flash.messageType = "danger"
		redirect action: "auth", params: params
	}

	@Secured("ROLE_USER")
	def authComplete() {
		User currentUserInstance = springSecurityService.getCurrentUser()
		log.debug "Redirecting $currentUserInstance to the mobile app"
		redirect(url: Utils.getOuraAppSigninLink())
	}

	def loggedOut() {
		if (userAgentIdentService.isMobile()) {
			redirect(url: Utils.getOuraAppSignoutLink())
			return
		}

		redirect(uri: "/")
	}

	/**
	 * Initiate the forgot password process by sending an reset password email to the given user.
	 */
	def forgotPassword() {
		// For GET request i.e. for rendering the forgot password page
		if (!request.post) {
			return
		}

		log.info "Parameters recieved to reset password: $params"
		String username = params.username
		if (!username) {
			flash.message = "Please provide your email."
			flash.messageType = "danger"
			return
		}

		// Add "enabled" restriction to not allow the deleted marked accounts
		User userInstance = User.withCriteria {
			or {
				// MongoDB matches with case sensitive unlike MySQL so using "ilike" to match case insensitive
				ilike("email", username)
				ilike("username", username)
			}
			eq("enabled", true)
		}[0]

		if (!userInstance) {
			flash.message = "No user was found with given email."
			flash.messageType = "danger"
			return
		}

		RegistrationCode registrationCode = RegistrationCode.findOrCreateByUsername(userInstance.username)
		if (!registrationCode.id) {
			Utils.save(registrationCode)
		}

		String bodyText = g.render([template: "/emailTemplates/resetPasswordEmail", model: [userInstance:
				userInstance, token: registrationCode.token]])

		sendMail {
			to userInstance.email
			subject "Ōura Ring - Password Reset"
			html bodyText
			async true
		}

		log.info "Mail sent successfully to change password of $userInstance"
		flash.message = "Mail sent successfully to change password."
		redirect(uri: "/login")
	}

	def resetPassword(ResetPasswordCommand command) {
		String token = params.remove("t")

		RegistrationCode registrationCode = token ? RegistrationCode.findByToken(token) : null
		if (!registrationCode) {
			log.info "Unable to find registration code of token [$token]"
			flash.message = g.message([code: "password.reset.badCode"])
			flash.messageType = "danger"
			redirect(action: "forgotPassword")
			return
		}

		if (!request.post) {
			return [token: token]
		}

		command.username = registrationCode.username
		command.validate()

		if (command.hasErrors()) {
			log.info "Error resetting password for User [$command.username]"
			return [token: token, command: command]
		}

		RegistrationCode.withTransaction { status ->
			User userInstance = User.look(registrationCode.username)
			userInstance.password = command.password
			userInstance.save()
			registrationCode.delete()
			log.info "Password changed successfully for $userInstance"

			springSecurityService.reauthenticate(userInstance.username)
			flash.message = message(code: "password.reset.success")
			redirect(uri: "/welcome")
		}
	}
}

@Validateable
class ResetPasswordCommand {
	String username
	String password
	String password2

	static constraints = {
		password blank: false, minSize: 6, maxSize: 64, validator: { String password, command ->
			if (command.username && command.username.equals(password)) {
				return "command.password.error.username"
			}

			// Enable the special character limitation later
			/*if (password && (!password.matches('^.*\\p{Alpha}.*$') || !password.matches('^.*\\p{Digit}.*$'))) {
				return 'command.password.error.strength'
			}*/
		}
		password2 validator: { value, command ->
			if (command.password != command.password2) {
				return "confirm.password.not.match"
			}
		}
	}
}