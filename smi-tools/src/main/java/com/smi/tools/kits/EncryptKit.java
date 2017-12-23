package com.smi.tools.kits;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import com.smi.tools.exception.UtilException;
import com.smi.tools.lang.Conver;

/**
 * 安全相关工具类
 * 
 */
public class EncryptKit {
	
	private static final String ENCRYPT_KEY = "SMI#PASSPORT";
	public static final String MD2 = "MD2";
	public static final String MD4 = "MD4";
	public static final String MD5 = "MD5";
	/**
	 * DES算法字符串常量
	 */
	private static final String DES = "DES";

	public static final String SHA1 = "SHA-1";
	public static final String SHA256 = "SHA-256";

	public static final String HMAC_SHA1 = "HmacSHA1";

	public static final String RIPEMD128 = "RIPEMD128";
	public static final String RIPEMD160 = "RIPEMD160";

	/** base64码表 */
	private static char[] base64EncodeTable = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/' };

	private static final int[] INV = new int[256];

	static {
		Arrays.fill(INV, -1);
		for (int i = 0, iS = base64EncodeTable.length; i < iS; i++) {
			INV[base64EncodeTable[i]] = i;
		}
		INV['='] = 0;
	}

	/**
	 * 加密
	 * 
	 * @param source 被加密的字符串
	 * @param algorithmName 算法名
	 * @param charset 字符集
	 * @return 被加密后的值
	 */
	public static String encrypt(String source, String algorithmName, String charset) {
		return encrypt(StrKit.bytes(source, charset), algorithmName);
	}

	/**
	 * 加密
	 * 
	 * @param bytes 被加密的byte数组
	 * @param algorithmName 算法名
	 * @return 被加密后的值
	 */
	public static String encrypt(byte[] bytes, String algorithmName) {
		return Conver.toHex(encryptWithoutHex(bytes, algorithmName));
	}

	/**
	 * 加密，不对结果做Hex处理
	 * 
	 * @param bytes 被加密的byte数组
	 * @param algorithmName 算法名
	 * @return 被加密后的值
	 */
	public static byte[] encryptWithoutHex(byte[] bytes, String algorithmName) {
		return createMessageDigest(algorithmName).digest(bytes);
	}
	
	/**
	 * 加密文件
	 * 
	 * @param file 被加密的文字
	 * @param algorithmName 算法名
	 * @return 被加密后的字符串
	 */
	public static String encrypt(File file, String algorithmName) {
		return Conver.toHex(encryptWithoutHex(file, algorithmName));
	}
	
	/**
	 * 加密文件，不对结果做Hex处理
	 * 
	 * @param file 被加密的文字
	 * @param algorithmName 算法名
	 * @return 被加密后的值
	 */
	public static byte[] encryptWithoutHex(File file, String algorithmName) {
		final byte[] buffer = new byte[8192];
		MessageDigest md = createMessageDigest(algorithmName);
		BufferedInputStream in = null;
		try {
			in = FileKit.getInputStream(file);
			int length;
			while ((length = in.read(buffer)) != -1) {
				md.update(buffer, 0, length);
			}
		} catch (IOException e) {
			throw new UtilException(e);
		}finally{
			IoKit.close(in);
		}
		return md.digest();
	}
	
	/**
	 * 创建MessageDigest
	 * 
	 * @param algorithmName 算法名
	 * @return 被加密后的值
	 */
	public static MessageDigest createMessageDigest(String algorithmName) {
		MessageDigest md = null;
		try {
			if (StrKit.isBlank(algorithmName)) {
				algorithmName = MD5;
			}
			md = MessageDigest.getInstance(algorithmName);
		} catch (NoSuchAlgorithmException e) {
			throw new UtilException(StrKit.format("No such algorithm name for: {}", algorithmName));
		}
		return md;
	}

	/**
	 * SHA-1算法加密
	 * 
	 * @param source 被加密的字符串
	 * @param charset 字符集
	 * @return 被加密后的字符串
	 */
	public static String sha1(String source, String charset) {
		return encrypt(source, SHA1, charset);
	}
	
	/**
	 * SHA-1算法加密
	 * 
	 * @param file 被加密的字符串
	 * @return 被加密后的字符串
	 */
	public static String sha1(File file) {
		return encrypt(file, SHA1);
	}

	// ------------------------------------------------------------------------ MAC
	/**
	 * MAC 算法加密
	 * 
	 * @param algorithm 算法
	 * @param key 加密使用的key
	 * @param data 待加密的数据
	 * @return 被加密后的bytes
	 */
	public static byte[] mac(String algorithm, byte[] key, byte[] data) {
		Mac mac = null;
		try {
			mac = Mac.getInstance(algorithm);
			mac.init(new SecretKeySpec(key, algorithm));
		} catch (NoSuchAlgorithmException e) {
			throw new UtilException(e, "No such algorithm: {}", algorithm);
		} catch (InvalidKeyException e) {
			throw new UtilException(e, "Invalid key: {}", key);
		}
		return mac == null ? null : mac.doFinal(data);
	}

