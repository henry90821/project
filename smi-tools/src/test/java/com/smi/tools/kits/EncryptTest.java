package com.smi.tools.kits;

import java.util.List;

import org.junit.Test;

public class EncryptTest {

	@Test
	public void testDES() {
		String scr = "appcode=1002&reqno=98723623&sign=yuweuy238wueiw&username=11232&password=2323";
		
		String des = EncryptKit.desEncrypt(scr);
		System.out.println("DES加密后： " + des);
		System.out.println("DES解密后： " + EncryptKit.desDecrypt(des));
		
		String bas64= EncryptKit.base64(scr, CharsetKit.UTF_8);
		System.out.println("BASE64加密后： " + bas64);;
		System.out.println("BASE64解密后： " + EncryptKit.decodeBase64(bas64, CharsetKit.UTF_8));
		
		List<String> list = null;
	}
}
