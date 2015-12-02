package us.wearecurio.exception

/**
 * Exception to represent that any kind of authorization or authentication failed to complete.
 *
 * @author Shashank Agrawal
 * @since 0.0.2
 */
class AuthorizationFailedException extends Exception {

	AuthorizationFailedException() {
		this("")
	}

	AuthorizationFailedException(String message) {
		super(message)
	}
}
