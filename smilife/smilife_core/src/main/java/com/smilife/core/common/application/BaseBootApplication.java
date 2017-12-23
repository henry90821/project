package com.smilife.core.common.application;

import com.smilife.core.utils.logger.Logger;
import com.smilife.core.utils.logger.LoggerUtils;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

/**
 * Created by Andriy on 16/5/20.
 */
@ServletComponentScan({ "com.smi", "com.smilife" })
@SpringBootApplication(scanBasePackages = { "com.smi", "com.smilife" })
public class BaseBootApplication {
	private static final Logger LOGGER = LoggerUtils.getLogger(BaseBootApplication.class);
	protected static final String DEFAULT_CHARSET = "UTF-8";

	public BaseBootApplication() {
		LOGGER.info("设定系统字符编码为{}", DEFAULT_CHARSET);
		System.setProperty("file.encoding", DEFAULT_CHARSET);
	}
}
