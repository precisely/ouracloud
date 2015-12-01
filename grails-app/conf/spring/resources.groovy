import us.wearecurio.authentication.CustomAuthenticationFilter
import us.wearecurio.common.CustomUserDetailsService

// Place your Spring DSL code here
beans = {
	userDetailsService(CustomUserDetailsService)
	ouraRingShopAuthenticationFilter(CustomAuthenticationFilter) {
		ouraShopAPIService = ref("ouraShopAPIService")
	}
}