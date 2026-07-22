package tool.screte;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * RSA非对称加密
 */
public class RsaUtil {

    private RsaUtil() {
    }

    /**
     * 生成RSA密钥对
     *
     * @return 密钥对
     * @throws NoSuchAlgorithmException 密钥生成算法不支持异常
     */
    public static KeyPair generateRsaKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.generateKeyPair();
    }

    /**
     * 获取RSA公钥的Base64编码字符串
     *
     * @return RSA公钥Base64编码字符串
     */
    public static String getRsaPublicKeyString(PublicKey publicKey) {
        KeyFactory keyFactory;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKey.getEncoded());
            return Base64.getEncoder().encodeToString(keyFactory.generatePublic(publicKeySpec).getEncoded());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据Base64编码的字符串还原为RSA公钥
     *
     * @param publicKeyString RSA公钥Base64编码字符串
     * @return RSA公钥
     */
    public static PublicKey getPublicKey(String publicKeyString) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyString));
            return keyFactory.generatePublic(publicKeySpec);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取RSA私钥的Base64编码字符串
     *
     * @return RSA私钥Base64编码字符串
     */
    public static String getRsaPrivateKeyString(PrivateKey privateKey) {
        KeyFactory keyFactory;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKey.getEncoded());
            return Base64.getEncoder().encodeToString(keyFactory.generatePrivate(privateKeySpec).getEncoded());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据Base64编码的字符串还原为RSA私钥
     *
     * @param privateKeyString RSA私钥Base64编码字符串
     * @return RSA私钥
     */
    public static PrivateKey getPrivateKey(String privateKeyString) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyString));
            return keyFactory.generatePrivate(privateKeySpec);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 非对称加密算法RSA加密
     *
     * @param plaintext 明文
     * @param publicKey 公钥
     * @return 加密后的密文
     * @throws Exception 加密异常
     */
    public static String encryptWithRsa(String plaintext, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    /**
     * 非对称加密算法RSA解密
     *
     * @param ciphertext 密文
     * @param privateKey 私钥
     * @return 解密后的明文
     * @throws Exception 解密异常
     */
    public static String decryptWithRsa(String ciphertext, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(ciphertext));
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    public static void main(String[] args) throws Exception {
        //RSA非对称加密示例
        String plaintext = "Hello, RSA!";
        //生成RSA密钥对
        KeyPair keyPair = generateRsaKeyPair();
        //公钥字符串获取
        String publicKeyString = getRsaPublicKeyString(keyPair.getPublic());
        System.out.println("RSA publicKeyString: " + publicKeyString);
        //公钥还原
        PublicKey publicKey = getPublicKey(publicKeyString);
        //私钥字符串获取
        String privateKeyString = getRsaPrivateKeyString(keyPair.getPrivate());
        System.out.println("RSA privateKeyString: " + privateKeyString);
        //私钥还原
        PrivateKey privateKey = getPrivateKey(privateKeyString);
        // RSA公钥加密
        String encryptedRSA = encryptWithRsa(plaintext, publicKey);
        System.out.println("RSA Encrypted: " + encryptedRSA);
        // RSA私钥解密
        String decryptedRSA = decryptWithRsa(encryptedRSA, privateKey);
        System.out.println("RSA Decrypted: " + decryptedRSA);
    }
}