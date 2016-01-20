package us.wearecurio.oauth

import grails.util.Environment

class Client {

	private static final String NO_CLIENT_SECRET = ''
	static final String OURA_APP_ID = "oura-app"

	transient springSecurityService

	String name
	String clientId
	String clientSecret

	Integer accessTokenValiditySeconds
	Integer refreshTokenValiditySeconds
	String clientHookURL
	ClientEnvironment environment

	Map<String, Object> additionalInformation

	static hasMany = [
			authorities: String,
			authorizedGrantTypes: String,
			resourceIds: String,
			scopes: String,
			autoApproveScopes: String,
			redirectUris: String
	]

	static transients = ['springSecurityService']

	static constraints = {
		name blank: false
		clientId blank: false, unique: true, index: true, indexAttributes: [unique: true]
		clientSecret nullable: true

		accessTokenValiditySeconds nullable: true
		refreshTokenValiditySeconds nullable: true

		authorities nullable: true
		authorizedGrantTypes nullable: true

		resourceIds nullable: true

		scopes nullable: true
		autoApproveScopes nullable: true

		redirectUris nullable: true
		additionalInformation nullable: true
		clientHookURL blank: true, nullable: true
	}

	def beforeInsert() {
		encodeClientSecret()
	}

	def beforeUpdate() {
		if(isDirty('clientSecret')) {
			encodeClientSecret()
		}
	}

	protected void encodeClientSecret() {
		clientSecret = clientSecret ?: NO_CLIENT_SECRET
		clientSecret = springSecurityService?.passwordEncoder ? springSecurityService.encodePassword(clientSecret) : clientSecret
	}

	@Override
	String toString() {
		return "Client={id=$id, name=$name, env=${environment?.name}"
	}
}

/**
 * An enum class used to represent a registered third party client for various environment. Like, there can be
 * multiple registered clients but from production, the notifications should only be go to the production clients.
 *
 * Grails has it's own Environment class but not using that directly since that enum does not have the "id" so the
 * string will be persisted to the database. Also, if in future, we add more custom environments then we will not
 * be able to modify that Grails enum class hence using our own class.
 */
enum ClientEnvironment {

	PRODUCTION(1, Environment.PRODUCTION.name),
	DEVELOPMENT(2, Environment.DEVELOPMENT.name),
	TEST(3, Environment.TEST.name)

	final int id
	final String name
	ClientEnvironment(int id, String name) {
		this.id = id
		this.name = name
	}

	static ClientEnvironment getCurrent() {
		return this.values().find { it.name == Environment.current.name }
	}
}
