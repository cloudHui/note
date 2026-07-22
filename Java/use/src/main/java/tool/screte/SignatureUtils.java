package tool.screte;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.net.URLDecoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * 这一趟单向加密 只能验证是否签名相同不能还原得到原始数据
 * 只校验签名
 */
public class SignatureUtils {

    // 加密方法
    public static String encode(String secret, String stringToSign) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
            return URLEncoder.encode(Base64.getEncoder().encodeToString(signData), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 解码方法
    public static byte[] decode(String encodedSign) {
        try {
            // 1. URL 解码
            String base64EncodedSign = URLDecoder.decode(encodedSign, StandardCharsets.UTF_8.toString());

            // 2. Base64 解码
            return Base64.getDecoder().decode(base64EncodedSign);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 验证签名方法
    public static boolean verifySignature(String secret, String stringToSign, String encodedSign) {
        try {
            // 1. 解码签名
            byte[] signData = decode(encodedSign);

            // 2. 重新生成签名
            byte[] recalculatedSignData = calculateHmacSHA256(stringToSign, secret);

            // 3. 比较签名
            return MessageDigest.isEqual(signData, recalculatedSignData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // 计算 HMAC-SHA256 签名
    private static byte[] calculateHmacSHA256(String data, String secret) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(secretKeySpec);
        return mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
    }

    public static void main(String[] args) {
        // 示例数据
        String timestamp = "1672531199";  // 示例时间戳
        String secret = "your_secret_key";  // 示例密钥
        String stringToSign = timestamp + "\n" + secret;

        // 1. 加密
        String encodedSign = encode(secret, stringToSign);
        System.out.println("加密后的签名: " + encodedSign);

        // 2. 解码
        byte[] decodedSignData = decode(encodedSign);
        if (decodedSignData != null) {
            System.out.println("解码后的签名字节数组: " + bytesToHex(decodedSignData));
        } else {
            System.out.println("解码失败");
        }

        // 3. 验证签名
        boolean isVerified = verifySignature(secret, stringToSign, encodedSign);
        System.out.println("签名验证结果: " + (isVerified ? "成功" : "失败"));
    }

    // 辅助方法：将字节数组转换为十六进制字符串（用于打印）
    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
