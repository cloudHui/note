package test;

import com.alibaba.fastjson.JSONObject;
import httputils.HttpClientPool;
import timesutils.ExecutorPool;
import timesutils.Timer;
import tool.TimeUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class DownLoadTestFixRate {
	private static Map<String, List<Long>> oneOneMap = new ConcurrentHashMap<>();

	private static Map<String, List<Long>> thirtySixTyMap = new ConcurrentHashMap<>();

	private static Map<String, List<Long>> thirtyEightTyMap = new ConcurrentHashMap<>();

	private static Map<String, List<Long>> threeEightTeenMap = new ConcurrentHashMap<>();

	private static Map<String, List<Long>> threeTwentyFourMap = new ConcurrentHashMap<>();

	private static Timer timer = new Timer();


	static {
		timer.setRunners(new ExecutorPool("DownLoadTestFixRate"));
	}

	public static void main(String[] args) {

		try {
			URL url = new URL("https://www.baidu.com");
			url.openConnection();
		} catch (IOException e) {
			e.printStackTrace();
		}

		timer.register(0, 10, -1, integer -> {
			stat(oneOneMap, "1秒一个");
			stat(threeEightTeenMap, "3秒18个");
			stat(threeTwentyFourMap, "3秒24个");
			stat(thirtySixTyMap, "30秒60个");
			stat(thirtyEightTyMap, "30秒80个");
			return false;
		}, null);
//        hourRankRefresh(1, 0, 0, oneOneMap);
//
//        hourRankRefresh(18, 130, 10, threeEightTeenMap);
////
//        hourRankRefresh(24, 125, 120, threeTwentyFourMap);

		hourRankRefresh(60, 500, 0, thirtySixTyMap);


		hourRankRefresh(80, 350, 0, thirtyEightTyMap);

	}


	/**
	 * 未到服务器的下载
	 */
	private static void codeDownLoad(Map<String, List<Long>> timeMap) {
		List<String> list = new ArrayList<>();

		///https://wxxcx.aiqipai.top/mjRes/scmj_qttResource0
		list.add("https://wxxcx.aiqipai.top/qtt2020_release/index.html");
		list.add("https://wxxcx.aiqipai.top/qtt2020_release/jszip.min.js");
		list.add("https://wxxcx.aiqipai.top/qtt2020_release/loadingBg.jpg");
		list.add("https://wxxcx.aiqipai.top/qtt2020_release/egret.cfg?v=20009.26");
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
		if (download(path)) {
			return (System.currentTimeMillis() - start);
		}
		return 0;
	}

	static boolean download(String path) {
		try {
			URL url = new URL(path);
			URLConnection conn = url.openConnection();
			InputStream inStream = conn.getInputStream();
			byte[] buffer = new byte[1204];
			while (inStream.read(buffer) != -1) {
			}
			inStream.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}


	private static void stat(Map<String, List<Long>> timeMap, String name) {
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
//            StringBuilder sb = new StringBuilder();
			List<Double> timesList = new CopyOnWriteArrayList<>();
			for (Map.Entry<String, List<Long>> entry : timeMap.entrySet()) {
				List<Long> times = entry.getValue();
				long total = 0;
				for (long time : times) {
					total += time;
				}
//                sb.append(entry.getKey()).append(" 分别耗时:").append(times).append(" ms").append(" 完成一次耗时:");
//                sb.append(total).append("ms");
				totalCost += total;
				timesList.add((double) total);
			}
			double times = timeMap.size();
			StringBuilder sbN = new StringBuilder();
			sbN.append(new Date());
			sbN.append(name).append("统计 时间:").append(TimeUtils.now()).append(" 总并发数:").append(times).append(" 总耗时:");
			sbN.append(totalCost).append("ms").append(" 平均耗时:").append(totalCost / times).append("ms");
			sbN.append(" 分别总耗时:").append(timesList).append("ms");
//            sendMsg(sbN.toString());
			//清除
			timeMap.clear();
//            System.out.println(sb);
			System.out.println(sbN);
		}
	}

	/**
	 * 每半个小时刷新任务
	 */
	private static void hourRankRefresh(int sameNum, int sleep, int delayAdd, Map<String, List<Long>> timeMap) {
		//时间间隔 半小时
		int interval = 30 * 60;
//        Calendar calendar = Calendar.getInstance();
//        //每天整点执行
//        calendar.add(Calendar.HOUR_OF_DAY, 1);
//        calendar.set(Calendar.MINUTE, 0);
//        calendar.set(Calendar.SECOND, 0);
//        Date now = new Date();
//        long date = now.getTime() / 1000L;
//        long dateFirst = calendar.getTime().getTime() / 1000L;
//        int delay = (int) (dateFirst - date);//到下一个整点的时间间隔

		//到下一个整点执行任务 每一个任务执行的下一次执行时间都是往后一小时
		timer.register(delayAdd, interval, (-1), integer -> {
			schedule(sameNum, sleep, timeMap);
			return false;
		}, null);
	}

	private static void schedule(int num, int sleep, Map<String, List<Long>> timeMap) {
		for (int index = 0; index < num; index++) {
			try {
				Thread.sleep(sleep);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Thread thread = new Thread(() -> codeDownLoad(timeMap));

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
