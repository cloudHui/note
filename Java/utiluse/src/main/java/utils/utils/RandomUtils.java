package utils.utils;

import java.util.Random;

public class RandomUtils {
	private static final Random random = new Random(System.nanoTime());

	public RandomUtils() {
	}

	public static int random(int min, int max) {
		return random(min, max, random);
	}

	public static int random(int min, int max, Random r) {
		return (int) (r.nextDouble() * (double) (max - min + 1)) + min;
	}

	public static long random(long min, long max) {
		return random(min, max, random);
	}

	public static long random(long min, long max, Random r) {
		return (long) (r.nextDouble() * (double) (max - min + 1L)) + min;
	}

	public static void main(String[] args) {
		int value, start = 0, end = 10, times = 1000;
		for (int index = 0; index < times; index++) {
			value = random(0, end);
			if (value < start || value >= end) {
				System.out.println(value);
			}
		}
	}
}
