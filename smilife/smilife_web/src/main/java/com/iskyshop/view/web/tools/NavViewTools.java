package com.iskyshop.view.web.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.iskyshop.foundation.domain.Navigation;
import com.iskyshop.foundation.service.IActivityService;
import com.iskyshop.foundation.service.IArticleService;
import com.iskyshop.foundation.service.IGoodsClassService;
import com.iskyshop.foundation.service.INavigationService;

/**
 * 
 * <p>
 * Title: NavViewTools.java
 * </p>
 * 
 * <p>
 * Description:前台导航工具类，查询显示对应的导航信息
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2014
 * </p>
 * 
 * <p>
 * Company: 沈阳网之商科技有限公司 www.iskyshop.com
 * </p>
 * 
 * @author erikzhang
 * 
 * @version iskyshop_b2b2c v2.0 2015版
 */
@Component
public class NavViewTools {
	private Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private INavigationService navService;

	/**
	 * 查询页面导航
	 * 
	 * @param position
	 *            导航位置，-1为顶部，0为中间，1为底部
	 * @param count
	 *            导航数目，查询导航数目，-1为查询所有
	 * @return
	 */
	public List<Navigation> queryNav(int location, int count) {
		List<Navigation> navs = new ArrayList<Navigation>();
		Map params = new HashMap();
		params.put("display", true);
		params.put("location", location);
		params.put("type", "sparegoods");
		navs = this.navService
				.query("select obj from Navigation obj where obj.display=:display and obj.location=:location and obj.type!=:type order by obj.sequence asc",
						params, 0, count);
		
		logger.debug(navs);
		return navs;
	}
	
	public static void main(String[] args) {
		NavViewTools tools = new NavViewTools();
		
		List<Navigation> NavList = tools.queryNav(0, -1);
	}
}
