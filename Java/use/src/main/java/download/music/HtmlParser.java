package download.music;

import java.io.IOException;
import java.util.Arrays;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HtmlParser {


	/**
	 * 获取doc 内容
	 */
	public static String[] getDosString(String url) {
		// 1. 加载 HTML 页面（可以从文件、URL 或字符串加载）
		try {
			// 从 URL 加载 HTML 页面
			Document doc = Jsoup.connect(url).get();

			// 2. 使用选择器选择具有 ID 'ad' 的元素
			Element adElement = doc.getElementById("chapter");

			String[] results = new String[2];
			// 3. 检查是否找到该元素
			if (adElement != null) {
				// 4. 获取元素的文本内容
				results[0] = adElement.text();
				Elements select = doc.select("#ad");
				results[1] = select.text();
				System.out.println("元素 chapter 的文本内容: " + Arrays.toString(results));
				return results;
			} else {
				System.out.println("未找到 ID 为 'ad' 的元素");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}