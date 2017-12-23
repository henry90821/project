package com.iskyshop.smilife.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.iskyshop.core.annotation.Token;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.smilife.common.Result;
import com.iskyshop.smilife.enums.ErrorEnum;


@Controller
@RequestMapping(value="/api/app")
public class AppSubjectViewAction {
	private Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private IAppSubjectService appSubjectService;
	
	/**
     * 获取专题列表信息和商品
     * @method getSubjectList
     * @return List<Map>
     */
	@RequestMapping(value="/mall0101getItemAdverts.htm",produces={"application/json"} )
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public Object mall0101getItemAdverts(HttpServletRequest request, HttpServletResponse response,String currentPage) {
		Result result=new Result();
		result.put("code", ErrorEnum.SUCCESS.getIndex());//返回结果代码
		result.put("msg", ErrorEnum.SUCCESS.getDescr());//返回结果描述
		List<Map> entity = null;
		try {
			entity =  appSubjectService.getSubjectList();
		} catch (Exception e) {
			logger.error(e);
			result.set(ErrorEnum.SYSTEM_ERROR);
			result.put("msg", "服务器出错了，请稍后重试");
		}
		result.put("entity", entity);
		return result;
	}
	
	/**
	 * 显示优惠券礼包
	 * @param user 登录用户（必须登录）
	 * @param type 优惠券礼包类型 [register680:表示680新手优惠券礼包,其它待不全] 
	 * @param id 活动ID
	 * @return 优惠券列表
	 */
	@RequestMapping(value="/mall0102showCouponBag.htm",produces={"application/json"} )
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@Token 
	public Object mall0102showCouponBag(HttpServletRequest request,User user,String type,long id)
	{
		Result result=new Result();
		result.put("code", ErrorEnum.SUCCESS.getIndex());//返回结果代码
		result.put("msg", ErrorEnum.SUCCESS.getDescr());//返回结果描述
		
		if(user==null){
			//用户必须登录
			result.put("code", ErrorEnum.LOGIN_INVALID.getIndex());
			result.put("msg", ErrorEnum.LOGIN_INVALID.getDescr());
			return result;
		}
		
		List<Map<String,Object>> coupons=null;
		try{
			//页面标题
			String pageTitle="";
			//680新人优惠礼包
			if("register680".equals(type)){
				coupons=appSubjectService.getRegister680CouponBag(user,id);
				pageTitle="新手680优惠券";
			}
			
			Map<String,Object> data=new HashMap<String,Object>();
			data.put("list", coupons);
			data.put("pageTitle", pageTitle);
			data.put("totalCount", coupons==null ? 0 : coupons.size());
			data.put("pageCount", 1);
			result.setData(data);
		}catch(Exception e){
			logger.error(e);
			result.set(ErrorEnum.SYSTEM_ERROR);
			result.put("msg", "服务器出错了，请稍后重试");
		}
		
		return result;
	}
	
	/**
	 * 领取优惠券礼包
	 * @param user 登录用户（必须登录）
	 * @param type 优惠券礼包类型 [register680:表示680新手优惠券礼包,其它待不全] 
	 * @param id 活动ID
	 * @return code 1:表示成功 -1:合法的优惠礼包 -2:已经参加优惠礼包 -3：没有足够的优惠券 -4:表示不是新注册的用户
	 */
	@RequestMapping(value="/mall0103getCouponBag.htm",produces={"application/json"} )
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@Token 
	public Object mall0103getCouponBag(HttpServletRequest request,User user,String type,long id)
	{
		Result result=new Result();
		result.put("code", ErrorEnum.SUCCESS.getIndex());//返回结果代码
		result.put("msg", ErrorEnum.SUCCESS.getDescr());//返回结果描述
		if(user==null){
			//用户必须登录
			result.put("code", ErrorEnum.LOGIN_INVALID.getIndex());
			result.put("msg", ErrorEnum.LOGIN_INVALID.getDescr());
			return result;
		}
		
		try{
			//检查领取的优惠礼包
			Map<String,String> statusMap=new HashMap<String,String>();
			int code=appSubjectService.saveToUserCouponBag(user,type,id);
			String status_msg="领取优惠礼包成功";
			String status_code="1";
			
			switch(code){
				case 1:
					break;
				case -1:
					status_code="-1";
					status_msg="非法合法的优惠礼包";
					break;
				case -2:
					status_code="-2";
					status_msg="已经参加优惠礼包";
					break;
				case -3:
					status_code="-3";
					status_msg="没有足够的优惠券";
					break;
				case -4:
					status_code="-4";
					status_msg="您不是680新手活动的新用户";
				default:
					status_code="-5";
					status_msg="未知的错误";
					break;
			}
			
			
			status_code=String.valueOf(status_code);
			statusMap.put("status", status_code);
			statusMap.put("msg",status_msg);
			result.setData(statusMap);
			return result;
		
		}catch(Exception e){
			logger.error(e);
			result.put("code",ErrorEnum.SYSTEM_ERROR.getIndex());
			result.put("msg", "服务器出错了，请稍后重试");
		}
		
		return result;
	}
}
