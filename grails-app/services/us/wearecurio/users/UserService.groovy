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
		return User.countByUsernameIlike(username.trim()) != 0
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

		return User.findByUsernameIlike(username.trim())	// MongoDB matches with case sensitive unlike MySQL
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

	/**
	 * Update the given user with the parameters
	 * @param userInstance Instance of {@link us.wearecurio.users.User User} to update
	 * @param args Map of parameters to update the userInstance for
	 * @return Updated userInstance
	 */
	User update(User userInstance, Map args) {
		List<String> whiteList = ["username", "password", "email"]
		grailsWebDataBinder.bind(userInstance, args as SimpleMapDataBindingSource, whiteList)

		/*
		 * MongoDB search is case sensitive so we can't rely on the Grails internal check of uniqueness since Grails
		 * do the same below criteria to check for uniqueness but doesn't respect the matching case. So manually
		 * matching the email and username below.
		 *
		 * Keeping both the checks for email and username separate so to give specific uniqueness error message.
		 */
		int existingEmailCount = User.withCriteria {
			ilike("email", userInstance.email)
			if (userInstance.id) {
				ne("id", userInstance.id)
			}
			projections {
				rowCount()
			}
		}[0]

		if (existingEmailCount != 0) {
			userInstance.errors.rejectValue("email", "user.email.unique")
			return userInstance
		}

		int existingUsernameCount = User.withCriteria {
			ilike("username", userInstance.username)
			if (userInstance.id) {
				ne("id", userInstance.id)
			}
			projections {
				rowCount()
			}
		}[0]

		if (existingUsernameCount != 0) {
			userInstance.errors.rejectValue("username", "user.username.unique")
			return userInstance
		}

		// If there is a validation failure during save
		if (!Utils.save(userInstance)) {
			return userInstance
		}

		return userInstance
	}
}
