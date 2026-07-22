package com.khjxiaogu.MiraiSongPlugin.musicsource;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.khjxiaogu.MiraiSongPlugin.NetEaseCrypto;
import com.khjxiaogu.MiraiSongPlugin.Utils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class NetEaseHQMusicSource extends NetEaseMusicSource {

	public NetEaseHQMusicSource() {
	}

	@Override
	public String queryRealUrl(String id) throws Exception {
		JsonObject params = new JsonObject();
		params.addProperty("ids", "[" + id + "]");
		params.addProperty("br", 999000);
		String[] encrypt = NetEaseCrypto.weapiEncrypt(params.toString());
		String sb = "params=" + encrypt[0] + "&encSecKey=" + encrypt[1];
		byte[] towrite = sb.getBytes(StandardCharsets.UTF_8);
		URL u = new URL("http://music.163.com/weapi/song/enhance/player/url?csrf_token=");
		HttpURLConnection conn = (HttpURLConnection) u.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setConnectTimeout(4000);
		conn.setReadTimeout(4000);
		conn.setFixedLengthStreamingMode(towrite.length);
		conn.setInstanceFollowRedirects(true);
		conn.setRequestProperty("Accept", "*/*");
		conn.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8,gl;q=0.6,zh-TW;q=0.4");
		conn.setRequestProperty("Connection", "keep-alive");
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		conn.setRequestProperty("Content-Length", Integer.toString(towrite.length));
		conn.setRequestProperty("Referer", "https://music.163.com");
		conn.setRequestProperty("Host", "music.163.com");
		conn.setRequestProperty("User-Agent", NetEaseCrypto.getUserAgent());
		conn.connect();
		conn.getOutputStream().write(towrite);
		if (conn.getResponseCode() == 200) {
			InputStream is = conn.getInputStream();
			byte[] bs = Utils.readAll(is);
			is.close();
			conn.disconnect();
			JsonObject main = JsonParser.parseString(new String(bs, StandardCharsets.UTF_8)).getAsJsonObject();
			if (main.get("code").getAsInt() == 200) {
				JsonArray data = main.get("data").getAsJsonArray();
				JsonObject song = data.get(0).getAsJsonObject();
				if (song.get("code").getAsInt() == 200) {
					return song.get("url").getAsString().trim();
				}
			}
		}
		return null;
	}

	@Override
	public boolean isVisible() {
		return false;
	}
}
