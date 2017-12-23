package com.iskyshop.view.web.tools;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Activity;
import com.iskyshop.foundation.domain.ActivityGoods;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.service.IActivityGoodsService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.manage.admin.tools.GoodsTools;

@Component
public class ActivityViewTools {
	@Autowired
	private IGoodsService goodspService;
	@Autowired
	private IActivityGoodsService actgoodsService;
	@Autowired
	private IntegralViewTools IntegralViewTools;
	@Autowired
	private GoodsTools goodsTools;


	/**
	 * 活动专题页中，每个商品显示其四个会员等级的价格，(?????直接使用全局价格?????)
	 * 
	 * @param goods_id
	 * @return
	 */
	public Map getActivityPrices(String goods_id) {
		Goods obj = this.goodspService.getObjById(CommUtil.null2Long(goods_id));
		Map map = new HashMap();
		if (obj != null && obj.getActivity_status() == 2) {
			ActivityGoods actGoods = this.actgoodsService.getObjById(obj.getActivity_goods_id());
			if (actGoods != null) {
				Activity act = actGoods.getAct();
				map.put("price1", CommUtil.formatMoney(CommUtil.mul(act.getAc_rebate(), obj.getGoods_current_price())));
				map.put("price2", CommUtil.formatMoney(CommUtil.mul(act.getAc_rebate1(), obj.getGoods_current_price())));
				map.put("price3", CommUtil.formatMoney(CommUtil.mul(act.getAc_rebate2(), obj.getGoods_current_price())));
				map.put("price4", CommUtil.formatMoney(CommUtil.mul(act.getAc_rebate3(), obj.getGoods_current_price())));
			}
		}
		return map;
	}

	/**
	 * 返回活动商品的信息，包括活动商品的价格（折扣后的价格）、活动折扣以及用户的等级
	 * @param goods_id 若此商品当前未参加商城活动，则本函数返回一个空Map
	 * @param user_id 指定返回的活动商品的信息是针对哪个用户的（不同的用户参加商城活动时的折扣率可能不一样）。为null，则表示针对的是当前登录的这个用户。若当前用户未登录，则返回的Map中的key对应的value都为null。
	 * @param gsps  活动商品的规格属性id列表，以逗号分隔。若此参数为null，则若对应商品是规格商品，则取此商品的默认规格属性的id列表
	 * @return 返回的map的内容：key=rate:对应用户的折扣，key=level_name:对应用户的等级（如“银牌会员”），key=rate_price：对应用户的折扣后的价格。若商品当前未参加商城活动，则返回一个空的map对象
	 */
	public Map getActivityGoodsInfo(String goods_id, String user_id, String gsps) {
		Goods obj = this.goodspService.getObjById(CommUtil.null2Long(goods_id));
		Map map = new HashMap();
		if (obj != null && obj.getActivity_status() == 2) {
			Map gm = this.goodsTools.getGoodsPriceAndInventory(obj, gsps, user_id);
			map.put("rate", CommUtil.formatMoney(CommUtil.null2Double(gm.get("rate")) * 10));
			map.put("level_name", gm.get("level_name"));
			map.put("rate_price", gm.get("act_price"));
		}
		return map;
	}	
}
