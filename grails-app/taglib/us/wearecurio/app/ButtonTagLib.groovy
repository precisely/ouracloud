package us.wearecurio.app

import us.wearecurio.utility.Utils

class ButtonTagLib {

	static defaultEncodeAs = [taglib: "none"]
	static namespace = "oura"

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

	def iTuneAppLink = {
		out << 	"""
					<a href="https://itunes.apple.com/us/app/we-are-curious/id1063805457?mt=8" class="nowrap">
						We Are Curious
					</a>
				"""
	}
}