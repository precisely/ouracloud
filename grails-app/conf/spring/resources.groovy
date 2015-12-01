import us.wearecurio.authentication.CustomAuthenticationFilter
import us.wearecurio.common.CustomUserDetailsService

// Place your Spring DSL code here
beans = {
	userDetailsService(CustomUserDetailsService)
	authenticationProcessingFilter(CustomAuthenticationFilter) { bean ->
		println bean.dump()
		bean.parent = ref("authenticationProcessingFilter")
		//bean.setParent(RequestHolderAuthenticationFilter)
	}
}