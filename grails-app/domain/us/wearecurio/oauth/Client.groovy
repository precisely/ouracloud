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
}

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
