import us.wearecurio.oauth.Client
import us.wearecurio.users.Role
import us.wearecurio.users.User
import us.wearecurio.users.UserRole

class BootStrap {

	def init = { servletContext ->
		log.debug "Bootstrap started executing"
		Role adminRole = Role.look("ROLE_ADMIN")
		Role userRole = Role.look("ROLE_USER")
		Role clientRole = Role.look("ROLE_CLIENT")

		User testUser = User.look("testuser", "xyz")

		UserRole.look(testUser, adminRole, true)
		UserRole.look(testUser, userRole, true)
		UserRole.look(testUser, clientRole, true)

		assert testUser.authorities.contains(adminRole)

		Client client = Client.findByClientId("ouracloud")

		if (!client) {
			new Client(
					clientId: "ouracloud",
					authorizedGrantTypes: ["authorization_code", "refresh_token", "implicit", "password", "client_credentials"],
					authorities: ["ROLE_CLIENT"],
					scopes: ["read", "write"],
					redirectUris: ["http://myredirect.com"]
			).save(flush: true)
		}
	}

	def destroy = {
		log.debug "Bootstrap destroyed"
	}
}