	/**
	 * MAC SHA-1算法加密
	 * 
	 * @param key 加密使用的key
	 * @param data 待加密的数据
	 * @return 被加密后的bytes
	 */
	public static byte[] sha1(byte[] key, byte[] data) {
		return mac(HMAC_SHA1, key, data);
	}

	/**
	 * MAC SHA-1算法加密
	 * 
	 * @param key 加密使用的key
	 * @param data 被加密的字符串
	 * @param charset 字符集
	 * @return 被加密后的字符串
	 */
	public static String sha1(String key, String data, String charset) {
		final Charset charsetObj = Charset.forName(charset);
		final byte[] bytes = sha1(key.getBytes(charsetObj), data.getBytes(charsetObj));
		return base64(bytes, charset);
	}

	/**
	 * 初始化HMAC密钥
	 * 
	 * @param algorithm 算法
	 * @param charset 字符集
	 * @return key
	 * @throws Exception
	 */
	public static String initMacKey(String algorithm, String charset) throws Exception {
		return base64(KeyGenerator.getInstance(algorithm).generateKey().getEncoded(), charset);
	}

	/**
	 * MD5算法加密
	 * 
	 * @param source 被加密的字符串
	 * @return 被加密后的字符串
	 */
	public static String md5(byte[] source) {
		return encrypt(source, MD5);
	}
	
	/**
	 * MD5算法加密
	 * 
	 * @param file 被加密的文件
	 * @return 被加密后的字符串
	 */
	public static String md5(File file) {
		return encrypt(file, MD5);
	}
	
	/**
	 * MD5算法加密, 默认采用 UTF-8编码
	 * 
	 * @param source 被加密的字符串
	 * @return 被加密后的字符串
	 */
	public static String md5(String source) {
		return EncryptKit.md5(source, CharsetKit.UTF_8);
	}
	
	/**
	 * MD5算法加密
	 * 
	 * @param source 被加密的字符串
	 * @param charset 字符集
	 * @return 被加密后的字符串
	 */
	public static String md5(String source, String charset) {
		return encrypt(source, MD5, charset);
	}

	/**
	 * 编码为Base64
	 * 
	 * @param arr 被编码的数组
	 * @param lineSep 在76个char之后是CRLF还是EOF
	 * @return 编码后的bytes
	 */
	public static byte[] base64(byte[] arr, boolean lineSep) {
		int len = arr != null ? arr.length : 0;
		if (len == 0) {
			return new byte[0];
		}

		int evenlen = (len / 3) * 3;
		int cnt = ((len - 1) / 3 + 1) << 2;
		int destlen = cnt + (lineSep ? (cnt - 1) / 76 << 1 : 0);
		byte[] dest = new byte[destlen];

		for (int s = 0, d = 0, cc = 0; s < evenlen;) {
			int i = (arr[s++] & 0xff) << 16 | (arr[s++] & 0xff) << 8 | (arr[s++] & 0xff);

			dest[d++] = (byte) base64EncodeTable[(i >>> 18) & 0x3f];
			dest[d++] = (byte) base64EncodeTable[(i >>> 12) & 0x3f];
			dest[d++] = (byte) base64EncodeTable[(i >>> 6) & 0x3f];
			dest[d++] = (byte) base64EncodeTable[i & 0x3f];

			if (lineSep && ++cc == 19 && d < destlen - 2) {
				dest[d++] = '\r';
				dest[d++] = '\n';
				cc = 0;
			}
		}

		int left = len - evenlen;
		if (left > 0) {
			int i = ((arr[evenlen] & 0xff) << 10) | (left == 2 ? ((arr[len - 1] & 0xff) << 2) : 0);

			dest[destlen - 4] = (byte) base64EncodeTable[i >> 12];
			dest[destlen - 3] = (byte) base64EncodeTable[(i >>> 6) & 0x3f];
			dest[destlen - 2] = left == 2 ? (byte) base64EncodeTable[i & 0x3f] : (byte) '=';
			dest[destlen - 1] = '=';
		}
		return dest;
	}
	
	/**
	 * base64编码, 默认采用UTF-8 字符编码
	 * @param source 被编码的base64字符串
	 * @return 被加密后的字符串
	 */
	public static String base64(String source) {
		return EncryptKit.base64(source, CharsetKit.UTF_8);
	}

	/**
	 * base64编码
	 * 
	 * @param source 被编码的base64字符串
	 * @param charset 字符集
	 * @return 被加密后的字符串
	 */
	public static String base64(String source, String charset) {
		return StrKit.str(base64(StrKit.bytes(source, charset), false), charset);
	}

	/**
	 * base64编码
	 * 
	 * @param source 被编码的base64字符串
	 * @param charset 字符集
	 * @return 被加密后的字符串
	 */
	public static String base64(byte[] source, String charset) {
		return StrKit.str(base64(source, false), charset);
	}
	
