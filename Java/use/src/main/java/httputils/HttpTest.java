package httputils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author admin
 * @className HttpTest
 * @description 为了测试我http服务的耐用性  好像超不多每毫秒都能立马处理完耐用性很好
 * @createDate 2025/3/14 2:10
 */
public class HttpTest {
	static final String URL = "http://172.20.16.119:5401/getGate";
	static final String JSON = "getGate";

	static final int MAX = 1000;

	public static void main(String[] args) {
		loopRequest();
	}


	private static void loopRequest() {
		long start = System.currentTimeMillis();
		for (int index = 0; index < MAX; index++) {
			request();
		}
		System.out.println(" total cost:" + (System.currentTimeMillis() - start) + "ms");
	}

	private static void threadRequest() {
		long start = System.currentTimeMillis();
		ExecutorService executor = Executors.newFixedThreadPool(1000);
		for (int index = 0; index < MAX; index++) {
			executor.submit(HttpTest::request);
		}
		executor.shutdown();
		while (true) {
			if (executor.isTerminated()) {
				System.out.println(" total cost:" + (System.currentTimeMillis() - start) + "ms");
				break;
			}
		}
	}

	private static void request() {
		HttpClientPool pool = new HttpClientPool();
		pool.init(1);
		pool.sendPostJson(URL, JSON);
	}
}
