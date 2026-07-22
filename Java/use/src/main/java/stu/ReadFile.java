package stu;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class ReadFile {

	private static List<String> errors;

	static {
		errors = new ArrayList<>();
		errors.add("Exception");
		errors.add("at");
		errors.add("java");
	}

	public static void main(String[] args) {
		fileCheck();
//        System.out.println(1 / 0);
	}

	private static void fileCheck() {
		List<String> values = new ArrayList<>();
		long lastCount = 0;
		while (true) {
			lastCount = readFile(lastCount, values);
			checkFile(values);
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private static long readFile(long lastRead, List<String> values) {
		values.clear();
		int count;
		long lastCount = 0;
		RandomAccessFile accessFile = null;
		try {
			File file = new File("D:/1.txt");
			long length = file.length();
			if (lastRead >= length) {
				return lastRead;
			}
			accessFile = new RandomAccessFile(file, "rw");
			accessFile.seek(lastRead);
			byte[] bytes = new byte[1024];
			while ((count = accessFile.read(bytes, 0, bytes.length)) != -1) {
				values.add(new String(bytes));
				lastCount += count;
			}
			System.out.println(values);
			accessFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (accessFile != null) {
				try {
					accessFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return lastCount + lastRead;
	}

	private static void checkFile(List<String> values) {
		List<String> errorCode = new ArrayList<>();
		for (String value : values) {
			if (errors.contains(value)) {
				errorCode.add(value);
			}
		}
		if (errorCode.size() > 0) {
			System.out.println("error:" + errorCode);
		}
	}
}
