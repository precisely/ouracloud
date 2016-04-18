package us.wearecurio.authentication.logout

import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler
import us.wearecurio.utility.Utils

import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * A custom logout handler which will be invoked via Spring after successful logout to redirect the user to the
 * Mobile app if the logout URL has the "ouraapp" parameter. We can not use the session in this case to set a flag
 * because after logout, the session is flushed.
 *
 * @author Shashank Agrawal
 */
class MobileAppAwareLogoutHandler extends SimpleUrlLogoutSuccessHandler {

	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		Map<String, Object> headers = (request.getHeaderNames() as List).collectEntries { String headerName ->
			[headerName, request.getHeader(headerName)]
		}

		logger.debug("[${authentication.getName()}] logged out with headers " + headers)

		// If the "ourapp" parameter was available on the logout link
		if (Utils.hasOuraappParameter(request.getParameterMap())) {
			String targetURL = Utils.getOuraAppSignoutLink()
			logger.debug "Redirecting to [$targetURL] after logout"

			// Then redirect the user to the mobile app
			getRedirectStrategy().sendRedirect(request, response, targetURL)
			return
		}

		// Else follow the default Spring flow
		super.handle(request, response, authentication)
	}
}