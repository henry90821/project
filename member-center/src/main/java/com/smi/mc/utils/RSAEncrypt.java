package com.smi.mc.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Properties;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import com.smi.mc.constants.ConfigConstants;

import sun.misc.BASE64Decoder;

public class RSAEncrypt {

	private static  String privatePath;

	private static  String publicPath = "/pem/1/rsa_public_key.pem";
	
	
	// 内存溢出，采用用单例模式
	private static BouncyCastleProvider bouncyCastleProvider;

	// public static final String DEFAULT_PUBLIC_KEY=
	// "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDLYxmytT1U6m+Q/eP6AXsOBpjq" + "\r"
	// +
	// "Y6RAzb8dXeVhOW3pIKqaZkEFgw2hPN7k2slzPBf0Ml8AUe+DV2zG6kuyz5ylQ8ID" + "\r"
	// +
	// "sp8X2ZCbhUKUQu07TAo5s4LQgfMr7TJUj0Qc1z24YkUBiy+P8CKOGX5hqFLCg0iR" + "\r"
	// +
	// "yMhKPtgu3Iq5JFiYFQIDAQAB";

	public static final String DEFAULT_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDEJx38MgljGyJ3y7qb9IyxbC98"
			+ "\r" + "KFDBTiGWGtYLchFB9lh1X5jKJVR1uzgOhTLA08WWg0oqXiihFtzFQ1WzPjbOxkGv" + "\r"
			+ "Ot/gHv9AYFo+mD72rzDpihk0sXAIueG9x0CFXWwtv0cJAE+vCAbSEiYyXNSi0v9T" + "\r" + "c4QxJ6wImaT76IlNKQIDAQAB";

	// public static final String DEFAULT_PRIVATE_KEY=
	// "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAMQnHfwyCWMbInfL" +
	// "upv0jLFsL3woUMFOIZYa1gtyEUH2WHVfmMolVHW7OA6FMsDTxZaDSipeKKEW3MVD" +
	// "VbM+Ns7GQa863+Ae/0BgWj6YPvavMOmKGTSxcAi54b3HQIVdbC2/RwkAT68IBtIS" +
	// "JjJc1KLS/1NzhDEnrAiZpPvoiU0pAgMBAAECgYBh2PGIQJ2WJo8HG0W3TkJwrvrx" +
	// "AFtoUkVGz5Q+mWQqd2yhAFMDY6PCJuZLt/4dWoaSEmQIxneWBAWVEYXKqBpGI3jq" +
	// "pc3Q7jBVyhBtvkqT91i5tR3WoE8PZ1pTjk+OE9g6mYFrZqUONyPrmSIoLW3CQ48p" +
	// "QiXhNFfz/fXgCCOIAQJBAO5krS7AwXeScKtZE2Jjg7OZ8hN/k7KKoIbdGwhi7cwP" +
	// "dzmHtIOhAiKzgXxWN0JWXAgMuB/Uc1B1MyeX/N0VvkECQQDSo8vWflm6fZm4JpnS" +
	// "F5IqQUp8Nd5G/Navy0LcqQ+Z1SEhk9DWYwZB/hB9ph+hAkXHtdWbrDUJimZkSRa9" +
	// "fiTpAkBJVh1UsaWSpDEW9TsaXGTKnoQy7V9BYJYLhv9m8BAZY3SY9R9aEvD2PeSK" +
	// "nHc5aBm9vDP0TsX+rV9EQeRfGu5BAkBtF3P8lNe35FD2tNc5ngePZt1C00tZjJ1L" +
	// "0oPAcLapv143W6zvO74D86dqy9zZsKmfMyd8RoX5ePpqugfTAD/xAkAuagcU6Nf4" +
	// "FLTZZL8uY6WSR27JJT3zGXukAwxwCqlZW9MHyjzITHlGVtAKttS+xA/8YUx+Gk7J" +
	// "z0ODsBKxpSZn" ;

