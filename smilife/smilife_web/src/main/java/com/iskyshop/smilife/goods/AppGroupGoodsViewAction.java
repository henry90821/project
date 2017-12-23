package com.iskyshop.smilife.goods;

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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.iskyshop.smilife.activity.IAppSubjectService;
import com.iskyshop.smilife.common.Result;
import com.iskyshop.smilife.enums.ErrorEnum;

/**
 * 获取团购详情接口
 * @author herendian
 */
@Controller
@RequestMapping(value="/api/app")
public class AppGroupGoodsViewAction {
	
	private Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private IAppGroupGoodsService appGroupGoodsService;
	
	/**
     * 获取指定团购中的商品列表(只返回未开始和正在进行的团购商品，不返回已过期的团购商品)
     * @method getGroupGoodsList
     * @return List<Map>
     */
	@RequestMapping(value="/mall13010GetItemGroup.htm", produces={"application/json"} )
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public Result mall13001GetItemGroup(HttpServletRequest request, HttpServletResponse response,Integer currentPage, Integer pageSize, Long group_id) {
		Result result = new Result();
		Map<String, Object> reusltList=new HashMap<String ,Object>();
		if(currentPage == null || currentPage < 0) {
			currentPage = 1;
		}
		if(pageSize == null || pageSize < 0) {
			pageSize = 5;
		}
		List<Map> entity = null;
		try {
			entity =  appGroupGoodsService.getGroupGoodsList(group_id, currentPage, pageSize);
			reusltList.put("list", entity);
			result.setData(reusltList);
		} catch (Exception e) {
			logger.error(e);
			result.set(ErrorEnum.SYSTEM_ERROR);
			return result;
		}
				
		return result;
	}
	
}
