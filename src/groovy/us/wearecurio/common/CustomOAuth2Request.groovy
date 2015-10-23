package us.wearecurio.common

import org.springframework.security.oauth2.provider.OAuth2Request

/**
 * A custom class used to generate the OAuth2 token for currently logged in user. The only use of this class is to
 * tell that User has approved the app by overriding the "isApproved" method.
 *
 * @Author Shashank Agrawal
 * @since 0.0.1
 */
class CustomOAuth2Request extends OAuth2Request {

	CustomOAuth2Request(String clientId) {
		super(clientId)
	}

	@Override
	boolean isApproved() {
		return true
	}
}
