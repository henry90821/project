package com.iskyshop.smilife.goods;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iskyshop.foundation.domain.Group;
import com.iskyshop.foundation.domain.GroupGoods;
import com.iskyshop.foundation.service.IGroupGoodsService;
import com.iskyshop.foundation.service.IGroupService;

@Service
public class AppGroupGoodsServiceImpl implements IAppGroupGoodsService {
	public static final Logger logger = Logger.getLogger(AppGroupGoodsServiceImpl.class);
	
	@Autowired
	private IGroupService groupService;
	@Autowired
	private IGroupGoodsService groupGoodsService;

	/**
     * 获取指定团购中的商品列表(只返回未开始和正在进行的团购商品，不返回已过期的团购商品)
     * @method getGroupGoodsList
     * @return List<Map>
     */
	public List<Map> getGroupGoodsList(Long group_id,Integer currentPage, Integer pageSize) {
		List<Map> map_list = new ArrayList<Map>();
		Group group = groupService.getObjById(group_id);
		if(group == null) {
			throw new RuntimeException("未找到id=" + group_id + "的团购。");
		}
		Date now = new Date();	
		Map allGroupGoodsParams = new HashMap();
		allGroupGoodsParams.put("group_id", group.getId());
		allGroupGoodsParams.put("now", now);
		allGroupGoodsParams.put("gg_status", 1);
		allGroupGoodsParams.put("goods_status", 0);
		List<GroupGoods> allGroupGoodsList = this.groupGoodsService.query(
				"select obj from GroupGoods obj where obj.group.id=:group_id "
				+ "and obj.endTime>=:now and obj.gg_status=:gg_status"
				+ " and obj.gg_goods.goods_status=:goods_status",allGroupGoodsParams, -1, -1);
		
		for (GroupGoods groupGoods : allGroupGoodsList) {
			Map groupGoodsMap = new HashMap();
			groupGoodsMap.put("goodPicUrl", groupGoods.getGg_img().getPath() +"/"+ groupGoods.getGg_img().getName());
			groupGoodsMap.put("goodDetailUrl", "");
			groupGoodsMap.put("goodName", groupGoods.getGg_goods().getGoods_name());
			groupGoodsMap.put("goodPrice", groupGoods.getGg_price());
			if(groupGoods.getGg_goods().getGoods_price() != null){
				groupGoodsMap.put("originalPrice", groupGoods.getGg_goods().getGoods_price());
			}else{
				groupGoodsMap.put("originalPrice", "");
			}
			groupGoodsMap.put("goodsSalenum", groupGoods.getGg_selled_count());
			map_list.add(groupGoodsMap);
		}
		return map_list;
	}
}
