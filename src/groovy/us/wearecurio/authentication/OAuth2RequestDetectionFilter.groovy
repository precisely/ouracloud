package us.wearecurio.authentication

import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.FilterConfig
import javax.servlet.ServletException
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

/**
 * A http servlet filter which just checks if the user has initiated the OAuth by browsing to the URL
 * "/oauth/authorize" and puts a key "isOAuth2Authorization" into the session to change some text in the login UI.
 *
 * This filter has been added before Spring's security context check filter (see Bootstrap.groovy for filter chain
 * map) so that we can first check the URL before Spring redirects the user to the login page.
 * @author Shashank Agrawal
 */
class OAuth2RequestDetectionFilter implements Filter {

	@Override
	void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		if (request instanceof HttpServletRequest) {
			String requestedPath = request.getServletPath()
			if (requestedPath && requestedPath.startsWith("/oauth/authorize")) {
				request.getSession().setAttribute("isOAuth2Authorization", true)
			}
		}

		chain.doFilter(request, response)
	}

	@Override
	void destroy() {
	}
}
