import grails.converters.JSON
import grails.plugin.springsecurity.SecurityFilterPosition
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.util.Environment
import us.wearecurio.marshallers.SummaryDataDomainMarshaller
import us.wearecurio.marshallers.UserDomainMarshaller
import us.wearecurio.marshallers.ValidationErrorMarshaller
import us.wearecurio.oauth.Client
import us.wearecurio.users.Role
import us.wearecurio.users.User
import us.wearecurio.users.UserRole
import us.wearecurio.users.UserService

class BootStrap {

	UserService userService

	def init = { servletContext ->
		log.debug "Bootstrap started executing"

		/**
		 * Register our custom filter just before the Spring's "authenticationProcessingFilter" so that we can first
		 * authenticate with OuraRing shop API and then our local authentication takes place.
		 *
		 * @see "resources.groovy"
		 * @see "https://github.com/grails-plugins/grails-spring-security-core/blob/v2.0-RC4/SpringSecurityCoreGrailsPlugin.groovy#L966"
		 */
		SpringSecurityUtils.clientRegisterFilter("ouraRingShopAuthenticationFilter",
				SecurityFilterPosition.FORM_LOGIN_FILTER.order - 1)

		registerMarshallers()

		Role.look("ROLE_CLIENT")		// This role is for client apps
		Role adminRole = Role.look("ROLE_ADMIN")
		Role userRole = Role.look("ROLE_USER")
		Role clientManagerRole = Role.look("ROLE_CLIENT_MANAGER")

		User testUser = userService.look("testuser")
		if (!testUser) {
			testUser = userService.create([username: "testuser", password: "xyz", email: "testuser@ouraring.com"])
		}

		UserRole.look(testUser, adminRole, true)
		UserRole.look(testUser, userRole, true)
		UserRole.look(testUser, clientManagerRole, true)

		assert testUser.authorities.contains(adminRole)

		if (Environment.isDevelopmentMode()) {
			createDevelopmentData()
		}
	}

	def destroy = {
		log.debug "Bootstrap destroyed"
	}

	private static void registerMarshallers() {
		JSON.registerObjectMarshaller(new ValidationErrorMarshaller())
		JSON.registerObjectMarshaller(new SummaryDataDomainMarshaller())
		JSON.registerObjectMarshaller(new UserDomainMarshaller())
	}

	private static void createDevelopmentData() {
		if (!Client.findByClientId(Client.OURA_APP_ID)) {
			new Client(
					name: "Oura Cloud Mobile App",
					clientId: Client.OURA_APP_ID,
					authorizedGrantTypes: ["password"],
					authorities: ["ROLE_CLIENT"],
					scopes: ["read", "write"]
			).save(flush: true)
		}

		if (!Client.findByClientId("curious-dev")) {
			new Client(
					name: "Curious Dev",
					clientId: "curious-dev",
					authorizedGrantTypes: ["authorization_code", "refresh_token"],
					authorities: ["ROLE_CLIENT"],
					scopes: ["read"],
					redirectUris: ["http://dev.wearecurio.us"]
			).save(flush: true)
		}
	}
}