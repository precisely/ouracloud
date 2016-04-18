package us.wearecurio.controllers

import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.annotation.Secured
import org.springframework.web.multipart.commons.CommonsMultipartFile
import us.wearecurio.BaseController
import us.wearecurio.users.OuraShopPassword
import us.wearecurio.users.User
import us.wearecurio.users.UserService
import us.wearecurio.utility.Utils
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

	/**
	 * The GSP endpoint for rendering the web signup form (for GET request) and performing the signup (for POST
	 * request).
	 */
	@Secured(["permitAll"])
	def signup() {
		if (springSecurityService.isLoggedIn()) {
			redirect(uri: "/my-account")
			return
		}
		// Do not allow signup if the user has landed to Oura cloud for OAuth2 authorization by Curious.
		if (session.isOAuth2Authorization) {
			redirect(uri: "/login")
			return
		}

		if (request.get) {
			// Render the GSP
			return
		}

		User userInstance = userService.create(params)
		if (userInstance && userInstance.hasErrors()) {
			render(view: "signup", model: [userInstance: userInstance])
			return
		}

		flash.message = g.message([code: "profile.created"])
		springSecurityService.reauthenticate(userInstance.username)
		redirect(controller: "login", action: "authComplete")
	}

	@Secured("ROLE_USER")
	def welcome() {
	}

	@Secured("ROLE_CLIENT_MANAGER")
	def upload() {
		if (request.get) {
			return
		}

		CommonsMultipartFile receivedFile = request.getFile("userFile")
		String userHome = System.getenv("HOME") ?: System.getProperty("user.home") ?: "/home/oura"
		File destinationFile = new File("$userHome/temp/" + System.currentTimeMillis() + ".csv")

		receivedFile.transferTo(destinationFile)

		int totalRecords = 0, existingUsers = 0, failedImport = 0
		List<String> failedEmails = []

		destinationFile.eachCsvLine{ token ->
			totalRecords++
			String email = token[1]
			String md5Password = token[2]
			log.debug "Importing user with email $email"

			User userInstance = User.findByEmailIlike(email)
			if (!userInstance) {
				log.debug "No user found with email $email"
				Map properties = [email: email, password: UUID.randomUUID().toString()]
				userInstance = userService.create(properties)

				if (userInstance && userInstance.hasErrors()) {
					failedEmails << userInstance.email
					return		// continue looping
				}
			} else {
				existingUsers++
			}

			OuraShopPassword ouraShopPasswordInstance = OuraShopPassword.findByUser(userInstance)

			if (ouraShopPasswordInstance) {
				log.debug "OuraShopPassword already exists for $userInstance"
				return		// continue looping
			}

			ouraShopPasswordInstance = new OuraShopPassword([user: userInstance, password: md5Password])
			if (!Utils.save(ouraShopPasswordInstance, true)) {
				failedImport++
			}

			log.debug "Created $ouraShopPasswordInstance for $userInstance"
		}

		return [totalRecords: totalRecords, existingUsers: existingUsers, failedImport: failedEmails.size(), failedEmails: failedEmails]
	}
}