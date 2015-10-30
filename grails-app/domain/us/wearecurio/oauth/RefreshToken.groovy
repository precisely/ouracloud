package us.wearecurio.oauth

class RefreshToken {

	String value
	Date expiration
	byte[] authentication

	static constraints = {
		value blank: false, unique: true
		expiration nullable: true
		authentication minSize: 1, maxSize: 1024 * 4
	}

	static mapping = {
		version false
	}
}
