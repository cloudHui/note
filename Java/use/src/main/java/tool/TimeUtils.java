package tool;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtils {
	public static Date now() {
		return new Date();
	}

	public static Date tomorrowyBegin() {
		return dayBegin(now(), 1);
	}

	public static Date day3Hour() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		if (calendar.get(Calendar.HOUR_OF_DAY) >= 3) {
			calendar.add(Calendar.DATE, 1);
		}
		calendar.set(Calendar.HOUR_OF_DAY, 3);

		return calendar.getTime();
	}

	/**
	 * 获取三天前的日志文件名字
	 */
	public static void getDateName(){
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, -2);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");//注意月份是MM
		Date date = calendar.getTime();
		Date dateNow = new Date();
		for (Date dateIndex = date; dateIndex.getTime() < dateNow.getTime(); ) {
			String now = simpleDateFormat.format(dateIndex);
			for (int i = 0; i < 10; i++) {
				String path = "D:\\" + now + "-" + i + ".log";
				System.out.println("path:" + path);
			}
			System.out.println("日期:" + now);
			calendar.add(Calendar.DAY_OF_YEAR, 1);
			dateIndex = calendar.getTime();
		}
	}

	public static Date day0Hour() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);

		calendar.add(Calendar.DATE, 1);

		return calendar.getTime();
	}

	public static Date dayBegin(Date date, int delay) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.add(Calendar.DATE, delay);

		return calendar.getTime();
	}

	public static Date delayYears(Date date, int delay) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.YEAR, delay);

		return calendar.getTime();
	}


	public static Date getDayStartTime(Date date) {
		Calendar todayStart = Calendar.getInstance();
		todayStart.setTime(date);
		todayStart.set(Calendar.HOUR_OF_DAY, 0);
		todayStart.set(Calendar.MINUTE, 0);
		todayStart.set(Calendar.SECOND, 0);
		todayStart.set(Calendar.MILLISECOND, 0);
		return todayStart.getTime();
	}

	public static Date getDayEndTime(Date date) {
		Calendar todayEnd = Calendar.getInstance();
		todayEnd.setTime(date);
		todayEnd.set(Calendar.HOUR_OF_DAY, 23);
		todayEnd.set(Calendar.MINUTE, 59);
		todayEnd.set(Calendar.SECOND, 59);
		todayEnd.set(Calendar.MILLISECOND, 999);
		return todayEnd.getTime();
	}

	public static Date getWeekBegin(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int dayofweek = cal.get(Calendar.DAY_OF_WEEK);
		if (dayofweek == 1) {
			dayofweek += 7;
		}
		cal.add(Calendar.DATE, 2 - dayofweek);
		return getDayStartTime(cal.getTime());
	}

	public static Date getWeekEnd(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(getWeekBegin(date));
		cal.add(Calendar.DAY_OF_WEEK, 6);
		Date weekEndSta = cal.getTime();
		return getDayEndTime(weekEndSta);
	}


	public static boolean isSameDay(Date d1, Date d2) {
		Calendar c1 = Calendar.getInstance();
		c1.setTime(d1);

		Calendar c2 = Calendar.getInstance();
		c2.setTime(d2);

		return (c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH)
				&& c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH)
				&& c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR));
	}

	public static void main(String[] args) {
		Date end = TimeUtils.getWeekEnd(TimeUtils.now());
		System.out.println(end);
	}

}
