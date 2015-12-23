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
package grails.plugin.springsecurity.web.access.intercept

import org.springframework.security.web.access.intercept.DefaultFilterInvocationSecurityMetadataSource
import org.springframework.security.web.util.matcher.AntPathRequestMatcher

import grails.plugin.springsecurity.AbstractUnitSpec

/**
 * @author <a href='mailto:burt@burtbeckwith.com'>Burt Beckwith</a>
 */
class ChannelFilterInvocationSecurityMetadataSourceFactoryBeanSpec extends AbstractUnitSpec {

	private ChannelFilterInvocationSecurityMetadataSourceFactoryBean factory = new ChannelFilterInvocationSecurityMetadataSourceFactoryBean()

	void 'getObjectType'() {
		expect:
		DefaultFilterInvocationSecurityMetadataSource.is(factory.objectType)
	}

	void 'isSingleton'() {
		expect:
		factory.singleton
	}

	void 'afterPropertiesSet'() {
		when:
		factory.afterPropertiesSet()

		then:
		thrown AssertionError

		when:
		factory.afterPropertiesSet()

		then:
		thrown AssertionError

		when:
		factory.definition = [[pattern: '/foo1/**', access: 'secure_only']]
		factory.afterPropertiesSet()

		then:
		thrown AssertionError

		when:
		factory.definition = [[pattern: '/foo1/**', access: 'REQUIRES_SECURE_CHANNEL']]
		factory.afterPropertiesSet()

		then:
		notThrown AssertionError
	}

	void 'getObject'() {
		when:
		factory.definition = [
			[pattern: '/foo1/**', access: 'REQUIRES_SECURE_CHANNEL'],
			[pattern: '/foo2/**', access: 'REQUIRES_INSECURE_CHANNEL'],
			[pattern: '/foo3/**', access: 'ANY_CHANNEL']
		]
		factory.afterPropertiesSet()

		def object = factory.object

		then:
		object instanceof DefaultFilterInvocationSecurityMetadataSource

		when:
		def map = object.@requestMap

		then:
		'REQUIRES_SECURE_CHANNEL'   == map[new AntPathRequestMatcher('/foo1/**')].attribute[0]
		'REQUIRES_INSECURE_CHANNEL' == map[new AntPathRequestMatcher('/foo2/**')].attribute[0]
		'ANY_CHANNEL'               == map[new AntPathRequestMatcher('/foo3/**')].attribute[0]
	}
}