	/**
	 * base64解码, 默认采用 UTF-8 字符编码
	 * 
	 * @param source 被解码的base64字符串
	 * @return 被加密后的字符串
	 */
	public static String decodeBase64(String source) {
		return EncryptKit.decodeBase64(source, CharsetKit.UTF_8);
	}

	/**
	 * base64解码
	 * 
	 * @param source 被解码的base64字符串
	 * @param charset 字符集
	 * @return 被加密后的字符串
	 */
	public static String decodeBase64(String source, String charset) {
		return StrKit.str(decodeBase64(StrKit.bytes(source, charset)), charset);
	}

	/**
	 * 解码Base64
	 * 
	 * @param arr byte数组
	 * @return 解码后的byte数组
	 */
	public static byte[] decodeBase64(byte[] arr) {
		int length = arr.length;
		if (length == 0) {
			return new byte[0];
		}

		int sndx = 0, endx = length - 1;
		int pad = arr[endx] == '=' ? (arr[endx - 1] == '=' ? 2 : 1) : 0;
		int cnt = endx - sndx + 1;
		int sepCnt = length > 76 ? (arr[76] == '\r' ? cnt / 78 : 0) << 1 : 0;
		int len = ((cnt - sepCnt) * 6 >> 3) - pad;
		byte[] dest = new byte[len];

		int d = 0;
		for (int cc = 0, eLen = (len / 3) * 3; d < eLen;) {
			int i = INV[arr[sndx++]] << 18 | INV[arr[sndx++]] << 12 | INV[arr[sndx++]] << 6 | INV[arr[sndx++]];

			dest[d++] = (byte) (i >> 16);
			dest[d++] = (byte) (i >> 8);
			dest[d++] = (byte) i;

			if (sepCnt > 0 && ++cc == 19) {
				sndx += 2;
				cc = 0;
			}
		}

		if (d < len) {
			int i = 0;
			for (int j = 0; sndx <= endx - pad; j++) {
				i |= INV[arr[sndx++]] << (18 - j * 6);
			}
			for (int r = 16; d < len; r -= 8) {
				dest[d++] = (byte) (i >> r);
			}
		}

		return dest;
	}
	
	/**
	 * @return 简化的UUID，去掉了横线
	 */
	public static String simpleUUID(){
		return UUID.randomUUID().toString().replace("-", "");
	}
	
	/**
	 * DES加密
	 * 
	 * @param toEncryptStr
	 *            待加密字符串
	 * @param enCryptKey
	 *            加密使用的key(长度为8个字节)，如果为null或者"", 则使用默认的key
	 * @return 加密后的字符串
	 */
	public static String desEncrypt(String toEncryptStr, String enCryptKey) {

		if (StringUtils.isEmpty(toEncryptStr)) {
			return "";
		}
		byte[] bytes = toEncryptStr.getBytes();
		try {
			Cipher cipher = Cipher.getInstance(DES);
			Key key = null;
			if (StringUtils.isEmpty(enCryptKey)) {
				key = getKey(ENCRYPT_KEY);
			} else {
				key = getKey(enCryptKey);
			}
			cipher.init(Cipher.ENCRYPT_MODE, key);
			byte[] encryptStrBytes = cipher.doFinal(bytes);
			return Base64.encodeBase64String(encryptStrBytes);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * DES解密
	 * 
	 * @param toDecryptStr
	 *            待解密的字符串
	 * @param enCryptKey
	 *            解密使用的key(长度为8个字节，要与加密的key保持一致)，如果为null或者"", 则使用默认的key
	 * @return 解密后的字符串
	 */
	public static String desDecrypt(String toDecryptStr, String enCryptKey) {
		if (StringUtils.isEmpty(toDecryptStr)) {
			return "";
		}
		try {
			byte[] bytes = Base64.decodeBase64(toDecryptStr);
			Cipher cipher = Cipher.getInstance(DES);
			Key key = null;
			if (StringUtils.isEmpty(enCryptKey)) {
				key = getKey(ENCRYPT_KEY);
			} else {
				key = getKey(enCryptKey);
			}
			cipher.init(Cipher.DECRYPT_MODE, key);
			bytes = cipher.doFinal(bytes);
			return new String(bytes);
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}

	/**
	 * DES 加密（使用默认的key进行加密）
	 * 
	 * @param toEncryptStr
	 *            待加密字符串
	 * @return 加密后字符串
	 */
	public static String desEncrypt(String toEncryptStr) {
		return desEncrypt(toEncryptStr, ENCRYPT_KEY);
	}

	/**
	 * DES解密 （使用默认的key进行解密）
	 * 
	 * @param toDecryptStr
	 *            待解密字符串
	 * @return 解密后字符串
	 */
	public static String desDecrypt(String toDecryptStr) {
		return desDecrypt(toDecryptStr, ENCRYPT_KEY);
	}

	private static Key getKey(String strKey) {
		try {
			DESKeySpec objDesKeySpec = new DESKeySpec(strKey.getBytes(CharsetKit.UTF_8));
			SecretKeyFactory objKeyFactory = SecretKeyFactory.getInstance(DES);
			return objKeyFactory.generateSecret(objDesKeySpec);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
