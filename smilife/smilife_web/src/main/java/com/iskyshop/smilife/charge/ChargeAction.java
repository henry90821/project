package com.iskyshop.smilife.charge;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.iskyshop.core.annotation.Token;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.User;

@Controller
@RequestMapping(value="/api/app")
public class ChargeAction {
	@Autowired
	private IChargeService chargeService;

	/**
	 * 账户充值接口
	 * @author chuzhisheng
	 * @version 1.0
	 * @date 2016年3月22日 下午1:30:57
	 * @param id
	 * @param amount
	 * @param type
	 * @param remark
	 * @return
	 */
	@RequestMapping(value="/mall0901Charge.htm",method=RequestMethod.POST,produces={"application/json"} )
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@Token
	public Object mall0901Charge(HttpServletRequest request,User user,String amount,String custId,String remark) {
		return chargeService.charge(user,amount,custId,remark,CommUtil.getURL(request));
	}
	
}
