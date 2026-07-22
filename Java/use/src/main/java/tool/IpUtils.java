package tool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class IpUtils {

	public static String getOutIp() {
		return exeCommand("curl icanhazip.com").get(0);
	}

	public static List<String> exeCommand(String cmd) {
		List<String> results = new ArrayList<>();
		String osName = System.getProperty("os.name");
		String command;
		if (osName.contains("Windows")) {
			command = "cmd.exe /c " + cmd;
		} else {
			//String[] command = {"/bin/sh", "-c", "curl icanhazip.com"};
			command = cmd;
		}
		InputStream in;
		String result;
		try {
			Process pro = Runtime.getRuntime().exec(command);
			in = pro.getInputStream();
			BufferedReader read = new BufferedReader(new InputStreamReader(in));
			if ((result = read.readLine()) != null) {
				do {
					results.add(result);
				} while (read.readLine() != null);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return results;
	}


	public static String getLocalIP() {
		try {
			StringBuilder sb = new StringBuilder();

			Enumeration allNetInterfaces = NetworkInterface.getNetworkInterfaces();
			InetAddress ip;
			while (allNetInterfaces.hasMoreElements()) {
				NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
				Enumeration addresses = netInterface.getInetAddresses();
				while (addresses.hasMoreElements()) {
					ip = (InetAddress) addresses.nextElement();
					if (ip instanceof Inet4Address) {
						if ("127.0.0.1".equals(ip.getHostAddress())) {
							continue;
						}
						sb.append("IP:").append(ip.getHostAddress());
					}
				}
			}
			return sb.toString();
		} catch (Exception e) {
			System.out.println(e);
		}
		return null;
	}

	public static String getHostName() {
		InetAddress adar;
		try {
			adar = InetAddress.getLocalHost();
			//获取本机计算机名称
			return adar.getHostName();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return null;
	}
}
