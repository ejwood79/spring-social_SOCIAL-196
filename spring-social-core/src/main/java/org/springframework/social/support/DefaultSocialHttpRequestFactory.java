package org.springframework.social.support;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.util.Properties;

import org.apache.http.HttpHost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HttpContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

/**
 * Chooses a request factory. Picks a HttpComponentsClientRequestFactory factory if Apache HttpComponents HttpClient is in the classpath.
 * If not, falls back to SimpleClientHttpRequestFactory.
 * @author Craig Walls
 * @author Roy Clarkson
 */
@Component
public class DefaultSocialHttpRequestFactory implements ClientHttpRequestFactory {

	private ClientHttpRequestFactory requestFactory;
	
	private static final boolean HTTP_COMPONENTS_AVAILABLE = ClassUtils.isPresent("org.apache.http.client.HttpClient", ClientHttpRequestFactory.class.getClassLoader());
	
	public DefaultSocialHttpRequestFactory() {
		Properties properties = System.getProperties();
		String proxyHost = properties.getProperty("http.proxyHost");
		int proxyPort = properties.containsKey("http.proxyPort") ? Integer.valueOf(properties.getProperty("http.proxyPort")) : 80;
		
		if (HTTP_COMPONENTS_AVAILABLE) {
			requestFactory = createRequestFactory(proxyHost, proxyPort);
		} else {
			SimpleClientHttpRequestFactory simpleRequestFactory = new SimpleClientHttpRequestFactory();
			if (proxyHost != null) {
				simpleRequestFactory.setProxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort)));
			}
			requestFactory = simpleRequestFactory;
		}
	}
	
	private ClientHttpRequestFactory createRequestFactory(String proxyHost, int proxyPort) {
		
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory() {
			@Override
			protected HttpContext createHttpContext(HttpMethod httpMethod, URI uri) {
				HttpClientContext context = new HttpClientContext();
				context.setAttribute("http.protocol.expect-continue", false);
				return context;
			}
		};
		
		
		if (proxyHost != null) {
			HttpHost proxy = new HttpHost(proxyHost, proxyPort);
			CloseableHttpClient httpClient = HttpClients.custom()
					.setProxy(proxy)
					.build();
			requestFactory.setHttpClient(httpClient);
		}
		
		return requestFactory;
		
	}

	@Override
	public ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod)
			throws IOException {
		return requestFactory.createRequest(uri, httpMethod);
	}
}

