package us.wearecurio.app

import grails.plugin.springsecurity.SpringSecurityService
import us.wearecurio.users.UserService

class ButtonTagLib {

	static defaultEncodeAs = [taglib: "none"]
	static namespace = "oura"

	UserService userService
	SpringSecurityService springSecurityService

	/**
	 * Renders the "Go to the App" button with an access token which will open the Ouracloud iOS mobile app.
	 */
	def appButton = {
		String accessToken = userService.getOAuth2Token(springSecurityService.getAuthentication())

		out << """
			   <a href="OuraApp://?token=${accessToken}" class="btn btn-green btn-lg btn-block btn-rounded visible-xs visible-sm">
					Go to the App
			   </a>
			   """
	}
}