package us.wearecurio.services

import grails.transaction.Transactional
import grails.util.Environment
import groovy.time.TimeCategory
import groovyx.net.http.ContentType
import org.codehaus.groovy.grails.commons.DefaultGrailsApplication
import org.springframework.security.authentication.BadCredentialsException
import us.wearecurio.exception.AuthorizationFailedException
import us.wearecurio.exception.RegistrationFailedException
/**
 * A service which will be used to interact with the Oura Shop API using the OAuth2 specification.
 *
 * @author Shashank Agrawal
 * @since 0.0.2
 */
@Transactional
class OuraShopAPIService {

	private static final String BASE_URL = "https://shop.ouraring.com"

	private String accessToken
	private Date accessTokenExpireAt
	private Date accessTokenAcquiredAt

	DefaultGrailsApplication grailsApplication
	HttpService httpService

	/**
	 * Authorize the API with client credential to acquire the access token. Since this authorization is a OAuth2
	 * client credential based authentication, the same access token will be used for every user so this method
	 * stores the access token withing the scope of this service to avoid authorizing the token each time.
	 *
	 * @throws AuthorizationFailedException if authorization fails for any reason
	 * @author Shashank Agrawal
	 * @since 0.0.2
	 */
	void authorize() throws AuthorizationFailedException {
		log.debug "Authenticating with OuraRing Shop API"
		if (accessToken && accessTokenExpireAt && (accessTokenExpireAt > new Date())) {
			log.debug "Last access token found and will expire at $accessTokenExpireAt"
			return
		}

		accessToken = null
		accessTokenExpireAt = null

		ConfigObject apiConfig = grailsApplication.config.api.shop.ouraring

		// Use the basic authentication i.e. Base64 encoding of string "username:password"
		String authorization = "${apiConfig.clientID}:${apiConfig.clientSecret}".encodeAsBase64()

		Map args = [headers: [Authorization: "Basic $authorization"], body: [grant_type: "client_credentials"]]
		Object response = httpService.postResource(BASE_URL + "/oauth2/token.php", args)

		if (!response.isSuccess()) {
			log.error "Unable to authorize with OuraRing API. Response: $response"
			throw new AuthorizationFailedException()
		}

		log.debug "Authenticated with OuraRing with response $response"

		accessTokenAcquiredAt = new Date()
		accessToken = response["access_token"]

		use (TimeCategory) {
			/**
			 * Storing the date at which the acquired access token will expire. Since this access token is client
			 * authentication based token so it will be same for all. Read the method comment for more details.
			 *
			 * Note: Keeping a buffer of 5 minutes of expiry just to be sure that we re-authenticate for token before
			 * 5 minutes of expiry.
			 */
			accessTokenExpireAt = (response["expires_in"].seconds.from.now - 5.minutes)
			log.debug "Access token will expire at $accessTokenExpireAt"
		}
	}

	/**
	 * Register a new user with given email and password to the Oura Shop site by making OAuth2 API call.
	 *
	 * @param email Email of the new user to register
	 * @param password Password for the new user
	 * @return A map containing various information of the registered user at Oura Shop site
	 * @throws AuthorizationFailedException If the authorization to the Oura Shop fails
	 * @throws RegistrationFailedException If the registration fails for some reason
	 */
	Map register(String email, String password) throws AuthorizationFailedException, RegistrationFailedException {
		log.debug "Registering [$email] to the OuraRing Shop API"

		if (Environment.current != Environment.PRODUCTION) {
			return [email: email]
		}

		authorize()

		Map args = [body: [access_token: accessToken, email: email, password: password], contentType: ContentType.JSON]
		Object response = httpService.postResource(BASE_URL + "/oauth2/register.php", args)

		log.debug "Response for registering [$email]: $response"

		if (!response.isSuccess() || !(response instanceof Map)) {
			throw new RegistrationFailedException()
		}

		if (isTokenExpired(response)) {
			return register(email, password)
		}

		// Especially checking for "null" since Groovy treats "0" as false value
		if (response["id"] == null) {
			throw new RegistrationFailedException("Unknown error occurred.")
		}

		int responseID = response["id"].toString().toInteger()

		// API returns "0" as the "id" if the registration fails for any reason
		if (responseID == 0) {
			// Check if the failure was due to the user already exists
			if (response["error"].contains("already exists")) {
				return [email: email]
			}

			throw new RegistrationFailedException(response["error"])
		}

		return response
	}

	/**
	 * Login the user with given email and password to the Oura Shop site by making OAuth2 API call.
	 *
	 * @param email Email of the user to login
	 * @param password Password for the user
	 * @return A map containing various information on successfully logged in user at Oura Shop site
	 * @throws AuthorizationFailedException If the authorization to the Oura Shop fails
	 * @throws org.springframework.security.authentication.BadCredentialsException If the login fails
	 */
	Map login(String email, String password) throws AuthorizationFailedException, BadCredentialsException {
		log.debug "Attempting OuraRing shop authentication with [$email]"

		if (Environment.current != Environment.PRODUCTION) {
			return [email: email]
		}

		authorize()

		Map args = [body: [access_token: accessToken, email: email, password: password], contentType: ContentType.JSON]
		Object response = httpService.postResource(BASE_URL + "/oauth2/login.php", args)

		log.debug "Response for login via [$email]: $response"

		if (!response.isSuccess() || !(response instanceof Map)) {
			throw new BadCredentialsException("")
		}

		if (isTokenExpired(response)) {
			return login(email, password)
		}

		// If "id" returned by the API is 0 or null
		if (!response["id"]) {
			throw new BadCredentialsException(response["error"])
		}

		return response
	}

	/**
	 * Logout the user. TODO This method call seems to be of no use. Confirm and verify this.
	 *
	 * @throws AuthorizationFailedException If the authorization to the Oura Shop fails
	 */
	Map logout() throws AuthorizationFailedException {
		log.debug "Attempting OuraRing Shop logout"

		authorize()

		Map args = [body: [access_token: accessToken], contentType: ContentType.JSON]
		Object response = httpService.postResource(BASE_URL + "/oauth2/logout.php", args)

		log.debug "Response for logout $response"
		return [:]
	}

	boolean isTokenExpired(Map response) {
		if (response["error"] == "expired_token") {
			accessToken = null
			accessTokenExpireAt = null
			authorize()
			return true
		}

		return false
	}
}