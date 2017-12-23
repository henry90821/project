package com.smi.sms.scheduler;

import org.apache.log4j.Logger;

import com.smi.sms.common.SendLimitCache;

public class CleanCacheJob {
	private Logger logger = Logger.getLogger(this.getClass());
	public void work() {
		logger.info("========== 清空缓存数据 开始 ==============");
		SendLimitCache.cleanCache();
		logger.info("========== 清空缓存数据 结束==============");
	}
}
