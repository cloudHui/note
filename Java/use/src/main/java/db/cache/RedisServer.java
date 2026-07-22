package db.cache;


import dbutils.JedisHelper;

public class RedisServer {

	public static boolean notContain(String key) {
		return !JedisHelper.exists(key);
	}

	public static void set(String key, String value) {
		JedisHelper.set(key, value);
	}
}
