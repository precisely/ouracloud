package us.wearecurio.utility

import grails.plugin.springsecurity.SpringSecurityService
import grails.util.Holders
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import us.wearecurio.users.UserService

/**
 * @author mitsu
 */
class Utils {

	private static Log log = LogFactory.getLog(this)

	static final String APP_BASE_NAME = "ouraapp://"
	static final String APP_PARAMETER_NAME = "ouraapp"
	static final String REDIRECT_TO_APP_KEY = "REDIRECT_TO_APP"

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
		Map caseInsensitiveParams = new TreeMap(String.CASE_INSENSITIVE_ORDER)
		caseInsensitiveParams << params

		// If a "ouraapp" parameter is available was available on the logout link
		return (caseInsensitiveParams[APP_PARAMETER_NAME] != null) && (caseInsensitiveParams[APP_PARAMETER_NAME] != "")
	}
}