package download.novel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import download.music.HtmlParser;
import httputils.HttpClientPool;
import stu.FileUtils;
import utils.utils.JsonUtils;

/**
 * 下小说
 */
public class DownloadNovel {
	static String DIR_PATH = System.getProperty("user.dir") + "/use" + "/novel/";
	static String SAVE_PATH = "path.txt";
	static String NOVEL_INFO = "down.txt";
	static String NOVEL_INFO_DOWN = "down_finish.txt";
	static String BOOK_NAME = "苟在妖武乱世修仙.txt";
	static String BASE_DIR = "https://m.xqianqian.com/11/11858/all.html";
	static String DOWN_DIR = "https://m.xqianqian.com";
	//static String BASE_DIR = "https://www.xs74w.com/62/62856/";

	static HttpClientPool pool = new HttpClientPool();

	static String next = "下一页";

	static {
		pool.init(1);
	}

	public static void main(String[] args) {
		downNovel();
	}


	/**
	 * 下小说
	 */
	private static void downNovel() {
		//整理下载和章节信息
		List<String> fileAppend = FileUtils.readFileByLine(DIR_PATH + NOVEL_INFO);
		NovelInfo novelInfo;
		int times = 20;
		for (String s : fileAppend) {
			try {
				if (--times <= 0) {
					System.out.println("休息 60s 再下载怕被封了");
					try {
						Thread.sleep(60000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					times = 20;
				}

				novelInfo = JsonUtils.readValue(s, NovelInfo.class);
				if (novelInfo != null && !novelInfo.getDown()) {
					downEach(novelInfo);
					System.out.println("下完了 " + novelInfo.getTitle());
				}
			} catch (Exception e) {
				System.out.println("失败 " + s);
				e.printStackTrace();
			}

		}
		System.out.println("都下完了 " + BOOK_NAME);
	}

	private static void downEach(NovelInfo novelInfo) {
		int tryTimes = 0;
		boolean downEachChapter = downEachChapter(novelInfo);
		while (!downEachChapter && ++tryTimes < 3) {
			System.out.println("休息 15s 再次尝试 超过三次不下载了 重试第 " + tryTimes + " 次");
			try {
				Thread.sleep(15000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			downEachChapter = downEachChapter(novelInfo);
		}
		if (downEachChapter) {
			novelInfo.setDown(true);
			writeNovelInfo(novelInfo, DIR_PATH + NOVEL_INFO_DOWN);
		}
	}

	/**
	 * 需要额外处理下载地址的
	 */
	private static void dealGetDownUrl() {
		//String url = BASE_DIR;
		//String result;
		while (true) {
			//result = pool.sendGetIgnoreSSL(url);
			//保存整理好的下载地址
			//FileUtils.writeFileAppendsUtf8(DIR_PATH + SAVE_PATH, result, false);
			List<NovelInfo> novelInfos = dealChapterAndDownInfo();
			FileUtils.deleteFile(DIR_PATH + NOVEL_INFO);
			for (NovelInfo novelInfo : novelInfos) {
				writeNovelInfo(novelInfo, DIR_PATH + NOVEL_INFO);
			}
			//int indexOf = result.indexOf(next);
			//if (indexOf != -1) {
			//	try {
			//		result = result.substring(0, indexOf);
			//		String[] split = result.split("<a");
			//		url = split[split.length - 1];
			//		url = url.substring(url.indexOf("href") + 6, url.indexOf("class") - 2);
			//	} catch (Exception e) {
			//		e.printStackTrace();
			//	}
			//} else {
			break;
			//}
		}
	}

	/**
	 * 写入下载信息
	 */
	private static void writeNovelInfo(NovelInfo novelInfo, String path) {
		FileUtils.writeFileAppendsUtf8(path, novelInfo.toString(), true);
		FileUtils.writeFileAppendsUtf8(path, "\n", true);
	}

	/**
	 * 整理下载和章节信息
	 */
	private static List<NovelInfo> dealChapterAndDownInfo() {
		List<String> list = FileUtils.readFileByLine(DIR_PATH + SAVE_PATH);
		List<NovelInfo> chapterUrl = new ArrayList<>();
		int starUrl;
		int endUrl;
		int starTitle;
		int endTitle;
		String url;
		String title;
		int chapter;
		for (String value : list) {
			if (value.contains("第") && value.contains("章")) {
				starUrl = value.indexOf("href") + 7;
				endUrl = value.indexOf("第") - 2;
				try {
					starTitle = endUrl + 2;
					endTitle = value.indexOf("</a>");
					url = value.substring(starUrl, endUrl);
					title = value.substring(starTitle, endTitle);
					chapter = Integer.parseInt(title.substring(title.indexOf("第") + 1, title.indexOf("章")));
					chapterUrl.add(new NovelInfo(chapter, title, url));
				} catch (Exception e) {
					try {
						starTitle = value.indexOf("title") + 7;
						endTitle = value.indexOf("rel") - 2;
						title = value.substring(starTitle, endTitle);
						url = value.substring(starUrl, endUrl);
						chapter = Integer.parseInt(title.substring(title.indexOf("第") + 1, title.indexOf("章")));
						chapterUrl.add(new NovelInfo(chapter, title, url));
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		}
		Collections.sort(chapterUrl);
		return chapterUrl;
	}

	/**
	 * 下载每一章节
	 */
	private static boolean downEachChapter(NovelInfo novelInfo) {
		String url = novelInfo.getUrl();
		url = url.split("\"")[0];
		String getIgnoreSSL = pool.sendGetIgnoreSSL(url);
		if (getIgnoreSSL == null) {
			System.out.println(url + "   失败了 " + novelInfo);
			return false;
		}

		String[] split = getIgnoreSSL.split(novelInfo.title);
        String[] temp;
        for (String result : split) {
            if (!result.contains("&nbsp;") || result.contains("目录")) {
                continue;
            }
            result = result.replace("<br/>", "");
            result = result.replace("<br />", "");
            result = result.replace("<br>", "");
            result = result.replace("<br />", "");
            result = result.replace("&nbsp;", "");
            result = result.trim();
            temp = result.split("记住手机版网址");
            result = temp[0];
            temp = result.split("script>");
            result = temp[2];
            if (result.equals("")) {
                continue;
            }
            writeTxt(novelInfo.title);
            writeTxt("\n");
            writeTxtOther(result);
            writeTxt(" \n");
        }
        return true;
    }


	/**
	 * 下载每一章节
	 */
	private static void downEveryChapter(NovelInfo novelInfo) {
		String url = novelInfo.getUrl();
		do {
			url = url.split("\"")[0];
			String result = pool.sendGetIgnoreSSL(url);
			if (result == null) {
				System.out.println(url + "   失败了 " + novelInfo.toString());
				break;
			}
			String[] novel = HtmlParser.getDosString(url);
			if (novel != null) {
				String transform = transform(novel);
				writeTxt(transform);
			} else {
				System.out.println(" failt url:" + url);
			}
			url = getNextPage(result);
		} while (url != null);
	}

	public static String transform(String[] content) {
		// 1. 将 content 按逗号分割成数组
		String[] arrays = content[0].split(",");

		// 2. 使用正则表达式移除 chapter 中的方括号及其内容
		Pattern pattern = Pattern.compile("\\[.*?\\]");
		Matcher matcher = pattern.matcher(content[1]);
		String chapter = matcher.replaceAll("");

		// 3. 将 chapter 按 "<br><br>" 分割成数组
		String[] datas = chapter.split("<br><br>");

		// 4. 初始化结果字符串构建器
		StringBuilder result = new StringBuilder();

		// 5. 获取第一个数组元素作为偏移量
		int el = Integer.parseInt(arrays[0]);

		// 6. 遍历 datas 数组，根据 arrays 中的索引构建结果
		for (int i = 1; i < arrays.length; i++) {
			// 计算实际索引
			int index = Integer.parseInt(arrays[i]) - el;
			if (index >= 0 && index < datas.length) {
				result.append(datas[index]).append("\n");
			}
		}

		// 7. 返回结果
		return result.toString().trim(); // 去掉最后多余的换行符
	}


	/**
	 * 写文本
	 */
	private static void writeTxt(String novel) {
		String[] split = novel.split("\n");
		for (String value : split) {
			FileUtils.writeFileAppendsUtf8(DIR_PATH + BOOK_NAME, value + "\n", true);
		}
	}

	/**
	 * 写文本
	 */
	private static void writeTxtOther(String novel) {
		String[] split = novel.split("。");
		for (String value : split) {
			FileUtils.writeFileAppendsUtf8(DIR_PATH + BOOK_NAME, value + "。\n", true);
		}
	}

	/**
	 * 获取下一页下载
	 */
	private static String getNextPage(String result) {
		int star = result.indexOf("next");
		int end = result.indexOf(">下一页<");
		if (star > 0 && end > 0) {
			String url = result.substring(star + 13, end - 2);
			return BASE_DIR + "/" + url;
		}
		return null;
	}


	/**
	 * 小说信息
	 */
	static class NovelInfo implements Comparable<NovelInfo> {
		private int chapter;

		private String title;

		private String url;

		private boolean down;

		//没有get set 反序列化不出来
		public int getChapter() {
			return chapter;
		}

		public void setChapter(int chapter) {
			this.chapter = chapter;
		}

		public String getTitle() {
			return title;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public boolean getDown() {
			return down;
		}

		public void setDown(boolean down) {
			this.down = down;
		}

		//没有这个无参构造 反序列化不了
		public NovelInfo() {
		}

		public NovelInfo(int chapter, String title, String url) {
			this.chapter = chapter;
			this.title = title;
			this.url = DOWN_DIR + url;
			//String[] split = this.url.split("/");
			//this.url = DOWN_DIR + split[split.length - 1];
		}

		@Override
		public int compareTo(DownloadNovel.NovelInfo o) {
			return Integer.compare(chapter, o.chapter);
		}

		@Override
		public String toString() {
			return JsonUtils.writeValue(this);
		}
	}
}
