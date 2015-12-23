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

import grails.plugin.springsecurity.InterceptedUrl
import grails.plugin.springsecurity.SpringSecurityUtils

import org.springframework.mock.web.MockFilterChain
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.web.FilterInvocation

/**
 * Unit tests for RequestmapFilterInvocationDefinition.
 *
 * @author <a href='mailto:burt@burtbeckwith.com'>Burt Beckwith</a>
 */
class RequestmapFilterInvocationDefinitionTests extends AbstractFilterInvocationDefinitionTests {

	private RequestmapFilterInvocationDefinition fid = new TestRequestmapFilterInvocationDefinition()

	protected void tearDown() {
		super.tearDown()
		SpringSecurityUtils.resetSecurityConfig()
	}

	void testSplit() {
		assert ['ROLE_1', 'ROLE_2', 'ROLE_3', 'ROLE_4', 'ROLE_5'] == fid.split('ROLE_1, ROLE_2,,,ROLE_3 ,ROLE_4,ROLE_5')
		assert ['hasAnyRole("ROLE_1","ROLE_2")'] == fid.split('hasAnyRole("ROLE_1","ROLE_2")')
	}

//	void testLoadRequestmaps() {
//		def requestMapConfig = SpringSecurityUtils.securityConfig.requestMap
//		requestMapConfig.className = TestRequestmap.name
//		requestMapConfig.urlField = 'urlPattern'
//		requestMapConfig.configAttributeField = 'rolePattern'
//
//		def instances = [new TestRequestmap(urlPattern: 'path1', rolePattern: 'config1'),
//		                 new TestRequestmap(urlPattern: 'path2', rolePattern: 'config2'),
//		                 new TestRequestmap(urlPattern: 'path3', rolePattern: 'config3')]
//		mockDomain TestRequestmap, instances
//
//		def requestmaps = fid.loadRequestmaps()
//		assert 3 == requestmaps.size()
//		assert 'config1' == requestmaps.path1
//		assert 'config2' == requestmaps.path2
//		assert 'config3' == requestmaps.path3
//	}

	void testStoreMapping() {

		assert !fid.configAttributeMap

		fid.storeMapping '/foo/bar', null, ['ROLE_ADMIN']
		assert 1 == fid.configAttributeMap.size()

		fid.storeMapping '/foo/bar', null, ['ROLE_USER']
		assert 1 == fid.configAttributeMap.size()

		fid.storeMapping '/other/path', null, ['ROLE_SUPERUSER']
		assert 2 == fid.configAttributeMap.size()
	}

	void testReset() {

		fid.roleVoter = ctx.getBean('roleVoter')
		fid.authenticatedVoter = ctx.getBean('authenticatedVoter')

		assert !fid.configAttributeMap

		fid.reset()

		assert 2 == fid.configAttributeMap.size()
	}

	void testInitialize() {

		fid.roleVoter = ctx.getBean('roleVoter')
		fid.authenticatedVoter = ctx.getBean('authenticatedVoter')

		assert !fid.configAttributeMap

		fid.initialize()
		assert 2 == fid.configAttributeMap.size()

		fid.resetConfigs()

		fid.initialize()
		assert !fid.configAttributeMap
	}

	void testDetermineUrl() {

		def request = new MockHttpServletRequest()
		def response = new MockHttpServletResponse()
		def chain = new MockFilterChain()
		request.contextPath = '/context'

		request.requestURI = '/context/foo'
		assert '/foo' == fid.determineUrl(new FilterInvocation(request, response, chain))

		request.requestURI = '/context/fOo/Bar?x=1&y=2'
		assert '/foo/bar' == fid.determineUrl(new FilterInvocation(request, response, chain))
	}

	void testSupports() {
		assert fid.supports(FilterInvocation)
	}
}

class TestRequestmapFilterInvocationDefinition extends RequestmapFilterInvocationDefinition {
	protected List<InterceptedUrl> loadRequestmaps() {
		[new InterceptedUrl('/foo/bar', ['ROLE_USER'], null), new InterceptedUrl('/admin/**', ['ROLE_ADMIN'], null)]
	}
}
