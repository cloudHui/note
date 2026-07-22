package httputils; /**
 * @ClassName SSLClientCustom
 * @Description: 类描述-Https忽略证书
 * @Author junqi
 * @Date 2020/10/26
 * @Version V1.0
 **/

import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;

/**
 * Https忽略证书
 */
public class SSLClientCustom {
	private static final String HTTP = "http";
	private static final String HTTPS = "https";
	private static SSLConnectionSocketFactory sslConnectionSocketFactory = null;
	private static PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = null;//连接池管理类

	static {
		try {
			// 信任所有站点 直接返回true
			//管理Https连接的上下文类
			SSLContextBuilder sslContextBuilder = new SSLContextBuilder().loadTrustMaterial(null,
					(TrustStrategy) (x509Certificates, s) -> {
//                    信任所有站点 直接返回true
						return true;
					});
			sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContextBuilder.build(), new String[] { "SSLv2Hello", "SSLv3", "TLSv1", "TLSv1.2" }, null, NoopHostnameVerifier.INSTANCE);
			Registry<ConnectionSocketFactory> registryBuilder = RegistryBuilder.<ConnectionSocketFactory>create()
					.register(HTTP, new PlainConnectionSocketFactory())
					.register(HTTPS, sslConnectionSocketFactory)
					.build();
			poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager(registryBuilder);
			poolingHttpClientConnectionManager.setMaxTotal(200);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 获取连接
	 *
	 * @return
	 */
	public static CloseableHttpClient getHttpClient() {
		return HttpClients.custom().setSSLSocketFactory(sslConnectionSocketFactory)
				.setConnectionManager(poolingHttpClientConnectionManager)
				.setConnectionManagerShared(true)
				.build();
	}
}
