package test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 统计时段最大并发数
 */
public class StatIntermConcurrent {
	private final static Logger log = LoggerFactory.getLogger(StatIntermConcurrent.class);

	/**
	 * 间隔30s
	 */
	private static final int second = 3;

	private StatIntermConcurrent() {
	}


	public static void main(String[] args) {


		StatIntermConcurrent app = new StatIntermConcurrent();
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, -9);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");//注意月份是MM
		Date date = calendar.getTime();
		Date dateNow = new Date();
		for (Date dateIndex = date; dateIndex.getTime() < dateNow.getTime(); ) {
			String now = simpleDateFormat.format(dateIndex);
			List<String> fileNames = new ArrayList<>();
			for (int i = 0; i < 3; i++) {
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
		AtomicLong nowTime = new AtomicLong(0);
		List<String> strs = new ArrayList<>();
		for (String path : fileNames) {
			File file = new File(path);//Text文件
			readAllFileInArray(file, strs);
		}
		String unicode = "unicode", statNum = "statNum";
		AtomicInteger maxTimes = new AtomicInteger(0);
		AtomicReference<Date> maxDate = new AtomicReference<>();
		AtomicInteger countTimes = new AtomicInteger(0);
		strs.forEach(string -> {
			if (string.contains(unicode) && string.contains(statNum)) {
				String day = string.substring(0, 8);

				String str = now + " " + day;
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				try {
					int statIndex = string.indexOf(statNum), end = string.indexOf("}]");
					String type = string.substring(statIndex + 9, end);
					if ("1".equals(type)) {
						countTimes.incrementAndGet();
					}
					Date date = sdf.parse(str);
					long times = date.getTime() / 1000L;

					if (nowTime.get() == 0) {
						nowTime.set(times);
					} else if (times - nowTime.get() >= second) {
						nowTime.set(times);
						if (maxTimes.get() < countTimes.get()) {
							maxTimes.set(countTimes.get());
							maxDate.set(date);
						}
						countTimes.set(0);
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		});
		System.out.println("每天最大次数的时间:" + maxDate.get() + " 最大次数:" + maxTimes.get());

		System.out.println();
		System.out.println();
		System.out.println();
	}

	private void readAllFileInArray(File file, List<String> strs) {
		read(file, strs);
	}

	public static void read(File file, List<String> strs) {
		try {
			//构造一个BufferedReader类来读取文件
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file),
					StandardCharsets.UTF_8));
			String str;
			//使用readLine方法，一次读一行
			while ((str = br.readLine()) != null) {
				strs.add(str);
			}
			br.close();
		} catch (Exception e) {
//            e.printStackTrace();
		}
	}
}
