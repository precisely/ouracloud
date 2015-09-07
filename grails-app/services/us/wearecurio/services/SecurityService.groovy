package us.wearecurio.services

import org.apache.commons.logging.LogFactory

class SecurityService {
	private static def log = LogFactory.getLog(this)
	
	static transactional = false

	static SecurityService service
	
	static def set(SecurityService s) { service = s }

	static SecurityService get() { return service }
	
	static def getSpring() { return service.springSecurityService }
	
	def springSecurityService
	
	String encodePassword(String password) {
		return springSecurityService.encodePassword(password)
	}
}
