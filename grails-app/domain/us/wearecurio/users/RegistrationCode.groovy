package us.wearecurio.users

/**
 * A domain class used to store the token for resetting a user's password.
 * @author Shashank Agrawal
 * @since 0.0.1
 */
class RegistrationCode {

	String username
	String token = UUID.randomUUID().toString().replaceAll("-", "")
	Date dateCreated = new Date()

	static constraints = {
		dateCreated(bindable: false)
	}

	static mapping = {
		version false
	}
}