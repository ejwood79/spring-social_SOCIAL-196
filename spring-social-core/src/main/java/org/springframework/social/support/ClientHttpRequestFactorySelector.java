/*
 * Copyright 2013 the original author or authors.
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
package org.springframework.social.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;

@Component
public class ClientHttpRequestFactorySelector {
	
	private static ClientHttpRequestFactory httpRequestFactory;

	public ClientHttpRequestFactory getHttpRequestFactory() {
		return httpRequestFactory;
	}

	public static ClientHttpRequestFactory foo() {
		return new HttpComponentsClientHttpRequestFactory();
	}	
	
	public static ClientHttpRequestFactory getRequestFactory() {
		return httpRequestFactory;
	}
	
	/**
	 * Decorates a request factory to buffer responses so that the responses may be repeatedly read.
	 * @param requestFactory the request factory to be decorated for buffering
	 * @return a buffering request factory
	 */
	public static ClientHttpRequestFactory bufferRequests(ClientHttpRequestFactory requestFactory) {
		return new BufferingClientHttpRequestFactory(requestFactory);
	}

	@Autowired(required = true)
	private void setHttpRequestFactory(
			ClientHttpRequestFactory httpRequestFactory) {
		ClientHttpRequestFactorySelector.httpRequestFactory = httpRequestFactory;
	}
	
}
