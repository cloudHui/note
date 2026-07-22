package test;

import db.business.entity.TypeEx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Test {

	private final static Logger log = LoggerFactory.getLogger(Test.class);


	public static void main(String[] args) {
		Test app = new Test();
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, -7);
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
		String start = ">", end = "stat:", cost = "cost:";
		final int[] indexValue = {0};
		Map<String, List<String>> hourPhone = new HashMap<>();

		Map<String, List<String>> totalPhone = new HashMap<>();
		strs.forEach(string -> {
			if (string.contains(start) && string.contains(end)) {
				String newStr = string.substring(0, 2);
				int value = Integer.parseInt(newStr);

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
		});
		log.info(now + " " + indexValue[0] + "点;");
		print(hourPhone);

		log.info("总统计;");
		print(totalPhone);
	}

	private void print(Map<String, List<String>> map) {
		List<TypeEx> list = new ArrayList<>();
		for (Map.Entry<String, List<String>> entry : map.entrySet()) {
			TypeEx ex = new TypeEx();
			ex.setNum(Integer.parseInt(entry.getKey()));
			ex.setType(entry.getValue());
			list.add(ex);
		}
		Collections.sort(list);
		for (int index = 0; index < list.size() - 1; index++) {
			List<String> ex = list.get(index).getType(), ex1 = list.get(index + 1).getType();
			for (String str : ex1) {
				ex.remove(str);
			}
			log.info(list.get(index).getNum() + "  ");
			if (ex.size() > 0) {
				ex.forEach(log::info);
			}
		}
	}

	private void put(String string, String stat, String end, Map<String, List<String>> hourPhone,
	                 Map<String, List<String>> totalPhone, String cost) {
		int index = string.indexOf(stat), endIndex = string.indexOf(end), costIndex = string.indexOf(cost);
		String newStr = string.substring(index + 1, endIndex - 1);
		String statStr = string.substring(endIndex + 5, costIndex - 1);
		List<String> hourList = hourPhone.computeIfAbsent(statStr, k -> new ArrayList<>());
		hourList.add(newStr);
		List<String> totalList = totalPhone.computeIfAbsent(statStr, k -> new ArrayList<>());
		totalList.add(newStr);
	}

	private void readAllFileInArray(File file, List<String> strs) {
		StatIntermConcurrent.read(file, strs);
	}

}
