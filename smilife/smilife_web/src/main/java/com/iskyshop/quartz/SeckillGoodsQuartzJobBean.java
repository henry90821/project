package com.iskyshop.quartz;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.SeckillGoods;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.ISeckillGoodsService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.lucene.LuceneUtil;
import com.iskyshop.lucene.tools.LuceneVoTools;

/**
 * 
 * @ClassName: SeckillGoodsQuartzJobBean
 * @Description: 检查秒杀商品是否需要开启、是否已过期
 * @author dengyuqi
 * @date 2015-10-14
 * 
 */
public class SeckillGoodsQuartzJobBean extends QuartzJobBean {
	private Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private ISeckillGoodsService seckillGoodsService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private LuceneVoTools luceneVoTools;
	
	@Override
	protected void executeInternal(JobExecutionContext arg0) throws JobExecutionException {
		logger.debug("秒杀商品时效定时器执行......start");
		
		//开始
		String query="select obj from SeckillGoods obj where obj.beginTime <=:beginTime and obj.gg_status=:gg_status";
		Map<String,Object> params = new HashMap<String, Object>();
		params.put("beginTime", new Date());
		params.put("gg_status", 1);
		List<SeckillGoods> seckillGoodss=seckillGoodsService.query(query,params, -1, -1);
		Goods goods = null;
		for(SeckillGoods seckillGoods : seckillGoodss){
			seckillGoods.setGg_status(2);
			seckillGoodsService.update(seckillGoods);
			
			goods = seckillGoods.getGg_goods();
			goods.setSeckill_buy(2);
			goods.setGoods_current_price(seckillGoods.getGg_price());
			goodsService.update(goods);
			
			// 更新lucene索引
			String goods_lucene_path = CommUtil.getServerRealPathFromSystemProp() + "luence" + File.separator
					+ "goods";
			File file = new File(goods_lucene_path);
			if (!file.exists()) {
				CommUtil.createFolder(goods_lucene_path);
			}
			LuceneUtil lucene = LuceneUtil.instance();
			LuceneUtil.setIndex_path(goods_lucene_path);
			lucene.update(CommUtil.null2String(goods.getId()), luceneVoTools.updateGoodsIndex(goods));
		}
		
		//过期
		query="select obj from SeckillGoods obj where obj.endTime <=:endTime and obj.gg_status=:gg_status";
		params.clear();
		params.put("endTime", new Date());
		params.put("gg_status", 2);
		seckillGoodss=seckillGoodsService.query(query,params, -1, -1);
		for(SeckillGoods seckillGoods : seckillGoodss){
			seckillGoods.setGg_status(3);
			seckillGoodsService.update(seckillGoods);
			
			goods = seckillGoods.getGg_goods();
			goods.setSeckill_buy(0);
			goods.setGoods_inventory(goods.getGoods_inventory()+(seckillGoods.getGg_count()));
			goods.setGoods_current_price(goods.getStore_price());
			goodsService.update(goods);
			
			// 更新lucene索引
			String goods_lucene_path = CommUtil.getServerRealPathFromSystemProp() + "luence" + File.separator
					+ "goods";
			File file = new File(goods_lucene_path);
			if (!file.exists()) {
				CommUtil.createFolder(goods_lucene_path);
			}
			LuceneUtil lucene = LuceneUtil.instance();
			LuceneUtil.setIndex_path(goods_lucene_path);
			lucene.update(CommUtil.null2String(goods.getId()), luceneVoTools.updateGoodsIndex(goods));
		}
		
		logger.debug("秒杀商品时效定时器执行......end");
	}
}
