package us.wearecurio

import org.springframework.http.HttpStatus

/**
 * Base controller of all controller which contains some generic methods & exception handlers which is used across
 * the controllers. Using Groovy's traits feature for allowing multiple inheritance.
 *
 * @since 0.0.1
 * @author Shashank Agrawal
 */
trait BaseController {

	static responseFormats = ["json"]

	/**
	 * Will catch all controller actions which throws {@link java.lang.IllegalArgumentException} and renders the
	 * exception to the client with 406 i.e. Not Acceptable status code.
	 *
	 * @param e Instance of IllegalArgumentException
	 * @return Returns the false value to avoid any further processing.
	 */
	Object handleIllegalArgumentException(IllegalArgumentException e) {
		log.info "IllegalArgumentException with message: $e.message"
		respondInternal([message: e.message], HttpStatus.NOT_ACCEPTABLE)
		return false
	}

	/**
	 * An overload method which is used to render data as JSON with any response code to the client. This method is
	 * required since there is a problem in Grails Controller's "respond" method for achieving the same thing.
	 *
	 * @param data Response data to render as JSON
	 * @param status Non 200 response code to return
	 * @return Returns false
	 *
	 * @see respond method in https://github.com/grails/grails-core/blob/master/grails-plugin-rest/src/main/groovy/grails/artefact/controller/RestResponder.groovy
	 */
	boolean respond(data, HttpStatus status) {
		return respondInternal(data, status)
	}

	boolean respondNotFound(Map data) {
		respondInternal(data, HttpStatus.NOT_FOUND)
	}

	private boolean respondInternal(Object data, HttpStatus status) {
		response.setStatus((status ?: HttpStatus.OK).value)
		respond(data)
		return false
	}
}
