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

import org.springframework.http.HttpMethod

import grails.plugin.springsecurity.InterceptedUrl
import grails.plugin.springsecurity.ReflectionUtils
import groovy.transform.CompileStatic

/**
 * @author <a href='mailto:burt@burtbeckwith.com'>Burt Beckwith</a>
 */
@CompileStatic
class RequestmapFilterInvocationDefinition extends AbstractFilterInvocationDefinition {

	@Override
	protected void initialize() {
		if (initialized) {
			return
		}

		try {
			reset()
			initialized = true
		}
		catch (RuntimeException e) {
			log.warn("Exception initializing; this is ok if it's at startup and due " +
			         'to GORM not being initialized yet since the first web request will ' +
			         're-initialize. Error message is: {}', e.message)
		}
	}

	/**
	 * Call at startup or when <code>Requestmap</code> instances have been added, removed, or changed.
	 */
	@Override
	synchronized void reset() {
		resetConfigs()

		loadRequestmaps().each { InterceptedUrl iu -> compileAndStoreMapping(iu) }

		log.trace 'configs: {}', configAttributeMap
	}

	protected List<InterceptedUrl> loadRequestmaps() {
		boolean supportsHttpMethod = ReflectionUtils.requestmapClassSupportsHttpMethod()

		ReflectionUtils.loadAllRequestmaps().collect { requestmap ->
			String urlPattern = ReflectionUtils.getRequestmapUrl(requestmap)
			String configAttribute = ReflectionUtils.getRequestmapConfigAttribute(requestmap)
			HttpMethod method = supportsHttpMethod ? ReflectionUtils.getRequestmapHttpMethod(requestmap) : null
			new InterceptedUrl(urlPattern, split(configAttribute), method)
		}
	}
}
