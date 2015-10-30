package us.wearecurio.users

import us.wearecurio.BaseIntegrationSpec

class UserServiceSpec extends BaseIntegrationSpec {

	void "test create when user exists with the same email or username with different case"() {
		given: "An existing user"
		assert userInstance.email == "testuser1@ouraring.com"
		assert userInstance.username == "testuser1"

		when: "A new user is created with same username with different case"
		User newUserInstance = userService.create([username: "testUser1", email: "testuser2@ouraring.com", password:
				"xyz1234"])
		flushSession()

		then: "New user should not be created"
		newUserInstance != null
		newUserInstance.id == null
		newUserInstance.hasErrors() == true
		newUserInstance.errors.getAllErrors().size() == 1
		newUserInstance.errors.getFieldErrors("username")[0].codes.find { it.contains("user.username.unique") } != null

		when: "A new user is created with same email with different case"
		newUserInstance = userService.create([username: "testUser2", email: "Testuser1@ourarinG.com", password:
				"xyz1234"])
		flushSession()

		then: "New user should not be created"
		newUserInstance != null
		newUserInstance.id == null
		newUserInstance.hasErrors() == true
		newUserInstance.errors.getAllErrors().size() == 1
		newUserInstance.errors.getFieldErrors("email")[0].codes.find { it.contains("user.email.unique") } != null

		when: "A new user is created with same email and username with different case"
		newUserInstance = userService.create([username: "testUser1", email: "Testuser1@ourarinG.com", password:
				"xyz1234"])
		flushSession()

		then: "New user should not be created"
		newUserInstance != null
		newUserInstance.id == null
		newUserInstance.hasErrors() == true
		newUserInstance.errors.getAllErrors().size() == 2
		newUserInstance.errors.getFieldErrors("email")[0].codes.find { it.contains("user.email.unique") } != null
		newUserInstance.errors.getFieldErrors("username")[0].codes.find { it.contains("user.username.unique") } != null
	}

	void "test update user when user exists with the same email or username with different case"() {
		given: "An existing user"
		User userInstance1 = userService.create([username: "johndoe", email: "johndoe@ouraring.com", password:
				"xyz1234"])

		assert userInstance1 != null
		assert userInstance1.id != null

		when: "The user is updated with an existing username"
		userService.update(userInstance1, [username: userInstance.username, email: userInstance1.email])
		flushSession()

		then: "The user should not be updated"
		userInstance1.hasErrors() == true
		userInstance1.errors.getAllErrors().size() == 1
		userInstance1.errors.getFieldErrors("username")[0].codes.find { it.contains("user.username.unique") } != null
	}

	void "test update user"() {
		given: "An existing user"
		User userInstance1 = userService.create([username: "johndoe", email: "johndoe@ouraring.com", password:
				"xyz1234"])

		assert userInstance1 != null
		assert userInstance1.id != null

		when: "The user is updated"
		userService.update(userInstance1, [username: "donaldDuck", email: userInstance1.email])
		flushSession()

		then: "The user should be updated"
		userInstance1.hasErrors() == false
		userInstance1.refresh().username == "donaldDuck"
	}
}
