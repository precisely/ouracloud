package us.wearecurio.authentication

import grails.plugin.springsecurity.web.authentication.RequestHolderAuthenticationFilter
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import us.wearecurio.services.OuraShopAPIService

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * A custom "authenticationProcessingFilter" for Grails Spring Security plugin to first authenticate the user to the
 * OuraRing shop API then to the OuraCloud (local) app.
 *
 * @author Shashank Agrawal
 * @since 0.0.2
 */
class CustomAuthenticationFilter extends RequestHolderAuthenticationFilter {

	private static OuraShopAPIService ouraShopAPIService

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
		println "hello $ouraShopAPIService"
		return super.attemptAuthentication(request, response)
	}

	static void init(OuraShopAPIService ouraShopAPIService) {
		this.ouraShopAPIService = ouraShopAPIService
	}
}
