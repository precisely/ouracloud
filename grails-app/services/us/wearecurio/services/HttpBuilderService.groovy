package us.wearecurio.services

import org.springframework.transaction.annotation.Transactional

import groovyx.net.http.ContentType
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method

class HttpBuilderService {

	static transactional = false    // Required if the method is called from any hibernate event

	/**
	 * A generic method to perform rest request to external api's or url
	 * using HttpBuilder.
	 * @param requestURL REQUIRED The external URL in which request is to
	 *        be performed
	 * @param method The HTTP method of request (must be all upper case).
	 * @param args A map containing request parameters to send.
	 * @param body in <b>args</b> is list of parameter to pass in form of map
	 * @return Returns the raw response with an addition method stating
	 *         that request is successful or not.
	 */
	def performRestRequest(String requestURL, String method = "POST", Map args = [:]) {
		def result = ""
		boolean success = true
		Method httpMethod = Method[method]
		HTTPBuilder http = new HTTPBuilder(requestURL)

		String logText = "$method request to $requestURL : "
		if(args.body) logText += "with request body: $args.body : ";

		if(log.debugEnabled) log.debug logText;
		else println logText;	// TODO Enable log for services & remove these lines.

		try {
			http.request(httpMethod, ContentType.JSON) {
				headers.'User-Agent' = 'Mozilla/5.0 Ubuntu/8.10 Firefox/3.0.4'
				headers.'Accept' = 'application/json'

				if(args.body) {
					body = args.body
				}

				response.success = { resp, json ->
					assert resp.statusLine.statusCode == 204
					result = json
				}
				response.failure = { resp ->
					success = false
					result = [error: "Unexpected error: ${resp.statusLine?.statusCode} : ${resp.statusLine?.reasonPhrase}",
							  code: resp.statusLine?.statusCode, reason: resp.statusLine?.reasonPhrase, success: false]
				}
			}
		} catch(Exception e) {
			success = false
			log.error "${logText} throws an exception: " + e.dump()
		}

		result.getMetaClass().isSuccess = { return success }
		result
	}

}