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
package grails.plugin.springsecurity.web.authentication

import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.web.RedirectStrategy

import grails.plugin.springsecurity.AbstractUnitSpec
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.plugin.springsecurity.web.SecurityRequestHolder

/**
 * @author <a href='mailto:burt@burtbeckwith.com'>Burt Beckwith</a>
 */
class AjaxAwareAuthenticationFailureHandlerSpec extends AbstractUnitSpec {

	private final AjaxAwareAuthenticationFailureHandler handler = new AjaxAwareAuthenticationFailureHandler()

	void setup() {
		SecurityRequestHolder.set request, response
	}

	void 'onAuthenticationFailure not Ajax'() {

		when:
		String defaultFailureUrl = '/defaultFailureUrl'
		handler.defaultFailureUrl = defaultFailureUrl
		handler.ajaxAuthenticationFailureUrl = '/ajaxAuthenticationFailureUrl'

		boolean redirectCalled = false
		def sendRedirect = { req, res, url ->
			redirectCalled = true
			assert defaultFailureUrl == url
		}
		handler.redirectStrategy = [sendRedirect: sendRedirect] as RedirectStrategy

		SpringSecurityUtils.securityConfig = [ajaxHeader: 'ajaxHeader'] as ConfigObject

		handler.onAuthenticationFailure request, response, new BadCredentialsException('fail')

		then:
		redirectCalled
	}

	void 'onAuthenticationFailure Ajax'() {

		when:
		String ajaxAuthenticationFailureUrl = '/ajaxAuthenticationFailureUrl'
		handler.defaultFailureUrl = '/defaultFailureUrl'
		handler.ajaxAuthenticationFailureUrl = ajaxAuthenticationFailureUrl

		boolean redirectCalled = false
		def sendRedirect = { req, res, url ->
			redirectCalled = true
			assert ajaxAuthenticationFailureUrl == url
		}
		handler.redirectStrategy = [sendRedirect: sendRedirect] as RedirectStrategy

		SpringSecurityUtils.securityConfig = [ajaxHeader: 'ajaxHeader'] as ConfigObject

		request.addHeader 'ajaxHeader', 'XMLHttpRequest'
		handler.onAuthenticationFailure request, response, new BadCredentialsException('fail')

		then:
		redirectCalled
	}

	void 'afterPropertiesSet'() {
		when:
		handler.afterPropertiesSet()

		then:
		thrown AssertionError

		when:
		handler.ajaxAuthenticationFailureUrl = 'url'
		handler.afterPropertiesSet()

		then:
		notThrown AssertionError
	}
}
