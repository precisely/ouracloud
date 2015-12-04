package us.wearecurio.oauth

class Client {

	private static final String NO_CLIENT_SECRET = ''
	static final String OURA_APP_ID = "oura-app"

	transient springSecurityService

	String name
	String clientId
	String clientSecret

	Integer accessTokenValiditySeconds
	Integer refreshTokenValiditySeconds
	String clientServerURL

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
		clientServerURL blank: false
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
