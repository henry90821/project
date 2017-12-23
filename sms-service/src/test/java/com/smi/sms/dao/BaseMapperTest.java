
package com.smi.sms.dao;

import org.apache.log4j.Logger;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.smi.tools.kits.JsonKit;
import com.smi.tools.kits.ObjectKit;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring.xml" })
public class BaseMapperTest {
protected Logger logger = Logger.getLogger(this.getClass());
	
	/**
	 * 打印信息
	 * @param obj 待打印的对象
	 */
	public void printInfo(Object obj) {
		logger.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ ");
		if (ObjectKit.isNull(obj)) {
			logger.info("没有查询到数据，返回对象为NULL");
		} else {
			logger.info(JsonKit.toJsonString(obj));
		}
		
		logger.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
	}
}
