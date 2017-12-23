
package com.smi.pay.dao;

import com.smi.pay.common.BaseWebIntegrationTests;
import com.smi.tools.kits.JsonKit;
import com.smi.tools.kits.ObjectKit;
import org.apache.log4j.Logger;

public class BaseMapperTest extends BaseWebIntegrationTests {
	protected Logger logger = Logger.getLogger(this.getClass());

	/**
	 * 打印信息
	 * 
	 * @param obj
	 *            待打印的对象
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
