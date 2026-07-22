package test;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class CountName {

	public static void main(String[] args) {

		CountName app = new CountName();
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");//注意月份是MM
		Date dateNow = new Date();
		String now = simpleDateFormat.format(dateNow);
		List<String> fileNames = new ArrayList<>();
		String path = "D:\\" + now + "-" + 0 + ".log";
		fileNames.add(path);
		System.out.println("日期:" + now);
		app.readFileNum(fileNames);
		calendar.add(Calendar.DAY_OF_YEAR, 1);
	}

	public void readFileNum(List<String> fileNames) {
		List<String> strs = new ArrayList<>();
		for (String path : fileNames) {
			File file = new File(path);//Text文件
			readAllFileInArray(file, strs);
		}
		String updateUserWin = "UpdateUserWin", name = "name:", channel = "channel";
		Map<String, Integer> hourPhone = new HashMap<>();

		strs.forEach(string -> {
			if (string.contains(updateUserWin) && string.contains(name) && string.contains(channel)) {
				String id = string.substring(string.indexOf(name) + 5, string.indexOf(channel) - 2);
				boolean intOrNot = isInteger(id);
				if (intOrNot) {
					hourPhone.put(id, 1);
				}
			}
		});
		System.out.println("size:" + hourPhone.size());
	}

	public static boolean isInteger(String str) {
		String patt = "^[-+]?[\\d]*$";
		Pattern pattern = Pattern.compile("patt");
		return pattern.matcher(str).matches();
	}

	private void readAllFileInArray(File file, List<String> strs) {
		StatIntermConcurrent.read(file, strs);
	}
}
