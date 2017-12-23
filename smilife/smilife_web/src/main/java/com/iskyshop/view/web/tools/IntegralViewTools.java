package com.iskyshop.view.web.tools;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.nutz.json.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserService;

/**
 * 
 * 
 * <p>
 * Title:IntegralViewTools.java
 * </p>
 * 
 * <p>
 * Description:积分商城工具类
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2015
 * </p>
 * 
 * <p>
 * Company: 沈阳网之商科技有限公司 www.iskyshop.com
 * </p>
 * 
 * @author jy
 * 
 * @date 2014年4月19日
 * 
 * @version iskyshop_b2b2c 2.0
 */
@Component
public class IntegralViewTools {
	private Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserService userService;

	/**
	 * 根据会员的id，确定其会员等级
	 * 
	 * @param user_goods_fee
	 * @return 0—铜牌会员1—银牌会员2—金牌会员3—超级会员
	 */
	public int query_user_level(String id) {
		if (this.configService.getSysConfig().getUser_level() != null
				&& !"".equals(this.configService.getSysConfig().getUser_level())) {
			User user = this.userService.getObjById(CommUtil.null2Long(id));
			if (user.getUser_goods_fee() == null) {
				user.setUser_goods_fee(new BigDecimal(0));
				this.userService.update(user);
				return 0;
			}
			int goods_fee = Math.round(CommUtil.null2Float(user.getUser_goods_fee()));
			Map map = Json.fromJson(HashMap.class, this.configService.getSysConfig().getUser_level());
			if (goods_fee >= CommUtil.null2Int(map.get("creditrule6"))) {
				return 3;
			}
			if (goods_fee >= CommUtil.null2Int(map.get("creditrule4"))) {
				return 2;
			}
			if (goods_fee >= CommUtil.null2Int(map.get("creditrule2"))) {
				return 1;
			}
			return 0;
		} else {
			return 0;
		}
	}

	/**
	 * 根据会员的id，返回其会员等级的名称
	 * 
	 * @param user_goods_fee
	 * @return 铜牌会员，银牌会员，金牌会员，超级会员
	 */
	public String query_user_level_name(String id) {
		int user_level = this.query_user_level(id);
		switch (user_level) {
		case 0:
			return "铜牌会员";
		case 1:
			return "银牌会员";
		case 2:
			return "金牌会员";
		case 3:
			return "超级会员";
		}
		return null;
	}

	/**
	 * 根据会员的id，返回其等级对应的勋章，用以商城前台使用
	 * 
	 * @param user_goods_fee
	 * @return
	 */
	public String query_user_level_img(String id) {
		int user_level = this.query_user_level(id);
		switch (user_level) {
		case 0:
			return "userlevel_0.png";
		case 1:
			return "userlevel_1.png";
		case 2:
			return "userlevel_2.png";
		case 3:
			return "userlevel_3.png";
		}
		return null;
	}
}
