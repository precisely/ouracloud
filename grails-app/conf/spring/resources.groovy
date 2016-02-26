import us.wearecurio.authentication.CustomAuthenticationFilter
import us.wearecurio.authentication.OAuth2RequestDetectionFilter
import us.wearecurio.authentication.logout.MobileAppAwareLogoutHandler
import us.wearecurio.common.CustomUserDetailsService

// Place your Spring DSL code here
beans = {
	userDetailsService(CustomUserDetailsService)
	oAuth2RequestDetectionFilter(OAuth2RequestDetectionFilter)
	ouraRingShopAuthenticationFilter(CustomAuthenticationFilter) {
		userService = ref("userService")
		authenticationFailureHandler = ref('authenticationFailureHandler')
	}

	logoutSuccessHandler(MobileAppAwareLogoutHandler) {
		redirectStrategy = ref("redirectStrategy")
		defaultTargetUrl = "/"
	}
}