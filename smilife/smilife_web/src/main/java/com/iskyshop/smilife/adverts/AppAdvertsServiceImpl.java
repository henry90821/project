package com.iskyshop.smilife.adverts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Accessory;
import com.iskyshop.foundation.domain.Advert;
import com.iskyshop.foundation.domain.AdvertPosition;
import com.iskyshop.foundation.service.IAdvertPositionService;
import com.iskyshop.foundation.service.IAreaService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.ISeckillGoodsService;
import com.tydic.framework.util.PropertyUtil;

/**
 * 获取商城广告接口的实现类
 * 
 * @author herendian
 * @version 1.0
 * @date 2016年3月18日 下午4:48:20
 */
@Service
@Transactional
public class AppAdvertsServiceImpl implements IAppAdvertsService {
	public static final Logger logger = Logger.getLogger(AppAdvertsServiceImpl.class);

	@Autowired
	private IAdvertPositionService advertsPositionService;
	
	@Autowired
	private IAreaService areaService;
	
	/**
	 * 根据广告位获取广告信息
	 * 
	 * @param type 字符串广告类型
	 * @param cityName 城市名字
	 * @author herendian
	 * @version 1.0
	 * @date 2016年3月21日 下午5:44:00
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> mall0500GetAdvertsByPosition(String type,String cityName)throws Exception{
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		// 获取首页轮播图的广告位ID
		String scrollerPositionIdStr = "";
		try {
			scrollerPositionIdStr =PropertyUtil.getProperty(type);
					//CommUtils.getProperties("AdvertsPositionType", type);
		} catch (Exception e) {
			logger.error(e);
			throw e;
		}
		try {
			long scrollerPositionIdL = CommUtil.null2Long(scrollerPositionIdStr);
			// 根据广告位ID获取广告位的广告信息
			AdvertPosition advertPosition = advertsPositionService.getObjById(scrollerPositionIdL);
			//@todo 取最新的
			if (null != advertPosition) {
				// 广告
				List<Advert> advs = advertPosition.getAdvs();

				Collections.sort(advs, new Comparator<Advert>() {
					@Override
					public int compare(Advert o1, Advert o2) {
						return o1.getAd_slide_sequence() - o2.getAd_slide_sequence();
					}
				});
				Map<String, Object> map = null;
				
				int i=1;
				//获取城市ID
				Long cityId=-1L;
				if(cityName!=null && cityName.length()>0){
					List cityData=areaService.query("select A.id from Area A where A.level=1 and A.areaName like '%"+cityName+"%'", new HashMap() , -1, -1);
					cityId=CommUtil.null2Long(cityData.get(0).toString());
				}
					
				for (Advert temp_adv : advs) {
					if (temp_adv.getAd_status() == 1 && temp_adv.getAd_begin_time().before(new Date())
							&& temp_adv.getAd_end_time().after(new Date())) {
						map = new HashMap<String, Object>();
						map.put("id", temp_adv.getId());
						map.put("seq", temp_adv.getAd_slide_sequence());
						map.put("title", temp_adv.getAd_title());
						map.put("detailUrl", temp_adv.getAd_url());
						Accessory accessory=temp_adv.getAd_acc();
						if (null!=accessory) {
							map.put("picUrl", accessory.getPath() + "/" + accessory.getName());
						}else {
							map.put("picUrl", "");
						}
						
						//如果是活动类型只返回一个指定城市的活动广告
						if("activity".equals(type) && cityName!=null && cityName.length()>0){
							if(temp_adv.getCity_id()==cityId){
								result.add(map);
								break;
							}
						}
						else
							result.add(map);
						
					}
					
				}
				
			}
		} catch (Exception e) {
		    e.printStackTrace();
			logger.error(e);
			throw e;
		}
		return result;
	}

}
