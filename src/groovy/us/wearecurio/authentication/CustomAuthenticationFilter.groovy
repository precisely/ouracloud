package us.wearecurio.authentication

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import us.wearecurio.exception.AuthorizationFailedException
import us.wearecurio.services.OuraShopAPIService

import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
/**
 * A custom "authenticationProcessingFilter" for Grails Spring Security plugin to first authenticate the user to the
 * OuraRing shop API then to the OuraCloud (local) app. This is achieved by adding a Authentication filter just
 * before the Spring registers the "authenticationProcessingFilter" so that we can first attempt the login at
 * OuraRing shop and if the authentication succeeded then only we further process with the Spring's authentication
 * of this cloud server.
 *
 * @see "Bootstrap.groovy"
 * @see "resources.groovy"
 * @see "https://github.com/grails-plugins/grails-spring-security-core/blob/v2.0-RC4/SpringSecurityCoreGrailsPlugin.groovy#L966"
 *
 * @author Shashank Agrawal
 * @since 0.0.2
 */
class CustomAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

	private OuraShopAPIService ouraShopAPIService
	private static Log log = LogFactory.getLog(this)
	// TODO Use from the config
	private String usernameParameter = "j_username"
	private String passwordParameter = "j_password"

	CustomAuthenticationFilter() {
		super("/j_spring_security_check")
	}

	@Override
	void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
		if (this.requiresAuthentication(request, response)) {
			attemptAuthentication(request, response)
		}

		chain.doFilter(request, response)
	}

	@Override
	Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
		try {
			ouraShopAPIService.login(request.getParameter(this.usernameParameter), request.getParameter(this.passwordParameter))
		} catch (AuthorizationFailedException e) {
			log.error("Unable to authenticate with OuraCloud Shop API", e)
		}

		return null
	}

	OuraShopAPIService getOuraShopAPIService() {
		return ouraShopAPIService
	}

	void setOuraShopAPIService(OuraShopAPIService ouraShopAPIService) {
		this.ouraShopAPIService = ouraShopAPIService
	}

	void afterPropertiesSet() {
		// Do nothing since we simply have to attempt login at OuraRing shop
	}
}
