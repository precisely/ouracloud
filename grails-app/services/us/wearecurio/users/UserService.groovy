package us.wearecurio.users

import grails.transaction.Transactional
import org.codehaus.groovy.grails.web.binding.GrailsWebDataBinder
import org.grails.databinding.SimpleMapDataBindingSource
import us.wearecurio.utility.Utils

/**
 * Grails service used to perform {@link us.wearecurio.users.User} related operations.
 *
 * @author Shashank Agrawal
 * @since 0.0.1
 */
@Transactional
class UserService {

	GrailsWebDataBinder grailsWebDataBinder

	/**
	 * Check if a user exists with the given username.
	 * @param username The username to search user for
	 * @return <code>true</code> if user exists otherwise <code>false</code>
	 */
	boolean exists(String username) {
		return User.countByUsername(username.trim()) != 0
	}

	/**
	 * Get the instance of {@link us.wearecurio.users.User} with given username.
	 * @param username The username to search user for
	 * @return The instance of User as described above or <code>null</code> if no user found.
	 */
	User look(String username) {
		if (!username) {
			return null
		}

		return User.findByUsername(username.trim())
	}

	/**
	 * Create a new user with the given arguments giving the "ROLE_USER" {@link us.wearecurio.users.Role}.
	 * @param args Required arguments for user creation: <code>username, password</code>/
	 * @return Newly created instance of User. The instance can have validation errors if a user already exists with
	 * the given username.
	 */
	User create(Map args) {
		User userInstance = new User()
		update(userInstance, args)

		if (userInstance.hasErrors()) {
			return userInstance
		}

		UserRole.create(userInstance, Role.look("ROLE_USER"))
		return userInstance
	}

	User createWithRoles(Map args, List<String> roles) {
		User userInstance = create(args)
		if (userInstance.hasErrors()) {
			return userInstance
		}

		roles.each { authority ->
			UserRole.create(userInstance, Role.look(authority))
		}

		return userInstance
	}

	User update(User userInstance, Map args) {
		List<String> whiteList = ["username", "password"]
		grailsWebDataBinder.bind(userInstance, args as SimpleMapDataBindingSource, whiteList)

		// If there is a validation failure during save
		if (!Utils.save(userInstance)) {
			return userInstance
		}

		return userInstance
	}
}
