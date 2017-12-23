package com.iskyshop.smilife.seckill;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.iskyshop.smilife.common.Result;
import com.iskyshop.smilife.enums.ErrorEnum;


@Controller
@RequestMapping(value="/api/app")
public class SeckillGoodsAction {
	private Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private IAppSeckillGoodsService appSeckillGoodsService;
	
	/**
     * 获取三个有效的秒杀商品
     * @param limit 限制数量
     * @return
     */
	@RequestMapping(value="/mall1390getAppSeckillGoods.htm",produces={"application/json"} )
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public Result mall1390getAppSeckillGoodsList()
	{
		Result result=new Result();
		result.put("code", ErrorEnum.SUCCESS.getIndex());//返回结果代码
		result.put("msg", ErrorEnum.SUCCESS.getDescr());//返回结果描述
		List<Map> entity = null;
		int limit=3;
		
		try {
			entity =  appSeckillGoodsService.getAppSeckillGoodsList(limit);
		} catch (Exception e) {
			logger.error(e);
			result.set(ErrorEnum.SYSTEM_ERROR);
			result.put("msg", "服务器出错了，请稍后重试");
		}
		result.put("entity", entity);
		return result;
	}
	
}
