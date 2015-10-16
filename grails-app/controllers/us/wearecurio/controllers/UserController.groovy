package us.wearecurio.controllers

import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.annotation.Secured
import us.wearecurio.BaseController
import us.wearecurio.users.User
import us.wearecurio.users.UserService

/**
 * Endpoint for creating and updating an user account.
 *
 * @author Shashank Agrawal
 * @since 0.0.1
 */
class UserController implements BaseController {

	static allowedMethods = [get: "GET", save: "POST", update: "PUT"]

	SpringSecurityService springSecurityService
	UserService userService

	/**
	 * Get the current authenticated user's details.
	 * @Request
	 * <pre>
	 *     GET		/api/user/me
	 * </pre>
	 *
	 * @Response
	 * <pre>
	 *     {
	 *         "id": 31,
	 *         "username": "johndoe",
	 *         "dateCreated": "",
	 *         "lastUpdated": "",
	 *         "version": 1
	 *     }
	 * </pre>
	 */
	@Secured(["ROLE_USER"])
	def get() {
		User userInstance = springSecurityService.getCurrentUser()

		respond(userInstance)
	}

	/**
	 * Create a new user. Need to authorize the client app before using this API. See "Client Access Token" section.
	 * @Request
	 * <pre>
	 *     POST		/api/user
	 *     {
	 *         "username": "newuser",
	 *         "password": "example1234"
	 *     }
	 * </pre>
	 *
	 * @Response
	 * For success response on user creation, see the "get" API.
	 */
	@Secured(["#oauth2.isClient() and #oauth2.clientHasRole('ROLE_CLIENT')"])
	def save() {
		Map requestData = request.JSON
		log.debug "Creating user profile with $requestData. Client " + springSecurityService.getPrincipal()

		String username = requestData["username"]
		if (!username) {
			throw new IllegalArgumentException(g.message(code: "user.signup.username.missing"))
		}

		User userInstance = userService.create(requestData)
		if (userInstance && userInstance.hasErrors()) {
			// Will result in unprocessable entity (422 status code) with error messages
			respond(userInstance.errors)		// respond(userInstance) can also be written. Both are same
			return
		}

		respond(userInstance)
	}

	/**
	 * Update the profile of current authenticated user.
	 * @Request
	 * <pre>
	 *     PUT		/api/user/me
	 *     {
	 *         "password": "updated password for example"
	 *     }
	 * </pre>
	 *
	 * @Response
	 * For success response, see the response from "get" API.
	 */
	@Secured(["ROLE_USER"])
	def update() {
		Map requestData = request.JSON
		User userInstance = springSecurityService.getCurrentUser()

		userService.update(userInstance, requestData)

		if (userInstance.hasErrors()) {
			respond(userInstance.errors)
			return
		}

		respond(userInstance)
	}

	@Secured(["permitAll"])
	def signup() {
		if (request.get) {
			return
		}

		User userInstance = userService.create(params)
		if (userInstance && userInstance.hasErrors()) {
			render(view: "signup", model: [userInstance: userInstance])
			return
		}

		springSecurityService.reauthenticate(userInstance.username)
		redirect(uri: "/")
	}

	@Secured(["ROLE_USER"])
	def account() {

	}
}