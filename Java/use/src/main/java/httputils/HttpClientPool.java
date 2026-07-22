package httputils;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import javax.net.ssl.SSLContext;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClientPool {
	private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientPool.class);
	public static final String CONTENT_TYPE_TEXT_HTML = "text/xml";
	public static final String CONTENT_TYPE_JSON_URL = "application/json;charset=utf-8";
	public static final String CONTENT_TYPE_WWW_FORM = "application/x-www-form-urlencoded;charset=utf-8";
	private final String CHARSET;
	private PoolingHttpClientConnectionManager pool;
	private ConnectionConfig connectionConfig;
	private RequestConfig requestConfig;
	private CloseableHttpClient httpClient;

	public HttpClientPool() {
		this("UTF-8");
	}

	public HttpClientPool(String charset) {
		this.CHARSET = charset;
		this.setConnectionConfig(4096);
		this.setTimeoutConfig(10000, 10000, 10000);
	}

	public HttpClientPool init(int poolSize) {
		try {
			SSLContextBuilder builder = (new SSLContextBuilder()).loadTrustMaterial(null,
					new TrustSelfSignedStrategy());
			SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(builder.build());


			Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
					.register("http", PlainConnectionSocketFactory.getSocketFactory())
					.register("https", socketFactory).build();
			this.pool = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
			this.pool.setMaxTotal(poolSize);
			this.pool.setDefaultMaxPerRoute(20);
		} catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException var5) {
			LOGGER.error("", var5);
		}

		return this;
	}

	public void setTimeoutConfig(int socketTimeout, int connectTimeout, int requestTimeout) {
		this.requestConfig = RequestConfig.custom().setSocketTimeout(socketTimeout).
				setConnectTimeout(connectTimeout).setConnectionRequestTimeout(requestTimeout).build();
	}

	public void setConnectionConfig(int size) {
		this.connectionConfig = ConnectionConfig.custom().setBufferSize(size).build();
	}

	public CloseableHttpClient getClient() {
		if (null == this.httpClient) {
			synchronized (this) {
				if (null == this.httpClient) {
					this.httpClient = HttpClients.custom().setConnectionManager(this.pool).
							setDefaultRequestConfig(this.requestConfig).setDefaultConnectionConfig
							(this.connectionConfig).setKeepAliveStrategy((httpResponse, httpContext) -> {
						Header[] headers = httpResponse.getAllHeaders();
						if (null != headers) {
							Header header;
							HeaderElement headerElement;
							int i = 0;

							for (int size = headers.length; i < size; ++i) {
								header = headers[i];
								HeaderElement[] headerElements = header.getElements();
								if (null != headerElements) {
									int j = 0;

									for (int jSize = headerElements.length; j < jSize; ++j) {
										headerElement = headerElements[j];
										if (headerElement.getName().toUpperCase().contains("Keep-Alive".toUpperCase())) {
											if (HttpClientPool.isNullOrEmpty(headerElement.getValue())) {
												return 10000L;
											}

											return Long.parseLong(headerElement.getValue()) * 1000L;
										}
									}
								}
							}
						}

						return 10000L;
					}).setRetryHandler(new DefaultHttpRequestRetryHandler()).build();
				}
			}

		}
		return this.httpClient;
	}

	public String sendPost(HttpPost httpPost) {
		String content = null;
		CloseableHttpResponse httpResponse = null;

		try {
			httpPost.setConfig(this.requestConfig);
			CloseableHttpClient httpClient = this.getClient();
			httpResponse = httpClient.execute(httpPost, HttpClientContext.create());
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode != 200) {
				throw new RuntimeException("HTTP Request is not success, Response code is " + statusCode);
			}

			HttpEntity entity = httpResponse.getEntity();
			if (null != entity) {
				content = EntityUtils.toString(entity, this.CHARSET);
				EntityUtils.consume(entity);
			}
		} catch (Exception var14) {
			LOGGER.error("", var14);
		} finally {
			if (null != httpResponse) {
				try {
					httpResponse.close();
				} catch (Exception var13) {
					LOGGER.error("", var13);
				}
			}

		}

		return content;
	}

	public String sendGet(String url) {
		HttpGet httpGet = new HttpGet(url);
		String content = null;
		CloseableHttpResponse httpResponse = null;

		try {
			httpGet.setConfig(this.requestConfig);
			CloseableHttpClient httpClient = this.getClient();
			httpResponse = httpClient.execute(httpGet, HttpClientContext.create());
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode != 200) {
				throw new RuntimeException("HTTP Request is not success, Response code is " + statusCode);
			}

			HttpEntity entity = httpResponse.getEntity();
			if (null != entity) {
				content = EntityUtils.toString(entity, this.CHARSET);
				EntityUtils.consume(entity);
			}
		} catch (Exception var14) {
			LOGGER.error("url:{}", url, var14);
		} finally {
			if (null != httpResponse) {
				try {
					httpResponse.close();
				} catch (IOException var13) {
					LOGGER.error("", var13);
				}
			}

		}

		return content;
	}

	public String sendGetIgnoreSSL(String url) {
		HttpGet httpGet = new HttpGet(url);
		String content = null;
		CloseableHttpResponse httpResponse = null;

		try {
			httpGet.setConfig(this.requestConfig);
			// 创建支持TLSv1.2的HTTP客户端
			SSLContextBuilder sslContextBuilder = new SSLContextBuilder()
					.loadTrustMaterial(null, (chain, authType) -> true);
			SSLContext sslContext = sslContextBuilder.build();

			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
					sslContext,
					new String[]{"TLSv1.2"},
					null,
					SSLConnectionSocketFactory.getDefaultHostnameVerifier()
			);
			CloseableHttpClient httpClient = HttpClients.custom()
					.setSSLSocketFactory(sslsf)
					.build();

			httpResponse = httpClient.execute(httpGet, HttpClientContext.create());
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode != 200) {
				throw new RuntimeException("HTTP Request is not success, Response code is " + statusCode);
			}

			HttpEntity entity = httpResponse.getEntity();
			if (null != entity) {
				content = EntityUtils.toString(entity, this.CHARSET);
				EntityUtils.consume(entity);
			}
		} catch (Exception e) {
			LOGGER.error("url:{}", url, e);
		} finally {
			if (null != httpResponse) {
				try {
					httpResponse.close();
				} catch (IOException e) {
					LOGGER.error("", e);
				}
			}

		}

		return content;
	}

	public String sendPost(String url, Map<String, String> header) {
		if (isNullOrEmpty(url)) {
			return null;
		} else {
			HttpPost httpPost = new HttpPost(url);
			if (null != header && !header.isEmpty()) {
				for (Map.Entry<String, String> entry : header.entrySet()) {
					httpPost.addHeader(entry.getKey(), entry.getValue());
				}
			}
			return this.sendPost(httpPost);
		}
	}

	public String sendPostJsonHead(String url, String content, Map<String, String> header, String contentType) {
		if (isNullOrEmpty(url)) {
			return null;
		} else {
			HttpPost httpPost = new HttpPost(url);
			if (null != header && !header.isEmpty()) {
				for (Map.Entry<String, String> entry : header.entrySet()) {
					httpPost.addHeader(entry.getKey(), entry.getValue());
				}
			}
			if (!isNullOrEmpty(content)) {
				StringEntity stringEntity = new StringEntity(content, this.CHARSET);
				stringEntity.setContentType(contentType);
				httpPost.setEntity(stringEntity);
			}

			return this.sendPost(httpPost);
		}
	}

	public String sendPostJson(String url, String jsonContent) {
		return getString(url, jsonContent, CONTENT_TYPE_JSON_URL);
	}

	public String sendPostXml(String url, String xmlContent) {
		return getString(url, xmlContent, CONTENT_TYPE_TEXT_HTML);
	}

	private String getString(String url, String content, String contentType) {
		if (isNullOrEmpty(url)) {
			return null;
		} else {
			HttpPost httpPost = new HttpPost(url);
			if (!isNullOrEmpty(content)) {
				StringEntity stringEntity = new StringEntity(content, this.CHARSET);
				stringEntity.setContentType(contentType);
				httpPost.setEntity(stringEntity);
			}

			return this.sendPost(httpPost);
		}
	}

	public String sendPostWWWForm(String url, String content) {
		return getString(url, content, CONTENT_TYPE_WWW_FORM);
	}

	private static boolean isNullOrEmpty(String val) {
		return null == val || val.isEmpty();
	}
}
