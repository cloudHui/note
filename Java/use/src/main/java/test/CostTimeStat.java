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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 测试
 */
public class CostTimeStat {
	private final static Logger log = LoggerFactory.getLogger(CostTimeStat.class);

	private CostTimeStat() {
	}


	public static void main(String[] args) {


		CostTimeStat app = new CostTimeStat();
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, -8);
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
		String unicode = "unicode", statNum = "statNum", info = "INFO", point = ".";
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
			if (type.length() < 3) {
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
			Collections.sort(statCostList);
			for (StatCost statCost : statCostList) {
				long last = statCost.getCost();
				if (start == 0 || lastType == statCost.getType()) {
					start = last;
				} else {
					long cost = last - start;
					statCost.setLastCost(cost);
					if (statCost.getType() > 2) {
						List<Double> longs = typeStatCost.computeIfAbsent(statCost.getType(), k -> new ArrayList<>());
						if (cost > 0) {
							longs.add((double) cost);
//                        } else if (cost > 0) {
//                            if (cost > 10000) {
//                                System.out.println("type:" + statCost.getType());
//                                System.out.println("start:" + start + " " + new Date(start));
//                                System.out.println(" last:" + last + " " + new Date(last));
//                                System.out.println(" cost:" + (cost/100L)+"s");
//                                System.out.println();
//                            }
						}
					}
					start = last;
					lastType = statCost.getType();
				}
			}
		}

		for (Map.Entry<Integer, List<Double>> entry : typeStatCost.entrySet()) {
			List<Double> list = entry.getValue();
			int type = entry.getKey();
//            if (type == 4) {
			if (list.size() > 0) {
				Map<String, Integer> stat = new LinkedHashMap<>();

				String keyEx;
				keyEx = "1s以内";
				stat.put(keyEx, 0);
				keyEx = "1-2s间";
				stat.put(keyEx, 0);
				keyEx = "2-3s间";
				stat.put(keyEx, 0);
				keyEx = "3-4s间";
				stat.put(keyEx, 0);
				keyEx = "4-5s间";
				stat.put(keyEx, 0);
				keyEx = "5-6s间";
				stat.put(keyEx, 0);
				keyEx = "6-7s间";
				stat.put(keyEx, 0);
				keyEx = "7-8s间";
				stat.put(keyEx, 0);
				keyEx = "8-9s间";
				stat.put(keyEx, 0);
				keyEx = "9-10s间";
				stat.put(keyEx, 0);
				keyEx = "超过10s";
				stat.put(keyEx, 0);
				Collections.sort(list);
//                long total = 0;
				for (double value : list) {
//                    total += value;
					String key;
					if (value <= 1000) {
						key = "1s以内";
					} else if (value <= 2000) {
						key = "1-2s间";
					} else if (value <= 3000) {
						key = "2-3s间";
					} else if (value <= 4000) {
						key = "3-4s间";
					} else if (value <= 5000) {
						key = "4-5s间";
					} else if (value <= 6000) {
						key = "5-6s间";
					} else if (value <= 7000) {
						key = "6-7s间";
					} else if (value <= 8000) {
						key = "7-8s间";
					} else if (value <= 9000) {
						key = "8-9s间";
					} else if (value <= 10000) {
						key = "9-10s间";
					} else {
						key = "超过10s";
					}
					int times = stat.get(key);
					stat.put(key, ++times);
				}
				int size = list.size();
				System.out.println(
						"[点位:" + entry.getKey()
// + " min:" + list.get(0) + "ms max:"
//                        + list.get(list.size() - 1) + "ms ave:" + (total / size)
// "ms "
								+ "总人次:"
								+ size
//                        + " middle:" + list.get(size / 2)
								+ " 1-10s及超过10s的统计:" + stat
								+ "]");
			}
//            }

		}
		System.out.println();
	}
}
