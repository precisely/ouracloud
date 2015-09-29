package us.wearecurio.services

import grails.plugin.springsecurity.SpringSecurityService
import grails.util.Holders
import org.apache.commons.logging.LogFactory

class SecurityService {

	static transactional = false

	private static def log = LogFactory.getLog(this)

	SpringSecurityService springSecurityService

	static SecurityService get() {
		return Holders.getApplicationContext().securityService
	}

	static SpringSecurityService getSpring() {
		return Holders.getApplicationContext().springSecurityService
	}

	String encodePassword(String password) {
		return springSecurityService.encodePassword(password)
	}
}
