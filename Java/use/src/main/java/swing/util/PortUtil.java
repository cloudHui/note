package swing.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PortUtil {
	public static boolean pingReachable(String ipAddress) throws Exception {
		int timeOut = 3000;   // 超时应该在3钞以上
		//  当返回值是true时，说明host是可用的，false则不可。
		return InetAddress.getByName(ipAddress).isReachable(timeOut);
	}


	public static List<String> pingCmd(String ipAddress) {
		List<String> values = new ArrayList<>();
		try {
			String line;
			Process pro = Runtime.getRuntime().exec("ping " + ipAddress);
			BufferedReader buf = new BufferedReader(new InputStreamReader(
					pro.getInputStream(), Charset.forName("GBK")));
			while ((line = buf.readLine()) != null) {
				values.add(line);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return values;
	}

	public static boolean pingCmdValue(String ipAddress) {
		int pingTimes = 5, timeOut = 5000;

		BufferedReader in = null;
		//  将要执行的ping命令,此命令是windows格式的命令
		Runtime r = Runtime.getRuntime();
		String pingCommand = "ping " + ipAddress + " -n " + pingTimes + " -w " + timeOut;
		try {    //  执行命令并获取输出
			System.out.println(pingCommand);
			Process p = r.exec(pingCommand);
			if (p == null) {
				return false;
			}
			//  逐行检查输出,计算类似出现=23ms TTL=62字样的次数
			in = new BufferedReader(new InputStreamReader(p.getInputStream(), Charset.forName("GBK")));
			int connectedCount = 0;
			String line;
			while ((line = in.readLine()) != null) {
				//  如果出现类似=23ms TTL=62这样的字样,出现的次数=测试次数则返回真
				connectedCount += getCheckResult(line);
			}
			return connectedCount == pingTimes;
		} catch (Exception ex) {
			ex.printStackTrace();
			//  出现异常则返回假
			return false;
		} finally {
			try {
				assert in != null;
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// 若line含有=18ms TTL=16字样,说明已经ping通,返回1,否則返回0.
	private static int getCheckResult(String line) {   //  System.out.println("控制台输出的结果为:"+line);
		Pattern pattern = Pattern.compile("(\\d+ms)(\\s+)(TTL=\\d+)", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(line);
		if (matcher.find()) {
			return 1;
		}
		return 0;
	}

	public static List<String> telnetCmd(String ipAddress, String port) {
		List<String> values = new ArrayList<>();
		try {
			String line;
			Process pro = Runtime.getRuntime().exec("telnet " + ipAddress + " " + port);
			BufferedReader buf = new BufferedReader(new InputStreamReader(
					pro.getInputStream(), Charset.forName("GBK")));
			while ((line = buf.readLine()) != null) {
				values.add(line);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return values;
	}

	public static void main(String[] args) throws Exception {
		String ipAddress = "127.0.0.1";
		System.out.println(pingReachable(ipAddress));
		pingCmd(ipAddress);
		System.out.println(pingCmdValue(ipAddress));
	}
}
