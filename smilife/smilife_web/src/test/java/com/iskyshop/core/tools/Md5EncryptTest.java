package com.iskyshop.core.tools;

import com.iskyshop.core.base.BaseTester;
import org.junit.Test;

/**
 * Md5加密测试类
 * 
 * @author Liangshuai
 *
 */
public class Md5EncryptTest extends BaseTester {

	@Test
	public void testMd5Encrypt() {
		LOGGER.info(Md5Encrypt.md5("123qwe"));
	}
}
