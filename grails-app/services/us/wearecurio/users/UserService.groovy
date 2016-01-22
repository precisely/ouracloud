package us.wearecurio.users

import grails.transaction.Transactional
import org.codehaus.groovy.grails.web.binding.GrailsWebDataBinder
import org.grails.databinding.SimpleMapDataBindingSource
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.security.oauth2.provider.OAuth2Request
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices
import us.wearecurio.common.CustomOAuth2Request
import us.wearecurio.oauth.Client
import us.wearecurio.utility.Utils

/**
 * Grails service used to perform {@link us.wearecurio.users.User} related operations.
 *
 * @author Shashank Agrawal
 * @since 0.0.1
 */
@Transactional
class UserService {

	GrailsWebDataBinder grailsWebDataBinder
	AuthorizationServerTokenServices tokenServices

	/**
	 * Get the instance of {@link us.wearecurio.users.User} with given email.
	 * @param email The email to search user for
	 * @return The instance of User as described above or <code>null</code> if no user found.
	 */
	@Deprecated
	User look(String email) {
		return User.look(email)
	}

	/**
	 * Create a new user with the given arguments giving the "ROLE_USER" {@link us.wearecurio.users.Role}.
	 * @param args Required arguments for user creation: <code>email, password</code>/
	 * @return Newly created instance of User. The instance can have validation errors if a user already exists with
	 * the given email.
	 */
	User create(Map args) {
		User userInstance = new User()
		update(userInstance, args)

		if (userInstance.hasErrors()) {
			return userInstance
		}

		UserRole.create(userInstance, Role.look("ROLE_USER"))
		return userInstance
	}

	User createWithRoles(Map args, List<String> roles) {
		User userInstance = create(args)
		if (userInstance.hasErrors()) {
			return userInstance
		}

		roles.each { authority ->
			UserRole.create(userInstance, Role.look(authority))
		}

		return userInstance
	}

	/**
	 * Update the given user with the parameters
	 * @param userInstance Instance of {@link us.wearecurio.users.User User} to update
	 * @param args Map of parameters to update the userInstance for
	 * @return Updated userInstance
	 */
	User update(User userInstance, Map args) {
		List<String> whiteList = ["password", "email", "username"]
		grailsWebDataBinder.bind(userInstance, args as SimpleMapDataBindingSource, whiteList)
		if (!userInstance.username) {
			userInstance.username = args["email"]
		}

		// If there is a validation failure during save
		if (!Utils.save(userInstance)) {
			return userInstance
		}

		return userInstance
	}

	/**
	 * Get an approved access token for the authenticated user which can be used to make OAuth2 API calls.
	 * @param authentication Spring Security Core Authentication instance to get token for
	 * @return Access Token for API calls
	 */
	String getOAuth2Token(Authentication authentication) {
		OAuth2Request oAuth2Request = new CustomOAuth2Request(Client.OURA_APP_ID)
		OAuth2Authentication authenticationRequest = new OAuth2Authentication(oAuth2Request, authentication);
		authenticationRequest.setAuthenticated(true);

		return tokenServices.createAccessToken(authenticationRequest).getValue()
	}
}
