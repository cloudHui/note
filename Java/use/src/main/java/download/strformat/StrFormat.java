package download.strformat;


import java.io.File;
import java.util.List;

import db.business.entity.User;
import stu.FileUtils;

/**
 * 字符格式化
 */
public class StrFormat {


	public static void main(String[] args) {
		//changeLrcMerge();
		changeLrcSpace();
	}


	private static void changeLrcSpace() {
		String path = System.getProperty("user.dir") + "/use" + "/mu/新建文本文档.txt";
		List<String> s = FileUtils.readFileByLine(path);
		FileUtils.deleteFile(path);
		for (String value : s) {
			if ("".equals(value)) {
				FileUtils.writeFileAppendsUtf8(path, "\n", true);
			} else {
				FileUtils.writeFileAppendsUtf8(path, value, true);
			}
		}
		s = FileUtils.readFileByLine(path);
		System.out.println(s);
	}

	private static void changeLrcMerge() {
		String path = System.getProperty("user.dir") + "/use" + "/mu/新建文本文档.txt";
		List<String> yue = FileUtils.readFileByLine(path);

		path = System.getProperty("user.dir") + "/use" + "/mu/result.txt";

		String newPath = System.getProperty("user.dir") + "/use" + "/mu/最爱-中粤对照.txt";
		List<String> cn = FileUtils.readFileByLine(path);

		try {
			File file = new File(newPath);
			file.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (int index = 0; index < cn.size(); index++) {
			FileUtils.writeFileAppendsUtf8(newPath, yue.get(index) + "\n", true);
			FileUtils.writeFileAppendsUtf8(newPath, cn.get(index) + "\n", true);

			FileUtils.writeFileAppendsUtf8(newPath, "\n", true);
		}
		yue = FileUtils.readFileByLine(newPath);
		System.out.println(yue);
	}


	/**
	 * 歌词转两列
	 */
	private static void changeLrcTwoLine() {
		String path = System.getProperty("user.dir") + "/use" + "/mu/新建文本文档.txt";
		List<String> yue = FileUtils.readFileByLine(path);

		String newPath = System.getProperty("user.dir") + "/use" + "/mu/lrc.txt";
		try {
			File file = new File(newPath);
			if (file.exists()) {
				file.delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		int maxLength = 0;
		for (String s : yue) {
			if (s.length() > maxLength) {
				maxLength = s.length();
			}
		}
		int size = yue.size();
		size = size % 2 == 0 ? size / 2 : size / 2 + 1;
		for (int index = 0; index < size; index++) {
			String format = String.format("%-" + (maxLength + 2 + maxLength - yue.get(index).length() / 2) + "s", yue.get(index));
			int space = 0;
			for (int start = 0; start < format.length(); start++) {
				if (format.charAt(start) == ' ') {
					space++;
				}
			}
			if (yue.size() > index + size) {
				format += yue.get(index + size).trim();
			}
			System.out.println(yue.get(index) + "size " + yue.get(index).length() + " space " + space);
			System.out.println(format);
			FileUtils.writeFileAppendsUtf8(newPath, format + "\n", true);
		}
		//yue = FileUtils.readFileByLine(newPath);
		//System.out.println(yue);
	}

	/**
	 * 歌词转三列
	 */
	private static void changeLrcThreeLine() {
		String path = System.getProperty("user.dir") + "/use" + "/mu/新建文本文档.txt";
		List<String> yue = FileUtils.readFileByLine(path);

		String newPath = System.getProperty("user.dir") + "/use" + "/mu/lrc.txt";
		try {
			File file = new File(newPath);
			if (file.exists()) {
				file.delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (int index = 0; index < 4; index++) {
			FileUtils.writeFileAppendsUtf8(newPath, yue.remove(0) + "\n", true);
		}
		int size = yue.size();
		int line = size % 3 == 0 ? size / 3 : size / 3 + 1;
		// 打印三列格式
		for (int index = 0; index < line; index++) {
			String format = String.format("%-20s %-20s %-20s%n",
					yue.get(index),
					index + line < size ? yue.get(index + line) : "",
					index + line * 2 < size ? yue.get(index + line * 2) : "");
			FileUtils.writeFileAppendsUtf8(newPath, format, true);
		}
	}


	private static void changeLrcLineTwoHead() {
		String path = System.getProperty("user.dir") + "/use" + "/mu/新建文本文档.txt";
		List<String> yue = FileUtils.readFileByLine(path);

		String newPath = System.getProperty("user.dir") + "/use" + "/mu/lrc.txt";
		try {
			File file = new File(newPath);
			if (file.exists()) {
				file.delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		int length = 0;
		for (String s : yue) {
			if (s.length() > length) {
				length = s.length();
			}
		}
		int size = yue.size() - 2;
		size = size % 2 == 0 ? size / 2 : size / 2 + 1;
		for (int index = 0; index < size; index++) {
			if (index < 2) {
				FileUtils.writeFileAppendsUtf8(newPath, yue.get(index) + "\n", true);
			} else {
				String format = String.format("%-" + (length + 10 + length - yue.get(index).length() / 2) + "s", yue.get(index));
				if (yue.size() > index + size) {
					format += yue.get(index + size).trim();
				}
				FileUtils.writeFileAppendsUtf8(newPath, format + "\n", true);
			}
		}
	}

	private static void changeLrcLineTwo() {
		String path = System.getProperty("user.dir") + "/use" + "/mu/新建文本文档.txt";
		List<String> yue = FileUtils.readFileByLine(path);

		String newPath = System.getProperty("user.dir") + "/use" + "/mu/lrc.txt";
		try {
			File file = new File(newPath);
			if (file.exists()) {
				file.delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		int length = 0;
		for (String s : yue) {
			if (s.length() > length) {
				length = s.length();
			}
		}
		int size = yue.size() - 2;
		size = size % 2 == 0 ? size / 2 : size / 2 + 1;
		for (int index = 0; index < size; index++) {
			String format = String.format("%-" + (length + 10 + length - yue.get(index).length() / 2) + "s", yue.get(index));
			if (yue.size() > index + size) {
				format += yue.get(index + size).trim();
			}
			FileUtils.writeFileAppendsUtf8(newPath, format + "\n", true);
		}
	}
}
