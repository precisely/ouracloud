package us.wearecurio.oauth

import grails.plugin.springsecurity.annotation.Secured
import grails.transaction.Transactional
import org.springframework.dao.DataIntegrityViolationException

@Secured(["ROLE_CLIENT_MANAGER"])
@Transactional(readOnly = true)
class ClientController {

	static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

	def beforeInterceptor = [action: this.&validate]

	// Read about before interceptor. https://grails.github.io/grails-doc/2.5.0/ref/Controllers/beforeInterceptor.html
	private validate() {
		if (!params.id) return true;

		Client clientInstance = Client.get(params.id)
		if (!clientInstance) {
			flash.message = g.message(code: 'default.not.found.message', args: [message(code: 'client.label'), params.id])
			redirect(action: "index")
			return false
		}
		return true
	}

	// Convert comma separated values as list
	private void tokenizeParams() {
		["resourceIds", "scopes", "autoApproveScopes", "redirectUris"].each { key ->
			if (params[key]) {
				params[key] = params[key].tokenize(",")*.trim()
			}
		}
	}

	def index(Integer max) {
		params.max = Math.min(max ?: 10, 100)

		List clientInstanceList = Client.createCriteria().list(params) {
			if (params.query) {
				or {
					ilike("name", "%${params.query}%")
					ilike("clientId", "%${params.query}%")
				}
			}
		}

		[clientInstanceList: clientInstanceList, clientInstanceTotal: clientInstanceList.totalCount]
	}

	def create() {
		[clientInstance: new Client(params)]
	}

	@Transactional
	def save() {
		tokenizeParams()
		Client clientInstance = new Client(params)
		if (!clientInstance.save(flush: true)) {
			render(view: "create", model: [clientInstance: clientInstance])
			return
		}

		flash.message = message(code: 'default.created.message', args: [message(code: 'client.label'), clientInstance.id])
		redirect(action: "show", id: clientInstance.id)
	}

	def show(Client clientInstance) {
		[clientInstance: clientInstance]
	}

	def edit(Client clientInstance) {
		[clientInstance: clientInstance]
	}

	@Transactional
	def update(Long id, Long version) {
		Client clientInstance = Client.get(id)

		if (version != null) {
			if (clientInstance.version > version) {
				clientInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [
						message(code: 'client.label')] as Object[],
						"Another user has updated this Client while you were editing")

				render(view: "edit", model: [clientInstance: clientInstance])
				return
			}
		}

		tokenizeParams()
		clientInstance.properties = params

		if (!clientInstance.save(flush: true)) {
			render(view: "edit", model: [clientInstance: clientInstance])
			return
		}

		flash.message = message(code: 'default.updated.message', args: [message(code: 'client.label'), clientInstance.id])
		redirect(action: "show", id: clientInstance.id)
	}

	@Transactional
	def delete(Long id) {
		Client clientInstance = Client.get(id)

		try {
			clientInstance.delete(flush: true)
			flash.message = message(code: 'default.deleted.message', args: [message(code: 'client.label'), id])
			redirect(action: "index")
		} catch (DataIntegrityViolationException e) {
			flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'client.label'), id])
			redirect(action: "show", id: id)
		}
	}
}