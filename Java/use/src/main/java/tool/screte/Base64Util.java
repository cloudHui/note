package tool.screte;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Base64
 */
public class Base64Util {

	private Base64Util() {
	}

	/**
	 * Base64 编码
	 *
	 * @param plainText 内容
	 * @return 十六进制字符串
	 */
	public static String encodeBase64(String plainText) {
		byte[] plainBytes = plainText.getBytes(StandardCharsets.UTF_8);
		return Base64.getEncoder().encodeToString(plainBytes);
	}

	/**
	 * Base64 解码
	 *
	 * @param base64Text 十六进制字符串
	 * @return 内容
	 */
	public static String decodeBase64(String base64Text) {
		byte[] base64Bytes = Base64.getDecoder().decode(base64Text);
		return new String(base64Bytes, StandardCharsets.UTF_8);
	}

	public static void main(String[] args) {
		// Base64 编码
		String base64Text = Base64Util.encodeBase64("Hello Base64!");
		System.out.println("Base64 Encoded: " + base64Text);
		// Base64 解码
		String decodedText = Base64Util.decodeBase64(base64Text);
		System.out.println("Base64 Decoded: " + decodedText);
	}
}