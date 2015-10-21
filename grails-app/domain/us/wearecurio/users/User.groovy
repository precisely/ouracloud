package us.wearecurio.users

import grails.rest.*
import us.wearecurio.utility.Utils
import us.wearecurio.services.SecurityService

/**
 * Grails domain class represents a end-user of the platform.
 * @since 0.0.1
 */
class User implements Serializable {

	private static final long serialVersionUID = 1

	Date dateCreated
	Date lastUpdated

	String email
	String username
	String password
	/**
	 * This field should be used by user when they want to disable their account.
	 */
	boolean enabled = true
	boolean accountExpired
	/**
	 * This field should be used for the admin purpose when admin wants to disable any profile.
	 */
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

	def beforeInsert() {
		this.password = encodePassword(this.password)
	}

	def beforeUpdate() {
		if (isDirty("password")) {
			this.password = encodePassword(this.password)
		}
	}

	static constraints = {
		/*
		 * "index" and "indexAttributes" key are for MongoDB. Not using "unique: true" to avoid another query by
		 * Grails to check for uniqueness since that check is not sufficient as MongoDB search is case sensitive.
		 * See, update method in UserService.grooovy.
		 */
		username(blank: false, /*unique: true,*/ index: true, indexAttributes: [unique: true])
		email(blank: false, email: true, /*unique: true,*/ index: true, indexAttributes: [unique: true])
		password(blank: false)
	}

	static mapping = {
	}
}