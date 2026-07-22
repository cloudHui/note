package server;

import server.model.Arith;
import server.model.Cpu;
import server.model.Mem;
import server.model.Server;
import server.model.SysFile;
import tool.IpUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

public class ServerInfo {

	public static void main(String[] args) {
		System.out.println(IpUtils.exeCommand("curl icanhazip.com"));

		System.out.println(IpUtils.getLocalIP());
		System.out.println(checkServer());
		System.out.println(getLoad());
	}

	/**
	 * 获取系统负载:top的15分钟平均值/(逻辑cpu核数*0.7)
	 */
	public static double getLoad() {
		try {
			String LOAD_COMMAND = "uptime";
			Runtime r = Runtime.getRuntime();
			double cpuPerformance = 0.7 * r.availableProcessors();
			Process pro = r.exec(LOAD_COMMAND);
			BufferedReader br = new BufferedReader(new InputStreamReader(pro.getInputStream()));
			String topLoads = br.readLine();
			double topLoad = Double.parseDouble(topLoads.substring(topLoads.lastIndexOf(" ") + 1));
			br.close();
			pro.destroy();
			return Arith.round(Arith.mul(topLoad / cpuPerformance, 100), 2);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * 检测服务器状态
	 */
	public static String checkServer() {
		StringBuilder sb = new StringBuilder();
		Server server = new Server();
		try {
			server.copyTo();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Cpu cpu = server.getCpu();
		String info = cpu.toString();
		sb.append(info).append("\n");

		double cpuRate = cpu.getUsed();
		info = Double.toString(cpuRate);
		sb.append("cpu rate:").append(info).append("\n");

		info = server.getJvm().toString();
		sb.append(info).append("\n");

		info = server.getSys().toString();
		sb.append(" ").append(info);

		Mem mem = server.getMem();
		double memRate = mem.getUsage();

		info = mem.toString();
		sb.append(info).append("\n");

		info = Double.toString(memRate);
		sb.append(" rate:").append(info).append("\n");
		List<SysFile> sysFiles = server.getSysFiles();
		boolean nowSafe = true;
		for (SysFile file : sysFiles) {
			sb.append("SysFile:").append(file.toString()).append("\n");
		}

		int safeCpu = 0;
		int safeMum = 0;
		if (memRate > safeMum || cpuRate > safeCpu) {
			nowSafe = false;
		}
		//服务器现在的状态不正常 或者现在状态正常 之前不正常都要通知一下状态
//        setServerSafe(nowSafe);
//        if (!nowSafe || !serverSafe) {
		if (!nowSafe) {
			return sb.toString();
		}
		return null;
	}
}
