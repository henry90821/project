package com.iskyshop.smilife.seckill;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iskyshop.foundation.domain.SeckillGoods;
import com.iskyshop.foundation.service.ISeckillGoodsService;

@Service
@Transactional
public class AppSeckillGoodsServiceImpl implements IAppSeckillGoodsService {
	@Autowired
	private ISeckillGoodsService seckillGoodsService;
	
	/**
     * 获取三个有效的秒杀商品
     * @param limit 限制数量
     * @return
     */
	public List<Map> getAppSeckillGoodsList(int limit) {
		//没有分页最多只取20个
		if(limit==0)
			limit=20;
		
		//获取有效的秒杀商品
		Map params=new HashMap<String,Object>();
		params.put("gg_status", 2);
		params.put("weixin_shop_recommend", true);
		params.put("currentDate", new Date());

		List<SeckillGoods> seckillGoodsList=seckillGoodsService.query("select obj from SeckillGoods obj where obj.gg_status=:gg_status "
				+"and obj.endTime >=:currentDate and obj.gg_count>0 "
				+"and weixin_shop_recommend=:weixin_shop_recommend", params, 0, limit);
		
		if(seckillGoodsList==null || seckillGoodsList.size()<=0)
			return null;
		
		List<Map> result=new ArrayList<Map>();
		for(SeckillGoods seckillGoods:seckillGoodsList){
			//秒杀没有关联商品则过滤掉
			if(seckillGoods.getGg_goods()==null)
				continue;
			
			Map<String,Object> map=new HashMap<String,Object>();
			
			//返回剩余时间
			long restTime=(seckillGoods.getEndTime().getTime()-(new Date()).getTime())/1000;
			
			//秒杀图片
			String seckill_img_url="";
			if(seckillGoods.getWx_photo()!=null)
				seckill_img_url=seckillGoods.getWx_photo().getPath()+ "/" + seckillGoods.getWx_photo().getName();
			
			map.put("goods_id", seckillGoods.getGg_goods().getId());
			map.put("goods_price", seckillGoods.getGg_goods().getGoods_price());
			map.put("seckill_img_url", seckill_img_url);
			map.put("seckill_title",seckillGoods.getGg_name());
			map.put("seckill_resttime",restTime);
			map.put("seckill_price",seckillGoods.getGg_price());
			map.put("seckill_content", seckillGoods.getGg_content());
			map.put("seckill_restcount", seckillGoods.getGg_count());
			
			result.add(map);
		}
		
		return result;
	}
}
