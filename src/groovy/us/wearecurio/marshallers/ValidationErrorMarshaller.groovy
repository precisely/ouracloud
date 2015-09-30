package us.wearecurio.marshallers

import grails.converters.JSON
import grails.util.Holders
import org.codehaus.groovy.grails.web.converters.exceptions.ConverterException
import org.codehaus.groovy.grails.web.converters.marshaller.ObjectMarshaller
import org.codehaus.groovy.grails.web.json.JSONWriter
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.validation.Errors
import org.springframework.validation.FieldError

/**
 * A generic custom marshaller for rendering domain's validation error in the JSON format.
 *
 * @since 0.0.1
 * @author Shashank Agrawal
 * @see Bootstrap.groovy for registration.
 */
class ValidationErrorMarshaller implements ObjectMarshaller<JSON>, ApplicationContextAware {

	private ApplicationContext applicationContext = Holders.getApplicationContext()

	/**
	 * Checks whether this ObjectMarshaller is able/intended to support the given Object
	 *
	 * @param object the object which is about to getting converted
	 * @return <code>true</code> if the marshaller can/should perform the marshalling, <code>false</code> otherwise
	 */
	@Override
	boolean supports(Object object) {
		return object instanceof Errors
	}

	/**
	 * This method used to marshal domain object errors into JSON Object with customized message.
	 * @param object Instance containing errors passed to marshal.
	 * @throws ConverterException
	 */
	@Override
	void marshalObject(Object object, JSON json) throws ConverterException {
		Errors errors = (Errors) object
		JSONWriter writer = json.getWriter()

		try {
			writer.object()
			writer.key("errors")
			writer.array()

			for (Object o : errors.getAllErrors()) {
				if (o instanceof FieldError) {
					FieldError fe = (FieldError) o
					writer.object()
					json.property("field", fe.getField())
					json.property("rejected-value", fe.getRejectedValue())
					Locale locale = LocaleContextHolder.getLocale()

					if (applicationContext) {
						json.property("message", applicationContext.getMessage(fe, locale))
					} else {
						json.property("message", fe.getDefaultMessage())
					}
					writer.endObject()
				}
			}
			writer.endArray()
			writer.endObject()
		} catch (ConverterException ce) {
			throw ce
		} catch (Exception e) {
			throw new ConverterException("Error converting Bean with class " + object.getClass().getName(), e)
		}
	}

	/**
	 * This method used to set current scope application context.
	 * @param applicationContext Interface provides configuration for an application.
	 */
	@Override
	void setApplicationContext(ApplicationContext applicationContext) {
		println "sa"
		this.applicationContext = applicationContext
	}
}