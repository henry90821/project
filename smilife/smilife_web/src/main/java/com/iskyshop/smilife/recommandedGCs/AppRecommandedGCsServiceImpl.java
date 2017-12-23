package com.iskyshop.smilife.recommandedGCs;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.foundation.domain.Accessory;
import com.iskyshop.foundation.domain.Advert;
import com.iskyshop.foundation.domain.GoodsClass;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.service.IAccessoryService;
import com.iskyshop.foundation.service.IAdvertService;
import com.iskyshop.foundation.service.IGoodsClassService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.tydic.framework.util.PropertyUtil;



@Service
public class AppRecommandedGCsServiceImpl implements IAppRecommandedGCsService {

	private Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	private IAdvertService advertService;
	
	@Autowired
	private IGoodsClassService goodsClassService;
	
	@Autowired
	private ISysConfigService sysConfigService;
	
	@Autowired
	private IAccessoryService accessoryService;
	
	
	/**
     * 获取APP2.0首页“分类”->“品类”页面中的所有数据
     * @return
     */
	public List<Map> getRecommandedGCsDetails() {
		String now = CommUtil.formatLongDate(new Date());
		String hql = "select obj from Advert obj where obj.ad_ap.id=" + PropertyUtil.getProperty("recommandedGoodsClassesAP") + " and obj.ad_status=1 and obj.ad_begin_time <='"
				+ now + "' and obj.ad_end_time >='" + now + "' order by obj.ad_slide_sequence asc";
		List<Advert> ads = advertService.query(hql, null, -1, -1);
		
		if(ads.size() > 0) {
			Set<String> parentGCIds = new HashSet<String>(ads.size());
			for(Advert ad: ads) {
				Long gcId = CommUtil.null2Long(ad.getAd_url().substring(ad.getAd_url().lastIndexOf("_") + 1));
				if(gcId != -1L && gcId > 0L) {
					parentGCIds.add(gcId.toString());
				} else {
					logger.error("推荐品类的广告链接配置错误。广告“" + ad.getAd_title() + "”的链接“" + ad.getAd_url() + "”配置错误");
				}
			}
			if(parentGCIds.size() == 0) {
				logger.error("推荐品类的广告链接配置错误，没有找到一个正确的广告链接");
				return new ArrayList<Map>(0);
			}
			
			
			hql = "select obj from GoodsClass obj where obj.recommend = true and obj.parent.id in (" + StringUtils.collectionToCommaDelimitedString(parentGCIds) + ") and obj.level=1 order by obj.sequence asc";
			List<GoodsClass> gcChildren = goodsClassService.query(hql, null, -1, -1);//为所有分类广告的被推荐子分类
			
			
			Map<Long, List<GoodsClass>> gcP2CMap = new HashMap<Long, List<GoodsClass>>();
			for(GoodsClass gc: gcChildren) {
				List<GoodsClass> children = gcP2CMap.get(gc.getParent().getId());
				if(children == null) {
					children = new ArrayList<GoodsClass>();
					gcP2CMap.put(gc.getParent().getId(), children);
				}
				children.add(gc);				
			}
			
			List<Map> result = new ArrayList<Map>(ads.size());
			SysConfig sysconfig = sysConfigService.getSysConfig();
			for(Advert ad: ads) {
				Map adInfoMap = new HashMap(4);
				adInfoMap.put("detailUrl", ad.getAd_url());
				adInfoMap.put("picUrl", ad.getAd_acc().getPath() + "/" + ad.getAd_acc().getName());
				adInfoMap.put("title", ad.getAd_title());
				
				Long gcId = CommUtil.null2Long(ad.getAd_url().substring(ad.getAd_url().lastIndexOf("_") + 1));
				
				List<GoodsClass> children = gcP2CMap.get(gcId);
				List<Map> gcChildrenInfo = new ArrayList<Map>();
				if(children != null && children.size() > 0) {
					for(GoodsClass gc: children) {
						Map m = new HashMap(3);
						m.put("classId", gc.getId());
						m.put("className", gc.getClassName());
						
						Long id = CommUtil.null2Long(gc.getWx_icon());
						
						if(id == -1L) {
							m.put("classPicUrl", "http://icon not configured!");
						} else {
							Accessory acc = this.accessoryService.getObjById(id);
							m.put("classPicUrl", acc.getPath() + "/" + acc.getName());
						}
						
						gcChildrenInfo.add(m);
					}
				}
				adInfoMap.put("gcChildren", gcChildrenInfo);
				
				result.add(adInfoMap);
			}
			
			return result;
		} else {
			if(logger.isInfoEnabled()) {
				logger.info("未配置推荐品类的广告。");
			}
			return new ArrayList<Map>(0);
		}
	}

}