	public static final String DEFAULT_PRIVATE_KEY = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAMLTIHVKH2udcaYW"
			+ "7KriJxJ32fs7JB1MmgNfABBsz/7deCmcltGFVbG6OLbdKtcVnB9nuOu4qmmktbTb"
			+ "gfzbAgTRGDCGdPNSjNxwwnTO5AIXs0GGJsqktKLDLaNOVjI1w2gYzh9R2HYCbGSg"
			+ "HcwFYYXLDWraU8rqUJ3R5u0EziO7AgMBAAECgYAzSSiuN4j150hXlIlQop/ueKZl"
			+ "QDhNjjCs2oFF0Z8fVGWhbps1JIhedMshOC9i10l16vP++WnCzZ0XswEQ8wYgkIQC"
			+ "bJS6Q2b5BDieaAB8r5jSPPr5Qa9LFysKIv1uwNsEfBa3cjiEUqUjvV0iFCLMk9QB"
			+ "VKDSQmWo48/qMhqpoQJBAPIZZV/5+evzXUOCf+f0t1aqpVeM3pZEKI6LfkGUKnqZ"
			+ "8IwCm7lWxPse1f7wUX2yu/vxoWYp+pgAzrcHh/AgXn0CQQDOAthv6mbKefkLoWEZ"
			+ "DBG2U8lD84P/HhScpt+xTC8Mv9qRb8Hnr/N1rq/FqxVANK8AL9T1leqgqYFyO12w"
			+ "1IiXAkBrCyGQoyWG1w7ol53YQJKDxmXe5RFR3YcVJ8ZGVq3FkaUTMuAJvbzQz9yM"
			+ "oVNWwZ1uk3Lqiisb5FeCA9luKdG9AkA3Pu4Z/Ss5q0INS2bFl8X8NjXoIhrIxKFU"
			+ "bS/Z1QtdCOotTGIcpsPMUF9UzA9z22z+yNT8UwrLsQlEwATdEfHbAkACzRG+CD08"
			+ "iIlJQrC0gosXY8qgu7BWD+//Sralh+RHByHiHhp0gAQZ5q/X3tAs/MG5nZLsfZ/v" + "Y2j2fuTQ9pxy";
	/**
	 * 私钥
	 */
	private RSAPrivateKey privateKey;

	/**
	 * 公钥
	 */
	private RSAPublicKey publicKey;

