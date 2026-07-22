package stu;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {

	/**
	 * 一次性读取全部文件数据
	 */
	public static void readFile(String strFile) {
		try {
			InputStream is = new FileInputStream(strFile);
			int iAvail = is.available();
			byte[] bytes = new byte[iAvail];
			int result = is.read(bytes);
			System.out.println("result:" + result + " 内容:" + new String(bytes));
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * 删除文件
	 */
	public static void deleteFile(String path){
		try {
			File file = new File(path);
			file.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 按行读取文件
	 */
	public static List<String> readFileByLine(String strFile) {
		List<String> value = new ArrayList<>();
		try {
			File file = new File(strFile);
			BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
			String strLine;
			while (null != (strLine = bufferedReader.readLine())) {
				value.add(strLine);
			}
			bufferedReader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}

	/**
	 * 读取文件
	 */
	public static String readFileAppend(String strFile) {
		StringBuilder sb = new StringBuilder();
		try {
			File file = new File(strFile);
			BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
			String strLine;
			while (null != (strLine = bufferedReader.readLine())) {
				sb.append(strLine);
			}
			bufferedReader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	/**
	 * 按位置和上次读取位置
	 *
	 * @param filePath   文件路径
	 * @param fileResult 定长读取文件
	 * @return 是否读取到内容
	 */
	public static boolean readFileByPos(String filePath, ReadFileResult fileResult) {
		boolean readSuccess = true;
		try (RandomAccessFile raf = new RandomAccessFile(filePath, "r")) {
			byte[] buffer = new byte[fileResult.getByteSize()];
			int bytesRead;
			// 移动到之前记录的位置
			if (fileResult.getPos() > 0) {
				raf.seek(fileResult.getPos());
			}
			// 定长读取
			bytesRead = raf.read(buffer);
			if (bytesRead != -1) {
				fileResult.setPos(raf.getFilePointer());
				fileResult.setResult(new String(buffer, StandardCharsets.UTF_8));
			} else {
				fileResult.setPos(bytesRead);
				readSuccess = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return readSuccess;
	}


	/**
	 * 写文件 没有就创建
	 *
	 * @param append 是否追加内容
	 */
	public static void writeFileAppendsUtf8(String strFile, String content, boolean append) {
		File file = new File(strFile);
		boolean exists = Files.exists(file.toPath());
		try (BufferedWriter writer = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8,
				exists ? (append ? StandardOpenOption.APPEND : StandardOpenOption.WRITE) : StandardOpenOption.CREATE)) {
			writer.write(content);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
