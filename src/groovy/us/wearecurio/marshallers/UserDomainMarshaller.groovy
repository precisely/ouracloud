package us.wearecurio.marshallers

import grails.converters.JSON
import org.codehaus.groovy.grails.web.converters.exceptions.ConverterException
import org.codehaus.groovy.grails.web.converters.marshaller.ObjectMarshaller
import org.codehaus.groovy.grails.web.json.JSONWriter
import us.wearecurio.users.User

/**
 * A marshaller for rendering the fields for domain {@link us.wearecurio.users.User User} in the JSON format.
 *
 * @since 0.0.1
 * @author Shashank Agrawal
 * @see Bootstrap.groovy for registration.
 */
class UserDomainMarshaller implements ObjectMarshaller<JSON> {

	private static final List<String> SIMPLE_FIELDS = ["id", "version", "username"]
	private static final List<String> CONVERTABLE_FIELDS = ["dateCreated", "lastUpdated"]

	/**
	 * Checks whether this ObjectMarshaller is able/intended to support the given Object
	 *
	 * @param object the object which is about to getting converted
	 * @return <code>true</code> if the marshaller can/should perform the marshalling, <code>false</code> otherwise
	 */
	@Override
	boolean supports(Object object) {
		return object instanceof User
	}

	/**
	 * Perform the conversion.
	 * @param object Instance of User which is about to convert
	 * @throws ConverterException
	 */
	@Override
	void marshalObject(Object object, JSON json) throws ConverterException {
		JSONWriter writer = json.getWriter()
		User instance = object

		writer.object()

		SIMPLE_FIELDS.each { field ->
			writer.key(field)
					.value(instance[field])
		}

		CONVERTABLE_FIELDS.each { field ->
			writer.key(field)
			json.convertAnother(instance[field])
		}

		writer.endObject()
	}
}