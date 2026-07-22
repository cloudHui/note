package test;

import db.business.entity.TypeBeanEx;
import tool.Arith;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 测试
 */
public class CountUnicode {

	public static void main(String[] args) {

		CountUnicode app = new CountUnicode();
		Calendar calendar = Calendar.getInstance();
//        calendar.add(Calendar.DAY_OF_YEAR, -5);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");//注意月份是MM
		Date date = calendar.getTime();
		Date dateNow = new Date();
		for (Date dateIndex = date; dateIndex.getTime() < dateNow.getTime(); ) {
			String now = simpleDateFormat.format(dateIndex);
			List<String> fileNames = new ArrayList<>();
			for (int i = 0; i < 10; i++) {
				String path = "D:\\" + now + "-" + i + ".log";
				fileNames.add(path);
			}
			System.out.println("日期:" + now);
			app.readFileNum(fileNames);
			calendar.add(Calendar.DAY_OF_YEAR, 1);
			dateIndex = calendar.getTime();
		}

	}


	public void readFileNum(List<String> fileNames) {
		List<String> strs = new ArrayList<>();
		for (String path : fileNames) {
			File file = new File(path);//Text文件
			readAllFileInArray(file, strs);
		}
		String cost = "process", stat = "statNum", uinicode = "12907c0f976a73000";
		final int[] indexValue = {0};
		Map<String, Integer> hourPhone = new HashMap<>();

		Map<String, Integer> totalPhone = new HashMap<>();
		strs.forEach(string -> {
			if (string.contains(cost) && string.contains(stat) && string.contains(uinicode)) {
				String newStr = string.substring(0, 2);
				int value = Integer.valueOf(newStr);

				if (indexValue[0] == value) {
					put(string, stat, cost, hourPhone, totalPhone);
				} else {
					System.out.println(indexValue[0] + "点;");
					printPercent(hourPhone);
					indexValue[0] = value;
					hourPhone.clear();
					put(string, stat, cost, hourPhone, totalPhone);
				}
			}
		});
		System.out.print((indexValue[0]) + "点;");
		printPercent(hourPhone);
		System.out.println("总统计;");
		printPercent(totalPhone);
	}

	private void printPercent(Map<String, Integer> map) {
		List<TypeBeanEx> beanList = new ArrayList<>();
		for (Map.Entry<String, Integer> entry : map.entrySet()) {
			TypeBeanEx bean = new TypeBeanEx();
			bean.setType(entry.getKey());
			bean.setNum(entry.getValue());
			beanList.add(bean);
		}
		if (beanList.size() > 0) {
			Collections.sort(beanList);
			int first = beanList.get(0).getNum();
			for (TypeBeanEx bean : beanList) {
				double rate = Arith.round(Arith.div((double) bean.getNum(), (double) first), 2);
				bean.setPercent((rate * 100) + "%");
				System.out.println(bean + ";");
			}
			System.out.println();
		}
		System.out.println();
	}

	private void put(String string, String stat, String cost, Map<String, Integer> hourPhone, Map<String, Integer> totalPhone) {
		int index = string.indexOf(stat), endIndex = string.indexOf(cost);
		String newStr = string.substring(index + 9, endIndex - 3);
		int num = hourPhone.getOrDefault(newStr, 0);
		hourPhone.put(newStr, ++num);
		num = totalPhone.getOrDefault(newStr, 0);
		totalPhone.put(newStr, ++num);
	}

	private void readAllFileInArray(File file, List<String> strs) {
		StatIntermConcurrent.read(file, strs);
	}
}
