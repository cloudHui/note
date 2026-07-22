package test;

import db.business.entity.StatCost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.ParseException;
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
public class CostDownTimeStat {
	private final static Logger log = LoggerFactory.getLogger(CostDownTimeStat.class);

	private CostDownTimeStat() {
	}


	public static void main(String[] args) {


		CostDownTimeStat app = new CostDownTimeStat();
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, -6);
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
		String unicode = "unicode", statNum = "statNum";
		final int[] indexValue = {0};
		Map<String, List<StatCost>> hourPhone = new HashMap<>();

		Map<String, List<StatCost>> totalPhone = new HashMap<>();
		strs.forEach(string -> {
			if (string.contains(unicode) && string.contains(statNum)) {
				String newStr = string.substring(0, 2);
				int value = Integer.valueOf(newStr);

				if (indexValue[0] == value) {
					try {
						put(string, statNum, unicode, now, hourPhone, totalPhone);
					} catch (ParseException e) {
						e.printStackTrace();
					}
				} else {
					System.out.print(indexValue[0] + "点:");
					print(hourPhone);
					indexValue[0] = value;
					hourPhone.clear();
					try {
						put(string, statNum, unicode, now, hourPhone, totalPhone);
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
			}
		});
		System.out.print((indexValue[0]) + "点:");
		print(hourPhone);
		System.out.print("总统计:");
		print(totalPhone);
	}

	private void put(String string, String statNum, String unicode, String now,
	                 Map<String, List<StatCost>> hourPhone, Map<String, List<StatCost>> totalPhone) throws ParseException {
		int unicodeindex = string.indexOf(unicode), statNumendIndex = string.indexOf(statNum), end = string.indexOf("}]");
		String code = string.substring(unicodeindex + 10, statNumendIndex - 3);
		if (code.length() > 2) {
			String type = string.substring(statNumendIndex + 9, end);
			if ("3".equals(type) || "4".equals(type) || "30007".equals(type)) {
				String day = string.substring(0, 8), mini = string.substring(9, 12);
				String str = now + " " + day;
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date date = sdf.parse(str);
				int minis = Integer.valueOf(mini);
				StatCost statCost = new StatCost(Integer.valueOf(type), date.getTime() + minis);
				List<StatCost> housList = hourPhone.computeIfAbsent(code, k -> new ArrayList<>());
				List<StatCost> totalList = totalPhone.computeIfAbsent(code, k -> new ArrayList<>());
				housList.add(statCost);
				totalList.add(statCost);
			}
		}

	}

	private void readAllFileInArray(File file, List<String> strs) {
		StatIntermConcurrent.read(file, strs);
	}

	private void print(Map<String, List<StatCost>> map) {
		Map<Integer, List<Double>> typeStatCost = new HashMap<>();
		for (Map.Entry<String, List<StatCost>> entry : map.entrySet()) {
			long start = 0;
			int lastType = 0;
			List<StatCost> statCostList = entry.getValue();
			for (StatCost statCost : statCostList) {
				long last = statCost.getCost();
				if (start == 0 || lastType == statCost.getType()) {
					start = last;
				} else {
					statCost.setLastCost(last - start);
					start = last;
					List<Double> longs = typeStatCost.computeIfAbsent(statCost.getType(), k -> new ArrayList<>());
					if (statCost.getLastCost() > 0) {
						longs.add(statCost.getLastCost());
					}
					lastType = statCost.getType();
				}
			}
		}

		for (Map.Entry<Integer, List<Double>> entry : typeStatCost.entrySet()) {
			List<Double> list = entry.getValue();
			if (list.size() > 0 && entry.getKey() != 3) {
				Collections.sort(list);
				long total = 0;
				for (double value : list) {
					total += value;
				}
				System.out.println("[type:" + entry.getKey() + " min:" + list.get(0) + "ms max:"
						+ list.get(list.size() - 1) + "ms ave:" + (total / list.size()) + "ms size:" + list.size() + "]");
			}
		}
		System.out.println();
	}
}
