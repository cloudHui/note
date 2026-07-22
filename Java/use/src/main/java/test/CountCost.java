package test;

import tool.Arith;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class CountCost {

	public static void main(String[] args) {

		CountCost app = new CountCost();
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, -1);
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
			System.out.println("日期:" + simpleDateFormat.format(dateIndex));
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
		String cost = "cost:", ms = "ms]";
		List<Integer> costTimes = new ArrayList<>();
		List<Integer> more1000 = new ArrayList<>();
		List<Integer> more100 = new ArrayList<>();
		List<Integer> more20 = new ArrayList<>();
		int total = 0, totalmore1000 = 0, totalmore100 = 0, totalmore20 = 0;
		for (String string : strs) {
			if (string.contains(cost) && string.contains(ms)) {
				int start = string.indexOf(cost), end = string.indexOf(ms);
				String id = string.substring(start + 5, end);
				long costTime = Long.valueOf(id);
				if (costTime < Integer.MAX_VALUE) {
					if (costTime >= 1000) {
						totalmore1000 += costTime;
						more1000.add((int) costTime);
						System.out.println(string.substring(0, 8) + " " + costTime);
					} else if (costTime >= 100) {
						totalmore100 += costTime;
						more100.add((int) costTime);
					} else if (costTime >= 20) {
						totalmore20 += costTime;
						more20.add((int) costTime);
					}
					total += costTime;
					costTimes.add((int) costTime);
				}
			}
		}
		int size = costTimes.size();
		if (size > 0) {
			Collections.sort(costTimes);
			double average = Arith.round(Arith.div(total, size, 2), 2);
			System.out.println("costTimes size:" + size + " max:" + costTimes.get(size - 1) + " average:" + average);

			Collections.sort(more1000);
			size = more1000.size();
			average = Arith.round(Arith.div(totalmore1000, size, 2), 2);
			System.out.println("more1000 size:" + size + " max:" + more1000.get(size - 1) + " average:" + average);
			System.out.println(more1000);

			Collections.sort(more100);
			size = more100.size();
			average = Arith.round(Arith.div(totalmore100, size, 2), 2);
			System.out.println("more100 size:" + size + " max:" + more100.get(size - 1) + " average:" + average);
			System.out.println(more100);

			size = more20.size();
			Collections.sort(more20);
			average = Arith.round(Arith.div(totalmore20, size, 2), 2);
			System.out.println("more20 size:" + size + " max:" + more20.get(size - 1) + " average:" + average);
			System.out.println(more20);
		}


	}

	private void readAllFileInArray(File file, List<String> strs) {
		StatIntermConcurrent.read(file, strs);
	}
}
