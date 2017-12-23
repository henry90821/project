package com.smi.pay.service;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.smi.pay.model.Order;
import com.smi.pay.model.OrderReturn;
import com.smi.pay.model.Refund;


public interface WXPayService {
	/**
	 * 微信H5登录，获取授权code(微信测试用，实际业务场景不调用)
	 * @return ModelAndView
	 * @throws Exception
	 * @since 2016-03-15
	 */
	 public ModelAndView login(HttpServletRequest request, 
	            HttpServletResponse response) throws IOException;
	
	 //公众号登录后获取openid
	 public ModelAndView getopenid(HttpServletRequest request, 
		            HttpServletResponse response) throws IOException;
		
	/**
	 * 移动应用调用支付接口，返回prepay_id和签名sign
	 * @return ModelAndView
	 * @throws Exception
	 * @since 2016-03-15
	 */
	public void prepay(Order order,OrderReturn orderReturn) throws IOException;
	
	public Map refund(Refund refund,Order order) throws IOException;
	
}
