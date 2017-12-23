package com.iskyshop.foundation.service.impl;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.nutz.json.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iskyshop.core.dao.IGenericDAO;
import com.iskyshop.core.query.GenericPageList;
import com.iskyshop.core.query.PageObject;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;
import com.iskyshop.foundation.domain.Evaluate;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.IEvaluateService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IUserService;
import com.jcraft.jsch.Logger;

@Service
@Transactional
public class EvaluateServiceImpl implements IEvaluateService {
	@Resource(name = "evaluateDAO")
	private IGenericDAO<Evaluate> evaluateDao;
	
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IUserService userService;
	
	@Transactional(readOnly = false)
	public boolean save(Evaluate evaluate) {
		/**
		 * init other field here
		 */
		try {
			this.evaluateDao.save(evaluate);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	@Transactional(readOnly = true)
	public Evaluate getObjById(Long id) {
		Evaluate evaluate = this.evaluateDao.get(id);
		if (evaluate != null) {
			return evaluate;
		}
		return null;
	}
	@Transactional(readOnly = false)
	public boolean delete(Long id) {
		try {
			this.evaluateDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	@Transactional(readOnly = false)
	public boolean batchDelete(List<Serializable> evaluateIds) {
		// TODO Auto-generated method stub
		for (Serializable id : evaluateIds) {
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
		GenericPageList pList = new GenericPageList(Evaluate.class, construct,
				query, params, this.evaluateDao);
		if (properties != null) {
			PageObject pageObj = properties.getPageObj();
			if (pageObj != null)
				pList.doList(
						pageObj.getCurrentPage() == null ? 0 : pageObj
								.getCurrentPage(),
						pageObj.getPageSize() == null ? 0 : pageObj
								.getPageSize());
		} else
			pList.doList(0, -1);
		return pList;
	}
	@Transactional(readOnly = false)
	public boolean update(Evaluate evaluate) {
		try {
			this.evaluateDao.update(evaluate);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	@Transactional(readOnly = true)
	public List<Evaluate> query(String query, Map params, int begin, int max) {
		return this.evaluateDao.query(query, params, begin, max);

	}

	@Override
	@Transactional(readOnly = true)
	public List<Goods> query_goods(String query, Map params, int begin, int max) {
		// TODO Auto-generated method stub
		return this.evaluateDao.query(query, params, begin, max);
	}
	
	@Override
	@Transactional(readOnly=false)
	public boolean saveEvaluateFromXlsxRow(String userName, long goodsId,
			String evaluateMsg, int evaluateType, Date evaluateTime,
			int saleAmount) {
		//检查参数
		if(userName==null || userName.length()<=0 || evaluateMsg==null || evaluateMsg.length()<=0 || evaluateTime==null)
			return false;
		
		try{
			//检查用户是否存在,不存在则创建一个
			Map params=new HashMap<String,Object>();
			params.put("userName",userName);
			User user=null;
			List<User> users = userService.query("select obj from User obj where obj.userName=:userName and obj.userRole='BUYER'",params, -1, -1);
			if(users==null || users.size()<=0){
				user=new User();
				user.setUserName(userName);
				user.setGold(0);
				user.setIntegral(0);
				user.setLoginCount(0);
				user.setMonth_income(0);
				user.setReport(0);
				user.setSex(0);
				user.setStatus(0);
				user.setUser_version(0);
				user.setUserRole("BUYER");
				//默认密码是123qwe
				user.setPassword("46f94c8de14fb36680850768ff1b7f2a");
				if(!userService.save(user))
					return false;
			}
			else
				user=users.get(0);
				
			//检查商品是否存在
			Goods goods=goodsService.getObjById(goodsId);
			if(goods==null)
				return false;
			
			Evaluate evaluate=new Evaluate();
			evaluate.setEvaluate_buyer_val(evaluateType);
			evaluate.setAddTime(evaluateTime);
			evaluate.setEvaluate_goods(goods);
			evaluate.setEvaluate_user(user);
			evaluate.setEvaluate_status(0);
			evaluate.setEvaluate_info(evaluateMsg);
			evaluate.setGoods_num(1);
			evaluate.setEvaluate_type("goods");
			evaluate.setGoods_price(String.valueOf(goods.getGoods_price()));
			//获取规格
			String specName="";
			try{
				List<Map> specs=(List<Map>)Json.fromJson(goods.getGoods_specs_info());
				specName=(String)specs.get(0).get("name");
			}catch(Exception e){
				
			}
			evaluate.setGoods_spec(specName);
			
			if(!this.save(evaluate))
				return false;
			
			//是否修改商品销量
			goods.setGoods_salenum(saleAmount);
			if(saleAmount>=0 && !goodsService.save(goods))
				return false;
			
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
}
