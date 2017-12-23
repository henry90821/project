package com.iskyshop.foundation.service.impl;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.dao.IGenericDAO;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.query.GenericPageList;
import com.iskyshop.core.query.PageObject;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Predeposit;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.IPredepositService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.smilife.charge.IChargeService;
import com.iskyshop.smilife.common.Result;
import com.iskyshop.smilife.enums.ErrorEnum;
import com.iskyshop.smilife.payCenter.IpayCenterService;

@Service
@Transactional
public class PredepositServiceImpl implements IPredepositService{
	@Autowired
	private IpayCenterService payCenterService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IChargeService chargeService;
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
		
	
	@Resource(name = "predepositDAO")
	private IGenericDAO<Predeposit> predepositDao;
	
	@Transactional(readOnly = false)
	public boolean save(Predeposit predeposit) {
		/**
		 * init other field here
		 */
		try {
			this.predepositDao.save(predeposit);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@Transactional(readOnly = true)
	public Predeposit getObjById(Long id) {
		Predeposit predeposit = this.predepositDao.get(id);
		if (predeposit != null) {
			return predeposit;
		}
		return null;
	}
	
	@Transactional(readOnly = true)
	public Predeposit getObjByProperty(String construct, String propertyName, String value){
		return this.predepositDao.getBy(construct, propertyName, value);
	}
	
	@Transactional(readOnly = false)
	public boolean delete(Long id) {
		try {
			this.predepositDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@Transactional(readOnly = false)
	public boolean batchDelete(List<Serializable> predepositIds) {
		// TODO Auto-generated method stub
		for (Serializable id : predepositIds) {
			delete((Long) id);
		}
		return true;
	}
	
	@Transactional(readOnly = true)
	public IPageList list(IQueryObject properties) {
		if (properties == null) {
			return null;
		}
		String query = properties.getQuery();
		String construct = properties.getConstruct();
		Map params = properties.getParameters();
		GenericPageList pList = new GenericPageList(Predeposit.class,construct, query,
				params, this.predepositDao);
		if (properties != null) {
			PageObject pageObj = properties.getPageObj();
			if (pageObj != null)
				pList.doList(pageObj.getCurrentPage() == null ? 0 : pageObj
						.getCurrentPage(), pageObj.getPageSize() == null ? 0
						: pageObj.getPageSize());
		} else
			pList.doList(0, -1);
		return pList;
	}
	
	@Transactional(readOnly = false)
	public boolean update(Predeposit predeposit) {
		try {
			this.predepositDao.update( predeposit);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}	
	
	@Transactional(readOnly = true)
	public List<Predeposit> query(String query, Map params, int begin, int max){
		return this.predepositDao.query(query, params, begin, max);
		
	}
	
	public Object predepositPay(HttpServletRequest request, HttpServletResponse response, String mobile, String amount, String channel, String orderType, String returnUrl){
		ModelAndView mv = new ModelAndView();
		try{
			String str = "";
			if("WAP".equals(channel)){
				str = "/wap";
			}
			User rechargeUser = userService.getBuyerOrMainSellerByMobile(mobile);
			User loginUser = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
			Result rechargeResult = this.chargeService.charge(loginUser, amount, rechargeUser.getCustId(), "", CommUtil.getURL(request));
			if(rechargeResult != null && CommUtil.null2String(ErrorEnum.SUCCESS.getIndex()).equals((rechargeResult.getCode()))){
				Map<String, Object> map = (Map<String, Object>) rechargeResult.getData();
				if(map != null){
					String pd_sn = CommUtil.null2String(map.get("value"));
					Result payResult =  this.payCenterService.placeOrder(loginUser, pd_sn, "", channel, orderType, "", returnUrl);
					if(payResult != null && CommUtil.null2String(ErrorEnum.SUCCESS.getIndex()).equals((payResult.getCode()))){
						Map<String, Object> json = (Map<String, Object>) payResult.getData();
						if(json != null){
							String url = CommUtil.null2String(json.get("url"));
							response.getWriter().write("<script>window.location.href='"+url+"'</script>");
							return null;
						}
					}else{
						mv = new JModelAndView(str + "/error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(), 1,
								request, response);
						mv.addObject("op_title", payResult.getMsg());
						mv.addObject("url", CommUtil.getURL(request) + str + "/index.htm");
						return mv;
					}
				}
			}else{
				mv = new JModelAndView("/wap/error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(), 1,
						request, response);
				mv.addObject("op_title", rechargeResult.getMsg());
				mv.addObject("url", CommUtil.getURL(request) + str + "/index.htm");
				return mv;
			}
		}catch(IOException e){
			e.printStackTrace();
		}
		return mv;
	}	
	
}
