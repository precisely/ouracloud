package us.wearecurio.controllers

import grails.rest.RestfulController
import us.wearecurio.users.User
import grails.plugin.springsecurity.annotation.Secured

@Secured(['ROLE_ADMIN'])
class UserController extends RestfulController {
	static responseFormats = ['json']
	
	UserController() {
		super(User)
	}

    def index() { }
}
