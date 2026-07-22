package test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MatchLogAsy {

	private final static Logger log = LoggerFactory.getLogger(MatchLogAsy.class);


	public static void main(String[] args) {
		MatchLogAsy app = new MatchLogAsy();
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, -1);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");//注意月份是MM
		Date date = calendar.getTime();
		Date dateNow = new Date();
		for (Date dateIndex = date; dateIndex.getTime() < dateNow.getTime(); ) {
			String now = simpleDateFormat.format(dateIndex);
			List<String> fileNames = new ArrayList<>();
			for (int i = 0; i < 4; i++) {
				String path = "D:\\" + now + "-" + i + ".log";
				fileNames.add(path);
			}
			log.info("日期:" + now);
			app.readFileNum(fileNames, now);
			calendar.add(Calendar.DAY_OF_YEAR, 1);
			dateIndex = calendar.getTime();
		}
	}


	public void readFileNum(List<String> fileNames, String now) {
		List<String> strs = new ArrayList<>();
		for (String path : fileNames) {
			File file = new File(path);//Text文件
			readAllFileInArray(file, strs);
		}
		String start = "[", end = "]", cost = "cost:";
		final int[] indexValue = {0};
		Map<String, List<String>> hourPhone = new HashMap<>();

		Map<String, List<String>> totalPhone = new HashMap<>();
		strs.forEach(string -> {
			if (string.contains(cost)) {
				String newStr = string.substring(0, 2);
				int value = Integer.valueOf(newStr);
				string = string.substring(string.indexOf(end) + 1);
				if (string.contains(cost)) {

					if (indexValue[0] == value) {
						put(string, start, end, hourPhone, totalPhone, cost);
					} else {
						log.info(now + " " + indexValue[0] + "点;");
						print(hourPhone);
						indexValue[0] = value;
						hourPhone.clear();
						put(string, start, end, hourPhone, totalPhone, cost);
					}
				}
			}
		});
		log.info(now + " " + indexValue[0] + "点;");
		print(hourPhone);

		log.info("总统计;");
		print(totalPhone);
	}

	private void print(Map<String, List<String>> map) {
		for (Map.Entry<String, List<String>> entry : map.entrySet()) {
			log.info(entry.getKey());
			entry.getValue().forEach(log::info);
		}
	}

	private void put(String string, String stat, String end, Map<String, List<String>> hourPhone,
	                 Map<String, List<String>> totalPhone, String cost) {
		int index = string.indexOf(stat), endIndex = string.indexOf(end), costIndex = string.indexOf(cost),
				ms = string.indexOf("ms]");
		if (ms < 0) {
			ms = string.indexOf("ms,]");
		}
		String newStr = string.substring(index + 1, costIndex - 1);
		String statStr = string.substring(costIndex, ms);
		List<String> hourList = hourPhone.computeIfAbsent(newStr, k -> new ArrayList<>());
		hourList.add(statStr);
		List<String> totalList = totalPhone.computeIfAbsent(newStr, k -> new ArrayList<>());
		totalList.add(statStr);
	}

	private void readAllFileInArray(File file, List<String> strs) {
		StatIntermConcurrent.read(file, strs);
	}

}
