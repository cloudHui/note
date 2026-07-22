package download;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

public class URLDownLoad {

	private static final int BUFFER_SIZE = 8192;

	public static boolean downloadFile(String destUrl, String fileName) {
		try {
			long start = System.currentTimeMillis();
			FileOutputStream fos;
			BufferedInputStream bis;
			HttpURLConnection httpURLConnection;
			URL url;
			byte[] buf = new byte[BUFFER_SIZE];
			int size;
			url = new URL(destUrl);
			httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.connect();
			bis = new BufferedInputStream(httpURLConnection.getInputStream());
			fos = new FileOutputStream(fileName);
			System.out.println("正在获取链接[" + destUrl + "]的内容 将其保存为文件[" + fileName + "]");
			while ((size = bis.read(buf)) != -1) {
				fos.write(buf, 0, size);
			}
			fos.close();
			bis.close();
			httpURLConnection.disconnect();

			long fileSizeInBytes = getFileSize(fileName);
			double fileSizeInMB = fileSizeInBytes / (1024.0 * 1024.0);

			System.out.println("文件：" + destUrl + " 大小:" + fileSizeInMB + " mb" +  " 消耗: " + (System.currentTimeMillis() -start)+" ms下载完成，保存为 " + fileName);
			return true;
		} catch (Exception e) {
			System.out.println("文件下载失败，信息：" + e.getMessage());
		}
		return false;
	}

	public static String download(String destUrl) {
		try {
			StringBuilder sb = new StringBuilder();
			BufferedInputStream bis;
			HttpURLConnection httpURLConnection;
			URL url;
			byte[] buf = new byte[BUFFER_SIZE];
			url = new URL(destUrl);
			httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.connect();
			bis = new BufferedInputStream(httpURLConnection.getInputStream());
			System.out.println("正在获取链接[ " + destUrl + " ]的内容 将其保存为字符串");
			while (bis.read(buf) != -1) {
				sb.append(Arrays.toString(buf));
			}
			bis.close();
			httpURLConnection.disconnect();

			System.out.println("网页：" + destUrl + " 下载完成，保存为 ");
			return sb.toString();
		} catch (Exception e) {
			System.out.println("文件下载失败，信息：" + e.getMessage());
		}
		return "";
	}

	/**
	 * 获取文件大小
	 */
	private static long getFileSize(String filePath) throws Exception {
		File file = new File(filePath);
		if (!file.exists()) {
			throw new Exception("File does not exist: " + filePath);
		}

		if (!file.isFile()) {
			throw new Exception("Not a regular file: " + filePath);
		}

		return file.length();
	}
}