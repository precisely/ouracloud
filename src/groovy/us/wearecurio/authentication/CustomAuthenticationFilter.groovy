package us.wearecurio.authentication

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import us.wearecurio.exception.AuthorizationFailedException
import us.wearecurio.users.UserService

import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
/**
 * We can not use the simple Grails Filter feature to filer the authentication request i.e. request to "/j_spring_security_check"
 * since Grails uses a filter chain which are invoked one by one prioritized by the index position of that chain and
 * the Grails filters are placed at the last. So using a custom authentication filter for Grails Spring Security
 * plugin to first authenticate the user to the OuraRing shop API then to the OuraCloud (local) app. This is achieved
 * by adding a Authentication filter into the Grails filter chain just before the registered Spring's
 * "authenticationProcessingFilter" so that we can first attempt the login at OuraRing shop and if the authentication
 * succeeded then only we further process with the Spring's authentication of this cloud server.
 *
 * @see "Bootstrap.groovy"
 * @see "resources.groovy"
 * @see "https://github.com/grails-plugins/grails-spring-security-core/blob/v2.0-RC4/SpringSecurityCoreGrailsPlugin.groovy#L966"
 *
 * @author Shashank Agrawal
 * @since 0.0.2
 */
class CustomAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

	UserService userService
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
		userService.validateOuraShopPassword(request.getParameter(this.usernameParameter),
				request.getParameter(this.passwordParameter))

		return null
	}

	// Will be used for properties set in the "resources.groovy"
	void setUserService(UserService userService) {
		this.userService = userService
	}

	void afterPropertiesSet() {
		// Do nothing since we simply have to attempt login at OuraRing shop
	}
}
