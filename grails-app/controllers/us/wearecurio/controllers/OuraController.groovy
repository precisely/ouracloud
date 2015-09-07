package us.wearecurio.controllers

import grails.plugin.springsecurity.annotation.Secured

@Secured(value = ['ROLE_ADMIN', 'IS_AUTHENTICATED_REMEMBERED'])
class OuraController {

    def index() {
		render 'Secure access only'
	}
}
