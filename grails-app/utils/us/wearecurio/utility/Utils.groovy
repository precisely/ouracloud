package us.wearecurio.utility

import grails.plugin.springsecurity.SpringSecurityService
import grails.util.Holders
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import us.wearecurio.users.UserService

import javax.servlet.http.HttpSession
/**
 * @author mitsu
 */
class Utils {

	private static Log log = LogFactory.getLog(this)

	static final String APP_BASE_NAME = "ouraapp://"
	static final String APP_PARAMETER_NAME = "ouraapp"
	static final String DISPLAY_SIGNUP_FORM_PARAMETER_NAME = "beta"
	static final String REDIRECT_TO_APP_KEY = "REDIRECT_TO_APP"
	static final String DISPLAY_SIGNUP_FORM_KEY = "DISPLAY_SIGNUP_FORM"

	static boolean save(obj) {
		return save(obj, false)
	}

	static boolean save(obj, boolean flush) {
		if (!obj.save(flush: flush)) {
			log.debug "Error saving $obj: $obj.errors"
			return false
		}

		log.debug "$obj saved successfully"
		return true
	}

	/**
	 * Get iOS mobile App URL to launch the Ouracloud mobile app with access token for currently logged in user.
	 * @return Above described URL
	 */
	static String getOuraAppSigninLink() {
		UserService userService = Holders.getApplicationContext()["userService"]
		SpringSecurityService springSecurityService = Holders.getApplicationContext()["springSecurityService"]

		String accessToken = userService.getOAuth2Token(springSecurityService.getAuthentication())
		return "${APP_BASE_NAME}signin?token=${accessToken}"
	}

	static String getOuraAppSignoutLink() {
		return "${APP_BASE_NAME}signout"
	}

	/**
	 * Check if the given parameters has case insensitive "ouraapp" parameter with any value other than empty or null.
	 * This parameter is used to redirect the user to the Oura mobile app after certain operation like signup, signup
	 * or logout.
	 *
	 * @param params Request parameters
	 * @return <code>true</code> if request parameters has the ouraapp parameter
	 */
	static boolean hasOuraappParameter(Map params) {
		Map loggingParams = new HashMap(params)

		Holders.getFlatConfig()["grails.exceptionresolver.params.exclude"].each { key ->
			// Mask confidential parameters from logging
			if (loggingParams.containsKey(key)) {
				loggingParams[key] = "****"
			}
		}

		log.debug "Check for ouraapp parameter in $loggingParams"

		Map caseInsensitiveParams = new TreeMap(String.CASE_INSENSITIVE_ORDER)
		caseInsensitiveParams << params

		// If a "ouraapp" parameter is available was available on the logout link
		return (caseInsensitiveParams[APP_PARAMETER_NAME] != null) && (caseInsensitiveParams[APP_PARAMETER_NAME] != "")
	}

	/**
	 * Confirm if we already have the key set in the session for redirecting the user after signup/signin/signout or
	 * else check the "ouraapp" parameter. See {@link #hasOuraappParameter} method for more details.
	 * @param session Current HTTP session of the user
	 * @param params Parameters received for this request
	 */
	static void checkParameterToRedirectToApp(HttpSession session, Map params, String requestURI) {
		// If "session" is already have key to redirect the user after signin/signout/logout
		if (session[REDIRECT_TO_APP_KEY]) {
			log.debug "Session key already set for redirecting to mobile app. [$requestURI]"
			// Then don't check again
			return
		}

		session[REDIRECT_TO_APP_KEY] = hasOuraappParameter(params)
		if (session[REDIRECT_TO_APP_KEY]) {
			log.debug "Session key added to redirect to mobile app. [$requestURI]"
		}
	}

	static Boolean shouldRedirectToTheMobileApp(HttpSession session) {
		return session[REDIRECT_TO_APP_KEY]
	}

	/**
	 * Checks whether we have to display the signup form or not. This method first checks if we have a session key
	 * set to check the signup form or not. If we already have that session key set then we return true.
	 * If not then we check for a case insensitive parameter "beta" for any values (except empty or null) and return
	 * the same by setting it to the session.
	 * @param session Current HTTP session of the user
	 * @param params Parameters received for this request
	 * @return <code>true</code> based on the above description
	 */
	static boolean shouldDisplayTheSignupForm(HttpSession session, Map params) {
		if (session[DISPLAY_SIGNUP_FORM_KEY]) {
			return true
		}

		// Using this map for case insensitive params's key checking for the "beta" parameter
		Map caseInsensitiveParams = new TreeMap(String.CASE_INSENSITIVE_ORDER)
		caseInsensitiveParams << params

		// Display signup form for any value of case insensitive "beta" parameter except for empty or null value
		boolean displaySignupForm = (caseInsensitiveParams[DISPLAY_SIGNUP_FORM_PARAMETER_NAME] != null) &&
				(caseInsensitiveParams[DISPLAY_SIGNUP_FORM_PARAMETER_NAME] != "")
		session[DISPLAY_SIGNUP_FORM_KEY] = displaySignupForm

		return displaySignupForm
	}
}