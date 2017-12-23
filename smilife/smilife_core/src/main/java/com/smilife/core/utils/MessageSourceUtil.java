package com.smilife.core.utils;

import com.smi.tools.kits.StrKit;
import com.smilife.core.exception.SmiBusinessException;
import com.smilife.core.utils.logger.Logger;
import com.smilife.core.utils.logger.LoggerUtils;
import org.springframework.context.MessageSource;

import java.util.Locale;

/**
 * 消息资源处理工具类
 */

public class MessageSourceUtil {
	private static final Logger LOGGER = LoggerUtils.getLogger(MessageSourceUtil.class);

	public static final Locale DEFAULT_LOCAL = Locale.SIMPLIFIED_CHINESE;

	public static MessageSource getMessageSource() {
		return ApplicationContextUtil.getBean(MessageSource.class);
	}

	/**
	 * 获取资源文件信息
	 *
	 * @param msgkey
	 * @param paramArrayOfObject
	 *            参数
	 * @param defaultmsg
	 *            默认信息,没有则传值为null
	 * @return
	 */
	public static String getMessage(String msgkey, Object[] paramArrayOfObject, String defaultmsg) {
		if (StrKit.isEmpty(msgkey)) {
			return "";
		}
		MessageSource messageSource = getMessageSource();
		if (messageSource == null) {
			LOGGER.error("The messageSource is null.");
			throw new SmiBusinessException("The messageSource is null.");
		}
		return messageSource.getMessage(msgkey, paramArrayOfObject, defaultmsg, DEFAULT_LOCAL);
	}

	/**
	 * 获取资源文件信息
	 *
	 * @param msgkey
	 *            the key
	 * @param defaultmsg
	 *            默认消息
	 * @return
	 */
	private static String getMessage(String msgkey, String defaultmsg) {
		return getMessage(msgkey, new Object[1], defaultmsg);
	}

	/**
	 * 获取资源文件信息
	 *
	 * @param msgkey
	 *            the key
	 * @return
	 */
	public static String getMessage(String msgkey) {
		String defaultmsg = null;
		return getMessage(msgkey, defaultmsg);
	}

	public static String getMessage(String msgkey, Object[] paramArrayOfObject) {
		return getMessage(msgkey, paramArrayOfObject, null);
	}

}