	/**
	 * 字节数据转字符串专用集合
	 */
	private static final char[] HEX_CHAR = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e',
			'f' };

	/**
	 * 获取私钥
	 * 
	 * @return 当前的私钥对象
	 */
	public RSAPrivateKey getPrivateKey() {
		return privateKey;
	}

	/**
	 * 获取公钥
	 * 
	 * @return 当前的公钥对象
	 */
	public RSAPublicKey getPublicKey() {
		return publicKey;
	}

	/**
	 * 从文件中输入流中加载公钥
	 * 
	 * @param in
	 *            公钥输入流
	 * @throws Exception
	 *             加载公钥时产生的异常
	 */
	public void loadPublicKey(InputStream in) throws Exception {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String readLine = null;
			StringBuilder sb = new StringBuilder();
			while ((readLine = br.readLine()) != null) {
				if (readLine.charAt(0) == '-') {
					continue;
				} else {
					sb.append(readLine);
					sb.append('\r');
				}
			}
			loadPublicKey(sb.toString());
		} catch (IOException e) {
			throw new Exception("公钥数据流读取错误");
		} catch (NullPointerException e) {
			throw new Exception("公钥输入流为空");
		}
	}

	/**
	 * 从字符串中加载公钥
	 * 
	 * @param publicKeyStr
	 *            公钥数据字符串
	 * @throws Exception
	 *             加载公钥时产生的异常
	 */
	public void loadPublicKey(String publicKeyStr) throws Exception {
		try {
			@SuppressWarnings("restriction")
			BASE64Decoder base64Decoder = new BASE64Decoder();
			@SuppressWarnings("restriction")
			byte[] buffer = base64Decoder.decodeBuffer(publicKeyStr);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
			this.publicKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("无此算法");
		} catch (InvalidKeySpecException e) {
			throw new Exception("公钥非法");
		} catch (IOException e) {
			throw new Exception("公钥数据内容读取错误");
		} catch (NullPointerException e) {
			throw new Exception("公钥数据为空");
		}
	}

	/**
	 * 从文件中加载私钥
	 * 
	 * @param keyFileName
	 *            私钥文件名
	 * @return 是否成功
	 * @throws Exception
	 */
	public void loadPrivateKey(InputStream in) throws Exception {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String readLine = null;
			StringBuilder sb = new StringBuilder();
			while ((readLine = br.readLine()) != null) {
				if (readLine.charAt(0) == '-') {
					continue;
				} else {
					sb.append(readLine);
					sb.append('\r');
				}
			}
			loadPrivateKey(sb.toString());
		} catch (IOException e) {
			throw new Exception("私钥数据读取错误");
		} catch (NullPointerException e) {
			throw new Exception("私钥输入流为空");
		}
	}

	public void loadPrivateKey(String privateKeyStr) throws Exception {
		try {
			@SuppressWarnings("restriction")
			BASE64Decoder base64Decoder = new BASE64Decoder();
			@SuppressWarnings("restriction")
			byte[] buffer = base64Decoder.decodeBuffer(privateKeyStr);
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			this.privateKey = (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("无此算法");
		} catch (InvalidKeySpecException e) {
			throw new Exception("私钥非法");
		} catch (IOException e) {
			throw new Exception("私钥数据内容读取错误");
		} catch (NullPointerException e) {
			throw new Exception("私钥数据为空");
		}
	}

	/**
	 * 加密过程
	 * 
	 * @param privateKey
	 *            私钥
	 * @param plainTextData
	 *            明文数据
	 * @return
	 * @throws Exception
	 *             加密过程中的异常信息
	 */
	public byte[] encrypt(RSAPrivateKey privateKey, byte[] plainTextData) throws Exception {
		if (privateKey == null) {
			throw new Exception("加密私钥为空, 请设置");
		}
		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", getInstance());
			cipher.init(Cipher.ENCRYPT_MODE, privateKey);
			byte[] output = cipher.doFinal(plainTextData);
			return output;
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("无此加密算法");
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
			return null;
		} catch (InvalidKeyException e) {
			throw new Exception("加密公钥非法,请检查");
		} catch (IllegalBlockSizeException e) {
			throw new Exception("明文长度非法");
		} catch (BadPaddingException e) {
			throw new Exception("明文数据已损坏");
		}
	}

	/**
	 * 解密过程
	 * 
	 * @param publicKey
	 *            公钥
	 * @param cipherData
	 *            密文数据
	 * @return 明文
	 * @throws Exception
	 *             解密过程中的异常信息
	 */
	public byte[] decrypt(RSAPublicKey publicKey, byte[] cipherData) throws Exception {
		if (publicKey == null) {
			throw new Exception("解密公钥为空, 请设置");
		}
		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", getInstance());
			cipher.init(Cipher.DECRYPT_MODE, publicKey);
			byte[] output = cipher.doFinal(cipherData);
			return output;
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("无此解密算法");
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
			return null;
		} catch (InvalidKeyException e) {
			throw new Exception("解密私钥非法,请检查");
		} catch (IllegalBlockSizeException e) {
			throw new Exception("密文长度非法");
		} catch (BadPaddingException e) {
			throw new Exception("密文数据已损坏");
		}
	}

	/**
	 * 字节数据转十六进制字符串
	 * 
	 * @param data
	 *            输入数据
	 * @return 十六进制内容
	 */
	public static String byteArrayToString(byte[] data) {
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < data.length; i++) {
			// 取出字节的高四位 作为索引得到相应的十六进制标识符 注意无符号右移
			stringBuilder.append(HEX_CHAR[(data[i] & 0xf0) >>> 4]);
			// 取出字节的低四位 作为索引得到相应的十六进制标识符
			stringBuilder.append(HEX_CHAR[(data[i] & 0x0f)]);
			if (i < data.length - 1) {
				stringBuilder.append(' ');
			}
		}
		return stringBuilder.toString();
	}

	public static String byte2hex(byte[] b) {
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1)
				hs = hs + "0" + stmp;
			else
				hs = hs + stmp;
		}
		return hs.toUpperCase();
	}

	public static byte[] hexStringToByte(String hex) {
		int len = (hex.length() / 2);
		byte[] result = new byte[len];
		char[] achar = hex.toCharArray();
		for (int i = 0; i < len; i++) {
			int pos = i * 2;
			result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
		}
		return result;
	}

	private static byte toByte(char c) {
		byte b = (byte) "0123456789ABCDEF".indexOf(c);
		return b;
	}

	/**
	 * 私钥加密
	 */
	public String enc(String code) {
		RSAEncrypt rsaEncrypt = new RSAEncrypt();
		String encryptValue = null;
		try {
			rsaEncrypt.loadPrivateKey(RSAEncrypt.class.getResourceAsStream(ConfigConstants.privatePath));
			// rsaEncrypt.loadPrivateKey(DEFAULT_PRIVATE_KEY);
			byte[] cipher = rsaEncrypt.encrypt(rsaEncrypt.getPrivateKey(), code.getBytes());
			encryptValue = byte2hex(cipher);
		} catch (Exception e) {
			System.err.println(e.getMessage());
			System.err.println("加载私钥失败");
		}
		return encryptValue;
	}

	/**
	 * 公钥解密
	 */
	public String dec(String signatureInfo) {
		RSAEncrypt rsaEncrypt = new RSAEncrypt();
		String Text = null;
		try {
			rsaEncrypt.loadPublicKey(RSAEncrypt.class.getResourceAsStream(publicPath));
			byte[] decCipher = hexStringToByte(signatureInfo);
			byte[] plainText = rsaEncrypt.decrypt(rsaEncrypt.getPublicKey(), decCipher);
			Text = new String(RSAEncrypt.byte2hex(plainText));
		} catch (Exception e) {
			System.err.println(e.getMessage());
			System.err.println("加载公钥失败");
		}
		return Text;
	}

	/**
	 * MD5加密
	 * 
	 * @param val
	 *            明文
	 * @return 密文
	 */
	public String MD5(String val) {
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		md5.update(val.getBytes());
		String code = RSAEncrypt.byte2hex(md5.digest());
		return code;
	}

	public static void main(String[] args) {
		RSAEncrypt rsaEncrypt = new RSAEncrypt();
		// 加载私钥
//		try {
//			File file = new File("D:/work/SaleWeb/pem/2/rsa_private_key.pem");
//			FileInputStream fis = new FileInputStream(file);
//			rsaEncrypt.loadPrivateKey(fis);// RSAEncrypt.class.getResourceAsStream("/configuration.properties"));
//		} catch (Exception e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		// 加载公钥
//		try {
//			File file = new File("D:/work/SaleWeb/pem/2/rsa_public_key.pem");
//			FileInputStream fis = new FileInputStream(file);
//			rsaEncrypt.loadPublicKey(fis);
//			System.out.println("加载公钥成功");
//		} catch (Exception e) {
//			System.err.println(e.getMessage());
//			System.err.println("加载公钥失败");
//		}

		// 测试字符串
		String encryptStr = "123456@qq.com";

		try {

			String code = rsaEncrypt.MD5(encryptStr);
			System.out.println("MD5加密：" + code);
			// 加密
			byte[] cipher = rsaEncrypt.encrypt(rsaEncrypt.getPrivateKey(), code.getBytes());
			System.out.println("私钥加密：" + RSAEncrypt.byte2hex(cipher));

			// 解密
			// String dec =
			// "0CCAC8DA8641F60D2011ED4A5174DBA0200A5D22F8E6080214CAE1321FE0B918D0B67447394990E27FC7CCEC4E1D0FCFFAAC07B2571F72CFD0475905429F6CC641AE913DF70AD05D1C578903D1EC9D8D6943CEB3405C4CF0E1882DBB34F0F9641EB38989889ABEDEDF81BCAE603EA2A2B3852CDFAC51E675D8E4EA151BED4A09";
			// String dec =
			// "715277AB9A90250DFFCFFFEAA530EC4B354BA72C740FCE0A5ACB57CE46C4599FA52E65E522EEEB69D1A35A7FF0021C1BEDB6734C79379AA96EBEFFB291F36E3ED9F18659022A726BEEC8BA74DA48BB08780C3E642AB08325787220EE3E909B429063F5BD5FE29556951E6A4BFE662742547E4DA559295D3E8D7B07985C073F3E";
			// byte[] decCipher = hexStringToByte(dec);
			byte[] plainText = rsaEncrypt.decrypt(rsaEncrypt.getPublicKey(), cipher);
			System.out.println("公钥解密：" + new String(plainText));

		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	// 单例模式
	public static synchronized BouncyCastleProvider getInstance() {
		if (bouncyCastleProvider == null) {
			bouncyCastleProvider = new BouncyCastleProvider();
		}
		return bouncyCastleProvider;
	}

}
