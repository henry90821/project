package com.iskyshop.view.web.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.nutz.json.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsCase;
import com.iskyshop.foundation.service.IGoodsCaseService;
import com.iskyshop.foundation.service.IGoodsService;

/**
 * 
 * <p>
 * Title: GoodsCaseViewTools.java
 * </p>
 * 
 * <p>
 * Description: 橱窗工具类
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2015
 * </p>
 * 
 * <p>
 * Company: 沈阳网之商科技有限公司 www.iskyshop.com
 * </p>
 * 
 * @author lixiaoyang
 * 
 * @date 2014-11-24
 * 
 * @version iskyshop_b2b2c 2.0
 */
@Component
public class GoodsCaseViewTools {

	private Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private IGoodsCaseService goodscaseService;
	@Autowired
	private IGoodsService goodsService;

	public List queryGoodsCase(String case_id) {
		Map params = new HashMap();
		params.put("case_id", case_id);
		params.put("display", 1);
		List<GoodsCase> list = this.goodscaseService.query(
				"select obj from GoodsCase obj where obj.case_id=:case_id and obj.display=:display order by obj.sequence",
				params, 0, 5);
		return list;
	}

	public List<Goods> queryCaseGoods(String case_content) {
		if (case_content != null && !"".equals(case_content)) {
			List<Object> list = (List<Object>) Json.fromJson(case_content);
			List<Goods> goods_list = new ArrayList<Goods>();
			if(list != null && !list.isEmpty()){
				int index = 0;
				for(Object id : list){
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("id", CommUtil.null2Long(id));
					params.put("goods_status", 0);
//					List<Goods> objs = this.goodsService
//							.query("select new Goods(id,goods_name,goods_current_price,goods_price,goods_main_photo) from Goods obj where obj.id=:id and obj.goods_status=:goods_status", params, -1, -1);
					//防止外层调用对返回值进行update操作，故查询所有字段
					List<Goods> objs = this.goodsService
							.query("select obj from Goods obj where obj.id=:id and obj.goods_status=:goods_status", params, -1, -1);
					if(objs != null && !objs.isEmpty()){
						if(index < 5){
							goods_list.add(objs.get(0));
							index ++;
						}else{
							break;
						}
					}
				}
				return goods_list;
			}
			
			/*List list = (List) Json.fromJson(case_content);
			List<Goods> goods_list = new ArrayList<Goods>();
			if (list.size() > 5) {
				for (Object id : list.subList(0, 5)) {
					Map params = new HashMap();
					params.put("id", CommUtil.null2Long(id));
					List<Goods> objs = this.goodsService
							.query("select new Goods(id,goods_name,goods_current_price,goods_price,goods_main_photo) from Goods obj where obj.id=:id",
									params, 0, 1);
					if (objs.size() > 0) {
						goods_list.add(objs.get(0));
					}
				}
			} else {
				for (Object id : list) {
					Map params = new HashMap();
					params.put("id", CommUtil.null2Long(id));
					List<Goods> objs = this.goodsService
							.query("select new Goods(id,goods_name,goods_current_price,goods_price,goods_main_photo) from Goods obj where obj.id=:id",
									params, 0, 1);
					if (objs.size() > 0) {
						goods_list.add(objs.get(0));
					}
				}
			}
			return goods_list;
*/
		}
		return null;
	}
}
