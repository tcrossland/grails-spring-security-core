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
package grails.plugin.springsecurity.web

import grails.plugin.springsecurity.AbstractUnitSpec
import grails.plugin.springsecurity.SecurityTestUtils

/**
 * Unit tests for <code>SecurityRequestHolder</code>.
 *
 * @author <a href='mailto:burt@burtbeckwith.com'>Burt Beckwith</a>
 */
class SecurityRequestHolderSpec extends AbstractUnitSpec {

	void 'set and get'() {
		expect:
		!SecurityRequestHolder.request
		!SecurityRequestHolder.response

		when:
		SecurityRequestHolder.set request, response

		then:
		request.is(SecurityRequestHolder.request)
		response.is(SecurityRequestHolder.response)
	}

	void 'reset'() {
		expect:
		!SecurityRequestHolder.request
		!SecurityRequestHolder.response

		when:
		SecurityRequestHolder.set request, response

		then:
		request.is(SecurityRequestHolder.request)
		response.is(SecurityRequestHolder.response)

		when:
		SecurityRequestHolder.reset()

		then:
		!SecurityRequestHolder.request
		!SecurityRequestHolder.response
	}

	void testPrivateConstructor() {
		expect:
		SecurityTestUtils.testPrivateConstructor SecurityRequestHolder
	}
}
