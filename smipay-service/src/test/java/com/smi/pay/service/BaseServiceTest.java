/**
 * 文件名称：BaseServiceTest.java
 * 版权：CopyRight 2014-2015 Digione Tech. Co. Ltd. All Rigths Reserved. 
 * 描述：
 * 修改人：carson
 * 修改时间：2015年3月12日下午4:20:33
 */
package com.smi.pay.service;

import com.smi.pay.common.BaseWebIntegrationTests;
import com.smi.tools.kits.ObjectKit;
import org.apache.log4j.Logger;

/**
 * 功能描述：<简单描述> <br/>
 * 作者： carson <br/>
 * 时间： 2015年10月12日下午4:20:33 <br/>
 * BugID: <br/>
 * 修改内容： <br/>
 */
public class BaseServiceTest extends BaseWebIntegrationTests {

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
			logger.info("result=> " + obj);
		}

		logger.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
	}
}
