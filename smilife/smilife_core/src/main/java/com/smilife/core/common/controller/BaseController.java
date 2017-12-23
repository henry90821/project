package com.smilife.core.common.controller;

import com.smilife.core.common.valueobject.BaseValueObject;
import com.smilife.core.common.valueobject.enums.CodeEnum;
import com.smilife.core.exception.NoLoginException;
import com.smilife.core.exception.SmiBusinessException;
import com.smilife.core.exception.SmiParamException;
import com.smilife.core.exception.WxNoSubscribeException;
import com.smilife.core.utils.MessageSourceUtil;
import com.smilife.core.utils.logger.Logger;
import com.smilife.core.utils.logger.LoggerUtils;
import net.bull.javamelody.MonitoredWithSpring;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Andriy on 16/4/4.
 */
@RestController
@MonitoredWithSpring
public class BaseController {
	private static final Logger LOGGER = LoggerUtils.getLogger(BaseController.class);

	/**
	 * 系统业务异常统一处理函数
	 *
	 * @param e
	 * @return
	 */
	@ExceptionHandler(value = SmiBusinessException.class)
	public BaseValueObject exceptionHandler(SmiBusinessException e) {
		LOGGER.error("发生系统业务异常!", e);
		BaseValueObject result = new BaseValueObject();
		result.setCode(CodeEnum.SERVER_INNER_ERROR);
		result.setMsg(e.getMessage());
		return result;
	}

	/**
	 * 系统未知异常统一处理函数
	 *
	 * @param e
	 * @return
	 */
	@ExceptionHandler(value = RuntimeException.class)
	public BaseValueObject exceptionHandler(RuntimeException e) {
		LOGGER.error(MessageSourceUtil.getMessage("comm.default"), e);
		BaseValueObject result = new BaseValueObject();
		result.setCode(CodeEnum.SERVER_INNER_ERROR);
		result.setMsg(MessageSourceUtil.getMessage("comm.default"));
		return result;
	}

	/**
	 * 请求参数缺失异常统一处理函数
	 *
	 * @param e
	 * @return
	 */
	@ExceptionHandler(value = MissingServletRequestParameterException.class)
	public BaseValueObject exceptionHandler(MissingServletRequestParameterException e) {
		LOGGER.warn(MessageSourceUtil.getMessage("comm.param.empty", new Object[] { e.getParameterName() }));
		BaseValueObject result = new BaseValueObject();
		result.setCode(CodeEnum.REQUEST_ERROR);
		result.setMsg(MessageSourceUtil.getMessage("comm.param.empty", new Object[] { e.getParameterName() }));
		return result;
	}

	/**
	 * 客户端未登录异常统一处理函数
	 *
	 * @param e
	 * @return
	 */
	@ExceptionHandler(value = NoLoginException.class)
	public BaseValueObject exceptionHandler(NoLoginException e) {
		LOGGER.warn(MessageSourceUtil.getMessage("comm.not.logged"));
		BaseValueObject result = new BaseValueObject();
		result.setCode(CodeEnum.NOT_LOGGED_IN);
		result.setMsg(MessageSourceUtil.getMessage("comm.not.logged"));
		return result;
	}

	/**
	 * 系统参数异常统一处理函数
	 *
	 * @param e
	 * @return
	 */
	@ExceptionHandler(value = SmiParamException.class)
	public BaseValueObject exceptionHandler(SmiParamException e) {
		LOGGER.error("请求参数[" + e.getParamName() + "]不合法!", e);
		BaseValueObject result = new BaseValueObject();
		result.setCode(CodeEnum.REQUEST_ERROR);
		result.setMsg("请求参数[" + e.getParamName() + "]不合法!");
		return result;
	}

	/**
	 * 微信用户未关注公众号异常统一处理函数
	 *
	 * @param e
	 * @return
	 */
	@ExceptionHandler(value = WxNoSubscribeException.class)
	public BaseValueObject exceptionHandler(WxNoSubscribeException e) {
		LOGGER.warn(MessageSourceUtil.getMessage("comm.no.subscribe"));
		BaseValueObject result = new BaseValueObject();
		result.setCode(CodeEnum.NO_SUBSCRIBE_ERROR);
		result.setMsg(MessageSourceUtil.getMessage("comm.no.subscribe"));
		return result;
	}

	/**
	 * 方法参数验证不合法异常处理
	 * 
	 * @param e
	 * @return
	 */
	@ExceptionHandler(value = MethodArgumentNotValidException.class)
	public BaseValueObject exceptionHandler(MethodArgumentNotValidException e) {
		String field = e.getBindingResult().getFieldError().getField();
		String fieldErrorMsg = e.getBindingResult().getFieldError().getDefaultMessage();
		String objectName = e.getBindingResult().getObjectName();
		String errorMsg = "参数" + objectName + "的属性" + field + "的值"
				+ (ObjectUtils.isEmpty(fieldErrorMsg) ? "不合法!" : fieldErrorMsg);
		LOGGER.warn(errorMsg);
		BaseValueObject result = new BaseValueObject();
		result.setCode(CodeEnum.REQUEST_ERROR);
		result.setMsg(errorMsg);
		return result;
	}
}
