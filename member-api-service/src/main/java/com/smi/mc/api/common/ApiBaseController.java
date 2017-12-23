package com.smi.mc.api.common;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smilife.core.common.controller.BaseController;
import com.smilife.core.common.valueobject.BaseValueObject;
import com.smilife.core.common.valueobject.enums.CodeEnum;

import net.bull.javamelody.MonitoredWithSpring;

/**
 * 接口系统对外服务基类<br/>
 * Created by Andriy on 16/9/19.
 */
@RestController
@MonitoredWithSpring
@RequestMapping(value = ApiBaseController.BASE_PATH)
public class ApiBaseController extends BaseController {

	/**
	 * 接口访问地址的基础前缀
	 */
	public static final String BASE_PATH = "";

	/**
	 * 接口V1版本号
	 */
	protected static final String API_VERSION_V1 = "/V1";
	
	/**
	 * <p>Description: 捕捉接口普通入参校验合法异常</p>
	 * <p>Company: SMI</p> 
	 * @author YANGHAILONG
	 * @date 2016年10月10日 下午4:17:51
	 */
	@ExceptionHandler(value = ConstraintViolationException.class)
	public BaseValueObject exceptionHandler(ConstraintViolationException e) {
		String msg = "";
		for (ConstraintViolation<?> constraintViolation : e.getConstraintViolations()) {
			msg +=  "参数值：" + constraintViolation.getInvalidValue() + "不合法，原因是："+ constraintViolation.getMessageTemplate();
			break;
        }
		BaseValueObject result = new BaseValueObject();
		result.setCode(CodeEnum.REQUEST_ERROR);
		result.setMsg(msg);
		return result;
	}
}
