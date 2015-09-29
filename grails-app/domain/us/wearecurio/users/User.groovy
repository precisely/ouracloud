package us.wearecurio.users

import java.util.Set;

import grails.rest.*
import us.wearecurio.utility.Utils
import us.wearecurio.services.SecurityService

@Resource(uri = '/users', formats = ['json'])
class User implements Serializable {

	private static final long serialVersionUID = 1

	String username
	String password
	boolean enabled = true
	boolean accountExpired
	boolean accountLocked
	boolean passwordExpired

	static User look(String username, String password) {
		User retVal = User.findByUsername(username)

		if (retVal)
			retVal.changePassword(password)
		else
			retVal = new User(username, password)

		Utils.save(retVal, true)

		return retVal
	}

	User(String username, String password) {
		this()
		this.username = username
		this.password = encodePassword(password)
	}

	@Override
	int hashCode() {
		username?.hashCode() ?: 0
	}

	@Override
	boolean equals(other) {
		is(other) || (other instanceof User && other.username == username)
	}

	@Override
	String toString() {
		"User{id=$id, username=$username}"
	}

	Set<Role> getAuthorities() {
		UserRole.findAllByUser(this)*.role
	}

	def changePassword(String password) {
		this.password = encodePassword(password)
	}

	static String encodePassword(String password) {
		SecurityService.get().encodePassword(password)
	}

	static constraints = {
		username blank: false, unique: true
		password blank: false
	}

	static mapping = {
		password column: '`password`'
	}
}
