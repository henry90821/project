package com.iskyshop.foundation.domain.query;

import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.query.QueryObject;

/**
 * 
 * @author dengyuqi
 *
 */
public class SeckillGoodsQueryObject extends QueryObject {
	public SeckillGoodsQueryObject(String construct, String currentPage,
			ModelAndView mv, String orderBy, String orderType) {
		super(construct, currentPage, mv, orderBy, orderType);
	}

	public SeckillGoodsQueryObject() {
		super();
	}

	public SeckillGoodsQueryObject(String currentPage, ModelAndView mv,
			String orderBy, String orderType) {
		super(currentPage, mv, orderBy, orderType);
	}
	
}
