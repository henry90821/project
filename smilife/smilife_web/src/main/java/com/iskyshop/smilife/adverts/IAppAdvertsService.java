package com.iskyshop.smilife.adverts;

import java.util.List;
import java.util.Map;

/**
 * 获取商城的广告信息接口
 * @author herendian
 * @version 1.0
 * @date 2016年3月18日 下午4:48:06
 */
public interface IAppAdvertsService {
	/**
	 *根据广告位获取广告信息
	 * 	   变更处
	 *     1.type为activity 活动广告位，则根据地理位置显示一个最新的专题广告给App
	 *     seckill_info
	 *     seckill_title:为秒杀商品名称，
	 *     seckill_resttime:秒杀剩余的毫秒时间(Long),
	 *     seckill_content：秒杀描述
	 *     seckill_price:秒杀价格
	 *     goods_price:商品价格
	 * 
	 * @param type 字符串广告类型
	 * @param cityName 城市名字
	 * @author herendian
	 * @version 1.0
	 * @date 2016年3月21日 下午5:44:00
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> mall0500GetAdvertsByPosition(String type, String cityName)throws Exception;
	
}
