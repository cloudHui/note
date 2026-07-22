package deepai;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

import deepai.request.ChatRequest;
import deepai.request.Message;
import deepai.response.ChatCompletion;
import utils.utils.JsonUtils;

public class DeepSeekLearn {
	private static final String HEAD = "Bearer ";
	//下面这个key 是何伟忠的  如果测试可以用  大量使用 就要 去 自己申请买一下 https://platform.deepseek.com/api_keys 10块钱
	private static final String KEY = "sk-f8cc953c22ee443798a0893874bb2fb2";

	public static void main(String[] args) throws Exception {
		String model = "客户模式";
		String question = "武汉的房子还会跌吗";
		String request = request(model, question);
		if (request == null) {
			System.out.println("error");
		} else {
			ChatCompletion chatCompletion = JsonUtils.readValue(request, ChatCompletion.class);
			if (chatCompletion == null) {
				System.out.println("result format error ");
			} else {
				System.out.println(chatCompletion.getChoices().get(0).getMessage().getContent());
			}
		}
	}

	/**
	 * 创建链接
	 */
	private static HttpsURLConnection makeConnectData() {
		try {
			URL url = new URL("https://api.deepseek.com/chat/completions");
			HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Authorization", HEAD + KEY);
			connection.setReadTimeout(60000);
			connection.setDoOutput(true);
			return connection;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 生成请求
	 */
	private static ChatRequest makeRequest(String model, String question) {
		ChatRequest request = new ChatRequest();
		ChatRequest chatRequest = new ChatRequest();
		chatRequest.setModel("deepseek-chat");
		chatRequest.setTemperature(1.0);

		// 添加system消息
		Message systemMessage = new Message();
		systemMessage.setRole("system");
		systemMessage.setContent(model);
		request.getMessages().add(systemMessage);

		// 添加user消息
		Message userMessage = new Message();
		userMessage.setRole("user");
		userMessage.setContent(question);
		request.getMessages().add(userMessage);
		return request;
	}

	/**
	 * 请求问题
	 */
	private static String request(String model, String question) {
		HttpsURLConnection connection = makeConnectData();
		if (connection != null) {

			String requestBody = JsonUtils.writeValue(makeRequest(model, question));
			if (requestBody != null) {
				try (OutputStream os = connection.getOutputStream()) {
					os.write(requestBody.getBytes());
				} catch (IOException e) {
					e.printStackTrace();
				}
				// 处理响应
				int responseCode = 0;
				try {
					responseCode = connection.getResponseCode();
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (responseCode == 200) {
					try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
						String line;
						StringBuilder response = new StringBuilder();
						while ((line = br.readLine()) != null) {
							response.append(line);
						}
						return response.toString();
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					System.err.println("请求失败，状态码：" + responseCode);
				}
			}
		}
		return null;
	}
}