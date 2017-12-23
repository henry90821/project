package com.iskyshop.smilife.adverts;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.iskyshop.smilife.enums.ErrorEnum;
import com.smi.tools.kits.StrKit;

/**
 * 获取轮播图接口， 获取8个导航饼接口， 获取首页7个活动广告接口， 获取活动列表接口， 获取母婴馆三个广告图接口
 * 
 * @author herendian
 * @version 1.0
 * @date 2016年3月18日 下午4:47:40
 */
@Controller
@RequestMapping("/api/app")
public class AppAdvertsViewAction {

	private Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private IAppAdvertsService appAdvertsService;

	/**
	 * 根据广告位获取广告信息
	 * 	   变更处
	 *     1.type为activity 活动广告位，则根据地理位置显示一个最新的专题广告给App
	 *     seckill_info:
	 *     seckill_title:为秒杀标题，
	 *     seckill_resttime:秒杀剩余的毫秒时间(Long),
	 *     seckill_content：秒杀描述
	 *     seckill_price:秒杀价格
	 *     goods_price:商品价格
	 *     seckill_restcount:秒杀剩余商品数量
	 *     goods_title:商品标题
	 * 
	 * @author herendian
	 * @version 1.0
	 * @date 2016年3月18日 下午4:47:51
	 * @param request
	 * @param response
	 * @param type 字符串广告类型
	 * @param cityId 城市名字
	 */
	@RequestMapping(value = "/mall0500GetAdverts.htm", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public Object mall0500GetAdverts(HttpServletRequest request, HttpServletResponse response, String type,String cityName) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("code", ErrorEnum.SUCCESS.getIndex());//返回结果代码
		result.put("msg", ErrorEnum.SUCCESS.getDescr());//返回结果描述
		List<Map<String, Object>> entity = null;
		try {
			if (StrKit.isNotEmpty(type)) {
				entity = (List<Map<String, Object>>) appAdvertsService.mall0500GetAdvertsByPosition(type,cityName);
			}
		} catch (Exception e) {
			logger.error(e);
			result.put("code", ErrorEnum.SYSTEM_ERROR.getIndex());//返回结果代码
			result.put("msg", ErrorEnum.SYSTEM_ERROR.getDescr());//返回结果描述
		}
		// 广告位
		result.put("entity", entity);

		return result;
	}

}
