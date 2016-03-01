package us.wearecurio.app

import us.wearecurio.utility.Utils

class ButtonTagLib {

	static defaultEncodeAs = [taglib: "none"]
	static namespace = "oura"

	private final static String ITUNE_URL = "https://itunes.apple.com/us/app/we-are-curious/id1063805457?mt=8"

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

	/**
	 * Renders the "We Are Curious" text link with iTune URL of Curious mobile app.
	 */
	def iTuneAppLink = {
		out << 	"""
					<a href="${ITUNE_URL}" class="nowrap">
						We Are Curious
					</a>
				"""
	}

	/**
	 * Renders the iTune URL of Curious mobile app.
	 */
	def iTuneAppURL = { attr, body ->
		out << body(ITUNE_URL)
	}

	/**
	 * Renders a logout link or button which will open the logout confirmation modal.
	 */
	def logoutLink = { attr ->
		out << 	"""
					<a href="#" data-toggle="modal" data-target="#confirm-logout-modal" class="${attr.class ?: ''}">
						Log Out
					</a>
				"""
	}

	/**
	 * Renders the logout URL.
	 */
	def logoutURL = { attr, body ->
		String targetURL = "/j_spring_security_logout"

		if (session[Utils.REDIRECT_TO_APP_KEY]) {
			targetURL += "?ouraapp=1"
		}

		out << body(targetURL)
	}
}