package us.wearecurio.oauth

class AuthorizationCode {

	byte[] authentication
	String code

	static constraints = {
		code blank: false, unique: true
		authentication minSize: 1, maxSize: 1024 * 4
	}

	static mapping = {
		version false
	}
}
