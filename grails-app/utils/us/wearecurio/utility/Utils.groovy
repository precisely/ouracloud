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
		return "ouraapp://signin?token=${accessToken}"
	}
}