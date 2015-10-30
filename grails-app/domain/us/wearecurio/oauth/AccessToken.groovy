package us.wearecurio.oauth

class AccessToken {

	String authenticationKey
	byte[] authentication

	String username
	String clientId

	String value
	String tokenType

	Date expiration
	Map<String, Object> additionalInformation

	static hasOne = [refreshToken: String]
	static hasMany = [scope: String]

	static constraints = {
		username nullable: true
		clientId blank: false
		value blank: false, unique: true
		tokenType blank: false
		refreshToken nullable: true
		authenticationKey blank: false, unique: true
		authentication minSize: 1, maxSize: 1024 * 4
		additionalInformation nullable: true
	}

	static mapping = {
		version false
		scope lazy: false
	}
}
