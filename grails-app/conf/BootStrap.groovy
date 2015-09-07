import us.wearecurio.services.SecurityService
import us.wearecurio.users.Role
import us.wearecurio.users.User
import us.wearecurio.users.UserRole

class BootStrap {
	
	SecurityService securityService

    def init = { servletContext ->
		
		SecurityService.set(securityService)
		
		def adminRole = Role.look('ROLE_ADMIN')
		def userRole = Role.look('ROLE_USER')
		
		def testUser = User.look('admin', 'xyz')
		
		UserRole.look(testUser, adminRole, true)
		UserRole.look(testUser, userRole, true)
		
		assert User.count() == 1
		assert Role.count() == 2
		assert UserRole.count() == 2
		
		assert testUser.authorities.contains(adminRole)
		
		/*Requestmap.look('/*', 'IS_AUTHENTICATED_ANONYMOUSLY')
		Requestmap.look('/logout/**', 'IS_AUTHENTICATED_REMEMBERED,IS_AUTHENTICATED_FULLY')
		Requestmap.look('/login/**', 'IS_AUTHENTICATED_ANONYMOUSLY')
		Requestmap.look('/index/**', 'IS_AUTHENTICATED_ANONYMOUSLY')
		Requestmap.look('/oura/**', 'ROLE_ADMIN')
		 
		assert Requestmap.count() == 5	*/
    }
	
    def destroy = {
    }
}
