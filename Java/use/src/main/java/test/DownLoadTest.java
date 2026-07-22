package test;

import com.alibaba.fastjson.JSONObject;
import httputils.HttpClientPool;
import timesutils.ExecutorPool;
import timesutils.Timer;
import tool.TimeUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class DownLoadTest {
	private static Map<String, List<Long>> timeMap = new ConcurrentHashMap<>();

	private static int num;

	private static long start;

	private static Timer timer = new Timer();


	static {
		timer.setRunners(new ExecutorPool("DownLoadTest"));
	}

	public static void main(String[] args) {
		try {
			URL url = new URL("https://www.baidu.com");
			url.openConnection();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String nums = System.getProperty("times", "20");
		num = Integer.parseInt(nums);
		start = TimeUtils.now().getTime();
		timer.register(0, 10, -1, integer -> {
			stat();
			return false;
		}, null);
		schedule(num);
//        hourRankRefresh();
//        schedule();


	}

	/**
	 * 3到4的下载耗时
	 */
	private void threeToFourDownLoad() {
		List<String> list = new ArrayList<>();
		//https://wxxcx.aiqipai.top/mjRes/scmj_qttResource0
		list.add("https://wxxcx.aiqipai.top/mjRes/scmj_qttResource1/assets/main/roomBG_55d88fb.jpg");
		list.add("https://wxxcx.aiqipai.top/mjRes/scmj_qttResource1/assets/main/newlogo_d8f25af.png");
		list.add("https://wxxcx.aiqipai.top/mjRes/scmj_qttResource1/assets/main/loadingbtmd_f749cbdc.png");
		list.add("https://wxxcx.aiqipai.top/mjRes/scmj_qttResource1/assets/main/newloadprogress_b2e26a.png");
		list.add("https://wxxcx.aiqipai.top/mjRes/scmj_qttResource1/assets/main/newloadprogressbg_56821150.png");
		for (String path : list) {
			List<Long> times = timeMap.computeIfAbsent(Thread.currentThread().getName(), k -> new CopyOnWriteArrayList<>());
			times.add(downLoad(path));
		}
	}

	/**
	 * 未到服务器的下载
	 */
	void codeDownLoad() {
		List<String> list = new ArrayList<>();

		list.add("http://47.99.197.173/qtt2020_test/index.html");
		list.add("http://47.99.197.173/qtt2020_test/jszip.min.js");
		list.add("http://47.99.197.173/qtt2020_test/loadingBg.jpg");
		list.add("http://47.99.197.173/qtt2020_test/egret.cfg?v=20009.26 ");
		for (String path : list) {
			List<Long> times = timeMap.computeIfAbsent(Thread.currentThread().getName(), k -> new CopyOnWriteArrayList<>());
			times.add(downLoad(path));
		}
	}

	/**
	 * 下载
	 */
	private static long downLoad(String path) {
		long start = System.currentTimeMillis();
		// 下载网络文件
		if (DownLoadTestFixRate.download(path)) {
			return System.currentTimeMillis() - start;
		}
		return 0;
	}


	private static void stat() {
		boolean allFit = timeMap.size() > 0;
		for (Map.Entry<String, List<Long>> entry : timeMap.entrySet()) {
			List<Long> longs = entry.getValue();
			if (longs == null || longs.size() != 4) {
				allFit = false;
				break;
			}
		}
		if (allFit) {
			double totalCost = 0;
			StringBuilder sb = new StringBuilder();
			List<Double> timesList = new CopyOnWriteArrayList<>();
			for (Map.Entry<String, List<Long>> entry : timeMap.entrySet()) {
				List<Long> times = entry.getValue();
				long total = 0;
				for (long time : times) {
					total += time;
				}
				sb.append(entry.getKey()).append(" 分别耗时:").append(times).append(" ms").append(" 完成一次耗时:");
				sb.append(total).append("ms");
				totalCost += total;
				timesList.add((double) total);
			}
			double times = timeMap.size();
			StringBuilder sbN = new StringBuilder();
			sbN.append("统计 时间:").append(TimeUtils.now()).append(" 总并发数:").append(times).append(" 总耗时:");
			sbN.append(totalCost).append("ms").append(" 平均耗时:").append(totalCost / times).append("ms");
			sbN.append(" 分别总耗时:").append(timesList).append("ms");
//            sendMsg(sbN.toString());
			//清除
//            timeMap.clear();
			System.out.println(sb);
			System.out.println(sbN);
			System.out.println("总耗时:" + (TimeUtils.now().getTime() - start) + " ms");
			System.exit(0);
		}
	}

	/**
	 * 每半个小时刷新任务
	 */
	private static void hourRankRefresh() {
		//时间间隔 1小时
		int interval = 30 * 60;
		Calendar calendar = Calendar.getInstance();
		//每天整点执行
		calendar.add(Calendar.HOUR_OF_DAY, 1);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		Date now = new Date();
		long date = now.getTime() / 1000L;
		long dateFirst = calendar.getTime().getTime() / 1000L;
		int delay = (int) (dateFirst - date);//到下一个整点的时间间隔

		//到下一个整点执行任务 每一个任务执行的下一次执行时间都是往后一小时
		timer.register(delay, interval, (-1), integer -> {
			schedule(num);
			return false;
		}, null);
	}

	private static void schedule(int num) {
		DownLoadTest loadTest = new DownLoadTest();
		for (int index = 0; index < num; index++) {
//            Thread thread = new Thread(loadTest::threeToFourDownLoad);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Thread thread = new Thread(loadTest::codeDownLoad);

			thread.start();
		}
	}

	/**
	 * 向钉钉发送信息
	 */
	private static void sendMsg(String postText) {
		JSONObject js = new JSONObject();
//        js.put("access_token", "3502e752afc250502eb8c33e4defac188942fd5718325722ea3e6a10c227e3f2");
		js.put("msgtype", "text");
		JSONObject js2 = new JSONObject();
		js2.put("content", postText);
		js.put("text", js2);

		String param = js.toJSONString();
		String url = "https://oapi.dingtalk.com/robot/send?access_token=" +
				"3502e752afc250502eb8c33e4defac188942fd5718325722ea3e6a10c227e3f2";
		String res = new HttpClientPool().sendPostJson(url, param);
		System.out.println("res" + res + " post:" + postText);

		//https://oapi.dingtalk.com/robot/send?access_token=3502e752afc250502eb8c33e4defac188942fd5718325722ea3e6a10c227e3f2
	}
}
