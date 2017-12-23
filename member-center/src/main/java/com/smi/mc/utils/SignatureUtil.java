package com.smi.mc.utils;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import org.mule.config.DefaultMuleConfiguration;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 数字签名的检证与 产生
 * 
 * @author Administrator
 *
 */
public class SignatureUtil {

	public static String home = new DefaultMuleConfiguration().getMuleHomeDirectory() + "/conf/bcp_xm";

	/**
	 * 数字签名的检证
	 * 
	 * @param reqJson
	 *            请求报文
	 * @throws Exception
	 *             抛出错误说明检证没有通过， 没有抛出错误则通过检证
	 */
	public static void verifySignature(String reqJson) throws Exception {
		try {

			RSAEncrypt rsaEncrypt = new RSAEncrypt();

			// 加载公钥
			try {
//				File file = new File(home + "/rsa_public_key.pem");
//				FileInputStream fs = new FileInputStream(file);
				rsaEncrypt.loadPublicKey(RSAEncrypt.DEFAULT_PUBLIC_KEY);// RSAEncrypt.class.getResourceAsStream("/configuration.properties"));
			} catch (Exception e) {
				throw new Exception("加载公钥失败");
			}

			// 得到报文摘要原文
			String sooCont = JSONParse.getSvcContJson(reqJson);

			// 对报文摘要原文用MD5加密
			String contMd5 = rsaEncrypt.MD5(sooCont);

			// 获得请求报文中的签名
			String SignatureInfo = JSONParse.getString(reqJson, "SignatureInfo");

			// 用对应公司的公钥对 数字签名进行 解密
			byte[] plainText = rsaEncrypt.decrypt(rsaEncrypt.getPublicKey(), rsaEncrypt.hexStringToByte(SignatureInfo));
			String contMd51 = new String(plainText);
			// 检证两者是否相等
			if (!contMd5.equals(contMd51)) {
				throw new Exception("数字签名不一致");
			}
		} catch (Exception e) {
			throw new Exception("数字签名验证出错：" + e.getMessage());
		}
	}

	/**
	 * 产生数据签名
	 * 
	 * @param respSvcJson
	 *            应答报文
	 * @return
	 * @throws Exception
	 */
	public static String generateSignature(String respJson) throws Exception {
		String signatureString = "";
		RSAEncrypt rsaEncrypt = new RSAEncrypt();

		// secret.private.key.filepath = "/com/tydic/util/rsa_private_key.pem";
		// EXT_SYSTEM.100005.public.key.filepath =
		// "/com/tydic/util/rsa_public_key.pem";
		try {
			// rsaEncrypt.loadPrivateKey(SignatureUtil.class.getClass().getResourceAsStream(privatekeyPath));
			File file = new File(home + "/rsa_private_key.pem");
			FileInputStream fs = new FileInputStream(file);
			rsaEncrypt.loadPrivateKey(fs);// RSAEncrypt.class.getResourceAsStream("/configuration.properties"));
		} catch (Exception e1) {
			e1.printStackTrace();
			throw new Exception(e1.getMessage());
		}

		// 得到要进行签名的报文摘要
		String sooCont = JSONParse.getSvcContJson(respJson);

		System.out.println("sooCont:" + sooCont);
		// 对报文摘要用MD5进行加密
		String contMd5 = rsaEncrypt.MD5(sooCont);
		// 对MD5加密后的摘要用 密钥 进行加密得到数据签名
		byte[] signatureBytes = rsaEncrypt.encrypt(rsaEncrypt.getPrivateKey(), contMd5.getBytes());

		signatureString = rsaEncrypt.byte2hex(signatureBytes);

		return signatureString;
	}

	public static void main(String[] args) {
		RSAEncrypt rsaEncrypt = new RSAEncrypt();
		String privateKey = "{'SvcCont':{{'SOO': [{'PUB_REQ': { 'TYPE': 'BD_CUST_ADD' },'CUST':{'CHANNEL_CODE': '10020','STAFF_ID':'10020','SYSTEM_ID':'120','MOBILE': '15297765280','BAIDU_UID':'1','BALANCE':'100000'}}]}}}";
		String sooCont;
		try {
			sooCont = JSONParse.getSvcContJson(privateKey);
			System.out.println(sooCont.toString());
			String md = rsaEncrypt.MD5(sooCont);
			System.out.println(md.toString());
			System.out.println(rsaEncrypt.dec(md));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
