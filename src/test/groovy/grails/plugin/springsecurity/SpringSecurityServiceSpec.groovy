/* Copyright 2006-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package grails.plugin.springsecurity

import org.grails.core.util.ClassPropertyFetcher
import org.springframework.core.annotation.AnnotationUtils
import org.springframework.security.authentication.AuthenticationTrustResolverImpl
import org.springframework.security.authentication.TestingAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder as SCH
import org.springframework.security.core.userdetails.User

import grails.transaction.Transactional

/**
 * Unit tests for SpringSecurityService.
 *
 * @author <a href='mailto:burt@burtbeckwith.com'>Burt Beckwith</a>
 */
class SpringSecurityServiceSpec extends AbstractUnitSpec {

	private SpringSecurityService service = new SpringSecurityService()

	void 'transactional'() {
		expect:
		!ClassPropertyFetcher.forClass(SpringSecurityService).getPropertyValue('transactional')
		SpringSecurityService.methods.any { AnnotationUtils.findAnnotation(it, Transactional) }
	}

	void 'principal authenticated'() {
		expect:
		!service.principal

		when:
		authenticate 'role1'

		then:
		service.principal
	}

	void 'encodePassword'() {
		when:
		service.passwordEncoder = [encodePassword: { String pwd, salt -> pwd + '_encoded' }]

		then:
		'passw0rd_encoded' == service.encodePassword('passw0rd')
	}

	void 'clearCachedRequestmaps'() {
		when:
		boolean resetCalled = false
		service.objectDefinitionSource = [reset: { -> resetCalled = true }]

		service.clearCachedRequestmaps()

		then:
		resetCalled
	}

	void 'getAuthentication'() {
		expect:
		!service.authentication?.principal

		when:
		authenticate 'role1'

		then:
		service.authentication
	}

	void 'isLoggedIn'() {
		when:
		service.authenticationTrustResolver = new AuthenticationTrustResolverImpl()

		then:
		!service.isLoggedIn()

		when:
		authenticate 'role1'

		then:
		service.isLoggedIn()
	}

	private void authenticate(roles) {
		def authorities = SpringSecurityUtils.parseAuthoritiesString(roles)
		def principal = new User('username', 'password', true, true, true, true, authorities)
		def authentication = new TestingAuthenticationToken(principal, null, authorities)
		authentication.authenticated = true
		SCH.context.authentication = authentication
	}
}
