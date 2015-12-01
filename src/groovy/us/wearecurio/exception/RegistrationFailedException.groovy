package us.wearecurio.exception

/**
 * Exception to represent that any kind of registration is failed due to some reason.
 *
 * @author Shashank Agrawal
 * @since 0.0.2
 */
class RegistrationFailedException extends Exception {

	RegistrationFailedException(String message) {
		super(message)
	}
}
