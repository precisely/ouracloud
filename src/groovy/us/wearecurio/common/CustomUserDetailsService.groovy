/*
 * Copyright (c) 2014, Neighbor Marketing Inc 2014.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are not permitted.
 */

package us.wearecurio.common

import grails.plugin.springsecurity.SpringSecurityUtils
import grails.plugin.springsecurity.userdetails.GrailsUser
import grails.plugin.springsecurity.userdetails.GrailsUserDetailsService
import org.springframework.dao.DataAccessException
import org.springframework.security.core.authority.GrantedAuthorityImpl
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import us.wearecurio.users.User
/**
 * This is a custom service class for SpringSecurityCore plugin to provide a different login by doing case insensitive
 * search of email or username for MongoDB so that user can login via username or email.
 * 
 * @author Shashank Agrawal
 * @since 0.0.1
 * @see resources.groovy
 */
class CustomUserDetailsService implements GrailsUserDetailsService {

	private static final List NO_ROLES = [new SimpleGrantedAuthority(SpringSecurityUtils.NO_ROLE)]

	@Override
	UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {
		User.withTransaction {
			User userInstance = User.withCriteria {
				or {
					ilike("email", username)
					ilike("username", username)
				}
			}[0]

			if (!userInstance) {
				throw new UsernameNotFoundException("User not found", username)
			}

			List authorities = userInstance.authorities.collect {
				new GrantedAuthorityImpl(it.authority)
			}

			new GrailsUser(userInstance.email, userInstance.password, userInstance.enabled, !userInstance.accountExpired,
					!userInstance.passwordExpired, !userInstance.accountLocked, authorities ?: NO_ROLES, userInstance.id)
		}
	}

	@Override
	UserDetails loadUserByUsername(String username, boolean loadRoles) throws UsernameNotFoundException, DataAccessException {
		loadUserByUsername(username)
	}
}