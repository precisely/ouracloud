package us.wearecurio.users

import us.wearecurio.utility.Utils

class Role implements Serializable {

	private static final long serialVersionUID = 1

	String authority

	static Role look(String authority) {
		Role role = Role.findByAuthority(authority)

		if (role) return role

		role = new Role(authority)

		Utils.save(role, true)

		return role
	}

	Role(String authority) {
		this()
		this.authority = authority
	}

	@Override
	int hashCode() {
		authority?.hashCode() ?: 0
	}

	@Override
	boolean equals(other) {
		is(other) || (other instanceof Role && other.authority == authority)
	}

	@Override
	String toString() {
		authority
	}

	static constraints = {
		authority blank: false, unique: true, index: true, indexAttributes: [unique: true]
	}

	static mapping = {
		cache true
	}
}
