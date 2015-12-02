package us.wearecurio.services

import groovyx.net.http.ContentType
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method

/**
 * A service method used to perform HTTP request to the given resource/URL and the data.
 *
 * @author Shashank Agrawal
 * @since 0.0.2
 */
class HttpService {

	static transactional = false    // Required if the method is called from any hibernate event

	/**
	 * Utility method to perform rest request to external API/URL using Groovy's HttpBuilder.
	 *
	 * @param requestURL REQUIRED The external URL in which request is to be performed
	 * @param method The HTTP method of request (must be all upper case).
	 * @param args A map containing request parameters to send.
	 * @param body in <b>args</b> is list of parameter to pass in form of map
	 * @return Returns the raw response with an addition method stating that request is successful or not.
	 */
	@SuppressWarnings("CatchException")
	def performRequest(String requestURL, Method method, Map args) {
		args = args ?: [:]
		args.requestContentType = args.requestContentType ?: ContentType.URLENC

		def result = ""
		int responseStatus
		boolean success = false
		long currentTime = System.currentTimeMillis()

		// Do not log "args" since it may contain the password
		log.debug "[$currentTime] $method request to $requestURL"

		try {
			HTTPBuilder http = new HTTPBuilder(requestURL)

			if (args.headers) {
				http.setHeaders((Map) args.headers)
			}
			// Which type of data do we expect in response
			if (args.contentType) {
				http.setContentType(args.contentType)		// Example: ContentType.JSON
			}

			http.request(method) {
				if (args.requestContentType) {
					requestContentType = args.requestContentType		// Example: ContentType.URLENC
				}
				if (args.body) {
					body = args.body
				}

				response.success = { resp, data ->
					responseStatus = resp.statusLine.statusCode
					success = true
					result = data
				}
				response.failure = { resp, data ->
					responseStatus = resp.statusLine.statusCode
					result = [reason: resp.statusLine?.reasonPhrase, data: data]
				}
			}
		} catch(Exception e) {
			log.error "[$currentTime] $method request to $requestURL failed with exception:", e
		}

		log.debug "[$currentTime] $method request to $requestURL completed with status [$responseStatus]"

		result.getMetaClass().getCode = { return responseStatus }
		result.getMetaClass().isSuccess = { return success }
		return result
	}

	/**
	 * Implementing Groovy's "methodMissing" method to provide some utility methods. Like:
	 *
	 * <pre>
	 * 		httpService.postResource("http://example.com", [body: [name: "John"]])
	 * </pre>
	 *
	 * The above service call will result in:
	 *
	 * <pre>
	 * 		httpService.performRequest("http://example.com", Method.POST, [body: [name: "John"]])
	 * </pre>
	 *
	 * @author Shashank Agrawal
	 * @since 0.0.2
	 * @see "http://www.groovy-lang.org/metaprogramming.html#_methodmissing"
	 */
	def methodMissing(String name, args) {
		if (name ==~ /^(get|put|post|delete|options|head)Resource/) {
			List<Object> arguments = args as List
			def data = (name =~ /^(get|put|post|delete|options|head)Resource/)

			Method requestMethod = Method[data[0][1].toUpperCase()]

			return performRequest(args[0], requestMethod, arguments[1])
		}

		throw new MissingMethodException(name, this.class, args)
	}
}