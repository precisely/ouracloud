package us.wearecurio.app

import grails.plugin.springsecurity.SpringSecurityService
import us.wearecurio.users.UserService
import us.wearecurio.utility.Utils

class ButtonTagLib {

	static defaultEncodeAs = [taglib: "none"]
	static namespace = "oura"

	UserService userService
	SpringSecurityService springSecurityService

	/**
	 * Renders the "Go to the App" button with an access token which will open the Ouracloud iOS mobile app.
	 */
	def appButton = {
		String appURL = Utils.getOuraAppSigninLink()

		out << """
			   <a href="${appURL}" class="btn btn-green btn-lg btn-block btn-rounded visible-xs visible-sm">
					Go to the App
			   </a>
			   """
	}
}