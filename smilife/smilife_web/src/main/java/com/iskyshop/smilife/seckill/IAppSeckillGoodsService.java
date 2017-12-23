package com.iskyshop.smilife.seckill;

import java.util.List;
import java.util.Map;

public interface IAppSeckillGoodsService {

	/**
	 * 获取三个有效的秒杀商品
	 * @param limit 限制数量
	 * @return
	 */
	public List<Map> getAppSeckillGoodsList(int limit);

}
