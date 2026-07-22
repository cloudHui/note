package test;

import tool.Arith;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 测试
 */
public class StatTwoCost {

	public static void main(String[] args) {

		StatTwoCost app = new StatTwoCost();
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, -4);
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
		String cost = "process", stat = "statNum", time = "time";
		final int[] indexValue = {0};
		List<Long> hourPhone = new ArrayList<>();

		List<Long> totalPhone = new ArrayList<>();
		strs.forEach(string -> {
			if (string.contains(cost) && string.contains(stat) && string.contains(time)) {
				String newStr = string.substring(0, 2);
				int value = Integer.parseInt(newStr);

				if (indexValue[0] == value) {
					put(string, time, cost, hourPhone, totalPhone);
				} else {
					System.out.println(indexValue[0] + "点;");
					printPercent(hourPhone);
					indexValue[0] = value;
					hourPhone.clear();
					put(string, time, cost, hourPhone, totalPhone);
				}
			}
		});
		System.out.print((indexValue[0]) + "点;");
		printPercent(hourPhone);
		System.out.println("总统计;");
		printPercent(totalPhone);
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
	}

	private void printPercent(List<Long> map) {
		int size = map.size();
		if (size > 0) {
			long total = 0;
			Collections.sort(map);
			for (long value : map) {
				total += value;
			}
			double rate = Arith.round(Arith.div((double) total, size), 2);
			double midle = Arith.round(Arith.div((double) total, (double) size / 2), 2);
			System.out.println("点位1到点位2 最小耗时:" + map.get(0) + "ms 中位数:" + midle + "ms 最大耗时:" + map.get(size - 1) + "ms 数量:"
					+ size + " 平均耗时:" + rate + "ms");
		}
	}

	private void put(String string, String stat, String cost, List<Long> hourPhone, List<Long> totalPhone) {
		int index = string.indexOf(stat), endIndex = string.indexOf(cost);
		String newStr = string.substring(index + 6, endIndex - 3);
		int costTime = Integer.parseInt(newStr);
		hourPhone.add((long) costTime);
		totalPhone.add((long) costTime);
	}

	private void readAllFileInArray(File file, List<String> strs) {
		StatIntermConcurrent.read(file, strs);
	}
}
