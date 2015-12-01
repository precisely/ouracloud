package us.wearecurio.services

import grails.transaction.Transactional
import groovyx.net.http.ContentType
import org.codehaus.groovy.grails.commons.DefaultGrailsApplication
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
	private Date lastAuthenticatedAt

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
		if (accessToken && lastAuthenticatedAt) {
			// TODO Check for 1hr elapsed time and do not authorize again
		}

		accessToken = null
		lastAuthenticatedAt = null

		ConfigObject apiConfig = grailsApplication.config.api.shop.ouraring
		String authorization = "${apiConfig.clientID}:${apiConfig.clientSecret}".encodeAsBase64()

		Map args = [headers: [Authorization: "Basic $authorization"], body: [grant_type: "client_credentials"]]
		Object response = httpService.postResource(BASE_URL + "/oauth2/token.php", args)

		if (!response.isSuccess()) {
			log.error "Unable to authorize with OuraRing API. Response: $response"
			throw new AuthorizationFailedException()
		}

		log.debug "Authenticated with OuraRing with response $response"

		lastAuthenticatedAt = new Date()
		accessToken = response["access_token"]
	}

	/**
	 * Register a new user with given email and password to the Oura Shop site by making OAuth2 API call.
	 *
	 * @param email Email of the new user to register
	 * @param password Password for the new user
	 * @return A map containing various information of the registered user at Oura Shop site
	 * @throws AuthorizationFailedException If the authorization to the Oura Shop fails
	 * @throws RegistrationFailedException IF the registration fails for some rason
	 */
	Map register(String email, String password) throws AuthorizationFailedException, RegistrationFailedException {
		log.debug "Registering [$email] to the OuraRing Shop API"

		authorize()

		Map args = [body: [access_token: accessToken, email: email, password: password], contentType: ContentType.JSON]
		Object response = httpService.postResource(BASE_URL + "/oauth2/register.php", args)

		log.debug "Response for registering [$email]: $response"

		if (!response.isSuccess() || !(response instanceof Map)) {
			throw new RegistrationFailedException()
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
}