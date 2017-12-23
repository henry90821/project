package com.iskyshop.smilife.goods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.jasig.cas.client.util.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.alibaba.fastjson.JSON;
import com.iskyshop.core.annotation.Token;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.foundation.domain.Accessory;
import com.iskyshop.foundation.domain.Evaluate;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsClass;
import com.iskyshop.foundation.domain.SeckillGoods;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.IAccessoryService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.lucene.LuceneResult;
import com.iskyshop.lucene.LuceneVo;
import com.iskyshop.smilife.common.Result;
import com.iskyshop.smilife.enums.ErrorEnum;

/**
 * 商品
 * 
 * @author hanhua
 * @version 1.0
 * @date 2016-3-19 下午2:43:42
 */
@Controller
@RequestMapping("/api/app")
public class AppV2GoodsViewAction
{
	private Logger				logger	= Logger.getLogger(this.getClass());
	@Autowired
	private IAppV2GoodsService	appGoodsService;
	@Autowired
	private IGoodsService		goodsService;
	@Autowired
	private IAccessoryService	accessoryService;

	/**
	 * 获取商品分类列表
	 * 
	 * @author hanhua
	 * @version 1.0
	 * @date 2016年3月21日 下午4:08:41
	 * @param request
	 * @param response
	 * @return mall1301GetGoodsClassList.htm
	 */
	@RequestMapping(value = "/mall1301GetGoodsClassList.htm", method =
	{ RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public Result mall1301GetGoodsClassList(HttpServletRequest request, HttpServletResponse response)
	{
		Result result = new Result();
		try
		{
			// 商品分类id
			String classId = request.getParameter("classId");
			// 通过商品分类id查询分类下的列表数据
			List<GoodsClass> goodsClassList = appGoodsService.getGoodsClassList(classId);
			// 判断数据是否为空
			if (!CollectionUtils.isEmpty(goodsClassList))
			{
				// 组装data数据
				List<Map<String, String>> list = new ArrayList<Map<String, String>>();
				Accessory accessory;
				Map<String, String> entity;
				for (GoodsClass goodsClass : goodsClassList)
				{

					entity = new HashMap<String, String>();
					entity.put("classId", String.valueOf(goodsClass.getId()));
					entity.put("className", goodsClass.getClassName());
					entity.put("classTitle", goodsClass.getNtitle());
					entity.put("classPicUrl", goodsClass.getIcon_sys());
					entity.put("classWxIcon", goodsClass.getWx_icon());
					entity.put("classHalfTitle", goodsClass.getHalfTitle());
					accessory = this.accessoryService.getObjById(CommUtil.null2Long(goodsClass.getWx_icon()));
					if (accessory != null)
					{
						entity.put("classWxIconPath", accessory.getPath());
						entity.put("classWxIconName", accessory.getName());
					}
					list.add(entity);
				}
				// 数据集合
				result.setData(list);
			}
		}catch (Exception e){
			result.set(ErrorEnum.SYSTEM_ERROR);
			logger.error(e.getMessage(),e);
		}
		return result;
	}

	/**
	 * 获取分类下商品列表接口
	 * 
	 * @author hanhua
	 * @version 1.0
	 * @date 2016年3月21日 下午4:29:17
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/mall1302GetClassGoods.htm", method =
	{ RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public Result mall1302GetClassGoods(HttpServletRequest request, HttpServletResponse response)
	{
		Result result = new Result();
		try
		{
			// 商品分类id
			String classId = request.getParameter("classId");
			// 检验必填参数项
			if (CommonUtils.isEmpty(classId))
			{
				result.set(ErrorEnum.REQUEST_ERROR);
				return result;
			}
			// 查询产品列表
			List<Goods> goodsList = appGoodsService.getClassGoods(classId, null, null);
			// Goods集合转换成传输格式
			List<Map<String, String>> data = this.exchangeGoodsList(goodsList);
			result.setData(data);
		}
		catch (Exception e)
		{
			result.set(ErrorEnum.SYSTEM_ERROR);
			logger.error(e.getMessage(),e);
		}
		return result;
	}

	/**
	 * 
	 * @author hanhua
	 * @version 1.0
	 * @date 2016年3月21日 下午4:29:17
	 * @param request
	 * @param response
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/mall1303GetClassGoods.htm", method =
	{ RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public Result mall1303GetClassGoods(HttpServletRequest request, HttpServletResponse response)
	{
		// 接口返回对象
		Result result = new Result();
		try
		{
			// 商品分类id
			String classId = request.getParameter("classId");// 商品分类id
			String orderType = request.getParameter("orderType");// 排序类型（如正序或者倒序）
			String type = request.getParameter("type");// 查询类型（如价格、销量、收藏次数）
			if ("1".equals(type))
			{
				type = "store_price";
			}
			else if ("2".equals(type))
			{
				type = "goods_salenum";
			}
			else if ("3".equals(type))
			{
				type = "goods_collect";
			}
			else
			{
				type = "goods_collect";
			}
			// String beginCount = request.getParameter("beginCount");
			String currentPage = request.getParameter("currentPage");
			String pageSize = request.getParameter("pageSize");
			// 检验必填参数项
			if (CommonUtils.isEmpty(classId))
			{
				result.set(ErrorEnum.REQUEST_ERROR);
				return result;
			}
			// 默认查询第一页
			if (StringUtils.isNullOrEmpty(currentPage))
			{
				currentPage = "1";
			}
			// 默认查询十条数据
			if (StringUtils.isNullOrEmpty(pageSize))
			{
				pageSize = "10";
			}
			// String beginCount = null;//第几条开始查询
			// String count = null;//查几条数据
			// beginCount = CommUtil.null2String((CommUtil.null2Int(currentPage)
			// - 1) * CommUtil.null2Int(pageSize));
			// count = pageSize;
			// List<Goods> goodsList = appGoodsService.getClassGoods(classId,
			// null, null, type, orderType, beginCount, count);
			IPageList pList = appGoodsService.getClassGoodsList(classId, null, null, type, orderType, currentPage, pageSize);
			List<Goods> goodsList = pList.getResult();
			// 组装data数据
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("classId", Long.valueOf(classId)); // 商品分类id
			data.put("orderType", orderType); // 排序类型（如正序或者倒序）
			data.put("type", type); // 查询类型（如价格、销量、人气）
			// data.put("beginCount", beginCount); // 从第几条开始加载
			// Goods集合转换成传输格式
			data.put("currentPage", currentPage);
			data.put("pageSize", pageSize);
			data.put("totalCount", pList.getRowCount());
			data.put("pageCount", pList.getPages());
			List<Map<String, String>> list = null;
			if (CommUtil.null2Int(currentPage) <= pList.getPages()){
				list = this.exchangeGoodsList(goodsList);
			} else {
				list = new ArrayList<Map<String, String>>(0);
			}
			data.put("list", list); // 产品列表
			result.setData(data);
		}
		catch (Exception e)
		{
			result.set(ErrorEnum.SYSTEM_ERROR);
			logger.error(e.getMessage(),e);
		}
		return result;
	}

	/**
	 * 
	 * @author hanhua
	 * @method classGoodsAjax
	 * @param request
	 * @param response
	 * @return
	 * @date 2016-3-19 下午2:29:30
	 * @return Result
	 */
	@RequestMapping(value = "/mall1304SearchGoods.htm", method =
	{ RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public Result mall1304SearchGoods(HttpServletRequest request, HttpServletResponse response)
	{
		// 接口返回对象
		Result result = new Result();
		try
		{
			String keyword = request.getParameter("keyword");
			String orderType = request.getParameter("orderType");
			String type = request.getParameter("type");
			// String beginCount = request.getParameter("beginCount");
			String currentPage = request.getParameter("currentPage");
			String pageSize = request.getParameter("pageSize");
			// 检验必填参数项
			if (CommonUtils.isEmpty(keyword))
			{
				result.set(ErrorEnum.REQUEST_ERROR);
				return result;
			}
			if ("1".equals(type))
			{
				type = "store_price";
			}
			else if ("2".equals(type))
			{
				type = "goods_salenum";
			}
			else if ("3".equals(type))
			{
				type = "goods_collect";
			}
			else if ("4".equals(type))
			{
				type = "well_evaluate";
			}
			LuceneResult pList = appGoodsService.searchGoods(keyword, orderType, type, currentPage, pageSize);
			List<LuceneVo> luceneVoList = pList.getVo_list();
			// 组装data数据
			Map<String, Object> data = new HashMap<String, Object>();
			// 组装data数据
			data.put("totalCount", pList.getRows());
			data.put("currentPage", currentPage);
			data.put("pageSize", pageSize);
			// 组装数据
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			Goods goods = null;
			if (!CollectionUtils.isEmpty(luceneVoList))
			{
				Map<String, Object> entity;
				for (LuceneVo luceneVo : luceneVoList)
				{
					goods = this.goodsService.getObjById(CommUtil.null2Long(luceneVo.getVo_id()));
					entity = new HashMap<String, Object>();
					entity.put("goodId", luceneVo.getVo_id());// 商品Id
					entity.put("goodName", StringUtils.isNullOrEmpty(luceneVo.getVo_title()) ? "" : luceneVo.getVo_title().replaceAll("<[/a-zA-Z'=\"\\s]+>", ""));// 商品名称
					entity.put("goodPrice", luceneVo.getVo_store_price());// 商品价格
					entity.put("goodInventory", luceneVo.getVo_goods_inventory());// 商品库存
					entity.put("goodPicUrl", luceneVo.getVo_main_photo_url()); // 商品图片url
					entity.put("originalPrice", luceneVo.getVo_cost_price()); // 商品原价
					entity.put("goodDetailUrl", luceneVo.getVo_url()); // 商品详情url
					entity.put("evaluateCount", luceneVo.getVo_goods_evas()); // 商品评价数量
					entity.put("goodsSalenum", luceneVo.getVo_goods_salenum()); // 商品销售数量
					entity.put("goodsConfig", luceneVo.getVo_goods_config());// 商品编码
					if (goods != null)
					{
						entity.put("activityStatus", CommUtil.null2String(goods.getActivity_status()));// 活动状态
						entity.put("groupBuy", CommUtil.null2String(goods.getGroup_buy()));// 团购状态
						entity.put("combinStatus", CommUtil.null2String(goods.getCombin_status()));// 组合销售商品
						entity.put("enoughStatus", CommUtil.null2String(goods.getOrder_enough_give_status()));// 满就送状态
						entity.put("enoughReduce", CommUtil.null2String(goods.getEnough_reduce()));// 参加满就减
						entity.put("fSaleType", CommUtil.null2String(goods.getF_sale_type()));// 是否为F码销售商品
						entity.put("advanceSaleType", CommUtil.null2String(goods.getAdvance_sale_type()));// 是否为预售商品
						entity.put("seckillBuy", CommUtil.null2String(goods.getSeckill_buy()));// 秒杀状态
					}
					list.add(entity);
				}
				data.put("list", list);
			}
			result.setData(data);
		}
		catch (Exception e)
		{
			result.set(ErrorEnum.SYSTEM_ERROR);
			logger.error(e.getMessage(),e);
		}
		return result;
	}

	/**
	 * 获取商品评价列表、成交记录列表
	 * 
	 * @author hanhua
	 * @method getGoodsEva
	 * @param request
	 * @param response
	 * @return
	 * @date 2016年3月22日 下午5:00:54
	 * @return Result
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/mall1305GoodsEva.htm", method =
	{ RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public Result mall1305GoodsEva(HttpServletRequest request, HttpServletResponse response)
	{
		// 接口返回对象
		Result result = new Result();
		try
		{
			String goodsId = request.getParameter("goodsId"); // 2128
			String type = request.getParameter("type");
			String currentPage = request.getParameter("currentPage");
			String pageSize = request.getParameter("pageSize");
			// 校验必填项
			if (CommonUtils.isEmpty(goodsId))
			{
				result.set(ErrorEnum.REQUEST_ERROR);
				return result;
			}
			// List<Evaluate> evaluateList =
			// appGoodsService.getGoodsEva(goodsId, type);
			IPageList pList = appGoodsService.getGoodsEva(goodsId, type, currentPage, pageSize);
			List<Evaluate> evaluateList = pList.getResult();
			Accessory accessory;
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("currentPage", currentPage);
			data.put("pageSize", pageSize);
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			if (CommUtil.null2Int(currentPage) <= pList.getPages()){
				if (!CollectionUtils.isEmpty(evaluateList))
				{
					Map<String, Object> entity;
					for (Evaluate evaluate : evaluateList)
					{
						entity = new HashMap<String, Object>();
						accessory = evaluate.getEvaluate_goods().getGoods_main_photo();
						if (accessory != null)
						{
							entity.put("accessoryPath", accessory.getPath());// 附件路径
							entity.put("accessoryName", accessory.getName());// 附件名
						}
						entity.put("userName", evaluate.getEvaluate_user().getUserName());// 评价人
						entity.put("addTime", evaluate.getAddTime());// 评价时间
						entity.put("replyStatus", evaluate.getReply_status()); // 评价回复的状态默认为0未回复
																				// 已回复为1
						entity.put("reply", evaluate.getReply()); // 评价回复
						if(evaluate.getGoods_price() != null){
							entity.put("goodsPrice", evaluate.getGoods_price()); // 成交时的价格
						}else{
							entity.put("goodsPrice", "");
						}
						entity.put("goodsSpec", evaluate.getGoods_spec()); // 商品属性值
						entity.put("goodsNum", evaluate.getGoods_num()); // 购买数量
						entity.put("evaluateInfo", evaluate.getEvaluate_info()); // 买家评价信息
						list.add(entity);
					}
					// result.setData(list);
					data.put("list", list);
					data.put("totalCount", pList.getRowCount());
					data.put("pageCount", pList.getPages());
				}
			}
			result.setData(data);
		}
		catch (Exception e)
		{
			result.set(ErrorEnum.SYSTEM_ERROR);
			logger.error(e.getMessage(),e);
		}
		return result;
	}

	/**
	 * 用户领取优惠券
	 * 
	 * @author hanhua
	 * @method getGoodsStoreCoupon
	 * @param request
	 * @param response
	 * @return
	 * @date 2016年3月22日 下午5:18:20
	 * @return Result
	 */
	@RequestMapping(value = "/mall1306GoodsStoreCoupon.htm", method =
	{ RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	@Token
	public Result mall1306GoodsStoreCoupon(HttpServletRequest request, User user, HttpServletResponse response)
	{
		// 接口返回对象
		Result result = new Result();
		// String custId = request.getParameter("token");
		try {
			String goodsId = request.getParameter("goodsId");
			String couponId = request.getParameter("couponId");

			// 判断必填参数
			if (CommonUtils.isEmpty(couponId))
			{
				result.set(ErrorEnum.REQUEST_ERROR);
				return result;
			}
			String couponFlag = appGoodsService.getGoodsStoreCoupon(user, goodsId, couponId);
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("goodsId", goodsId);
			data.put("couponFlag", couponFlag);
			result.setData(data);
		} catch (Exception e) {
			result.set(ErrorEnum.REQUEST_ERROR);
			logger.error(e.getMessage(),e);
		}
		return result;
	}

	/**
	 * Goods集合转换成传输格式
	 * 
	 * @author hanhua
	 * @version 1.0
	 * @date 2016年3月21日 下午5:53:49
	 * @param goodsList
	 * @return
	 */
	private List<Map<String, String>> exchangeGoodsList(List<Goods> goodsList)
	{
		// 判断数据是否为空
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		List<Map<String, String>> mList = new ArrayList<Map<String, String>>();
		if (!CollectionUtils.isEmpty(goodsList))
		{
			Map<String, String> entity;
			Map<String, String> seckillMap;
			Accessory accessory;
			List<SeckillGoods> seckillGoodsList;
			String goodPicPath;// 商品图片路径
			String goodPicName;// 商品图片名
			String goodPicExt;// 商品图片后缀名
			for (Goods goods : goodsList)
			{
				entity = new HashMap<String, String>();
				entity.put("goodId", String.valueOf(goods.getId())); // 商品Id
				entity.put("goodName", goods.getGoods_name()); // 商品名称
				entity.put("goodPrice", String.valueOf(goods.getStore_price())); // 商品当前价格
				entity.put("goodInventory", goods.getGoods_inventory_detail());// 商品库存
				if(goods.getGoods_price() != null){
					entity.put("originalPrice", CommUtil.null2String(goods.getGoods_price()));// 商品原价
				}else{
					entity.put("originalPrice", "");
				}
				entity.put("activityStatus", CommUtil.null2String(goods.getActivity_status()));// 活动状态
				entity.put("groupBuy", CommUtil.null2String(goods.getGroup_buy()));// 团购状态
				entity.put("combinStatus", CommUtil.null2String(goods.getCombin_status()));// 组合销售商品
				entity.put("enoughStatus", CommUtil.null2String(goods.getOrder_enough_give_status()));// 满就送状态
				entity.put("enoughReduce", CommUtil.null2String(goods.getEnough_reduce()));// 参加满就减
				entity.put("fSaleType", CommUtil.null2String(goods.getF_sale_type()));// 是否为F码销售商品
				entity.put("advanceSaleType", CommUtil.null2String(goods.getAdvance_sale_type()));// 是否为预售商品
				entity.put("seckillBuy", CommUtil.null2String(goods.getSeckill_buy()));// 秒杀状态
				entity.put("evaluateCount", CommUtil.null2String(goods.getEvaluate_count()));// 已评价
				entity.put("goodsSalenum", CommUtil.null2String(goods.getGoods_salenum()));// 已购买

				// 获取附近实体
				accessory = goods.getGoods_main_photo();
				if (accessory != null)
				{
					goodPicPath = accessory.getPath();
					goodPicName = accessory.getName();
					goodPicExt = accessory.getExt();
					entity.put("goodPicUrl", goodPicPath + "/" + goodPicName);// 商品图片路径
				}
				else
				{
					goodPicPath = "图片路劲丢失";
					goodPicName = "图片名丢失";
					goodPicExt = "图片后缀名丢失";
				}
				// 获取秒杀实体
				seckillGoodsList = goods.getSeckill_goods();
				if (seckillGoodsList != null)
				{
					for (SeckillGoods seckillGoods : seckillGoodsList)
					{
						seckillMap = new HashMap<String, String>();
						seckillMap.put("ggStatus", CommUtil.null2String(seckillGoods.getGg_status()));// 秒杀状态
						seckillMap.put("ggPrice", CommUtil.null2String(seckillGoods.getGg_price()));// 秒杀价格
						mList.add(seckillMap);
					}
				}
				entity.put("goodPicPath", goodPicPath); // 商品图片url
				entity.put("goodPicName", goodPicName); // 商品图片url
				entity.put("goodPicExt", goodPicExt); // 商品图片url
				entity.put("seckillGoodsList", JSON.toJSONString(mList));
				list.add(entity);
			}
		}
		return list;
	}

	/**
	 * 通过影院Id获取影院两公里商品列表
	 * 
	 * @author chuzhisheng
	 * @version 1.0
	 * @date 2016年3月23日 下午2:05:57
	 * @param request
	 * @param id
	 * @param cinemaId
	 * @return
	 */
	@RequestMapping(value = "/mall1307GetCenimaGoods.htm", method = RequestMethod.POST, produces =
	{ "application/json" })
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public Object mall1307GetCenimaGoods(HttpServletRequest request, String cinemaId, String currentPage, String pageSize)
	{
		return appGoodsService.getCenimaGoods(cinemaId, currentPage, pageSize);
	}

	/**
	 * 获取商品详情接口
	 * 
	 * @author tianbotao
	 */
	@RequestMapping(value = "/mall1308GetGoodsDetails.htm", method =
	{ RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	@Token(userNullable = true)
	public Result mall1308GetGoodsDetails(HttpServletRequest request, User user, HttpServletResponse response)
	{
		Result result = new Result();
		try {
			// 商品分类id
			String goodsId = request.getParameter("goodsId");
			// 检验必填参数项
			if (CommonUtils.isEmpty(goodsId))
			{
				result.set(ErrorEnum.REQUEST_ERROR);
				return result;
			}
			// 接口返回对象
			Map<String, Object> list = appGoodsService.getGoodsDetails(request, user, goodsId);
			result.setData(list);
			
		} catch (Exception e) {
			result.set(ErrorEnum.REQUEST_ERROR);
			logger.error(e.getMessage(),e);
		}
		return result;
	}

	/**
	 * 获取专题详情接口
	 * 
	 * @author tianbotao
	 */
	@RequestMapping(value = "/mall1309GetSubjectDetails.htm", method =
	{ RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public Result mall1309GetSubjectDetails(HttpServletRequest request, HttpServletResponse response)
	{
		Result result = new Result();
		try
		{
			String subjectId = request.getParameter("subjectId");
			// 检验必填参数项
			if (CommonUtils.isEmpty(subjectId))
			{
				result.set(ErrorEnum.REQUEST_ERROR);
				return result;
			}
			List<Map<String, Object>> list = appGoodsService.getSubjectDetails(subjectId);
			result.setData(list);
		}
		catch (Exception e)
		{
			result.set(ErrorEnum.SYSTEM_ERROR);
			logger.error(e.getMessage(),e);
		}
		return result;
	}

	/**
	 * 客户端商品详细介绍
	 * 
	 * @author tianbotao
	 */
	@RequestMapping(value = "/mall1310GoodsIntroduce.htm", method =
	{ RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public Result mall1310GoodsIntroduce(HttpServletRequest request, HttpServletResponse response)
	{
		Result result = new Result();
		try
		{
			String goodsId = request.getParameter("goodsId");
			// 检验必填参数项
			if (CommonUtils.isEmpty(goodsId))
			{
				result.set(ErrorEnum.REQUEST_ERROR);
				return result;
			}
			Map<String, Object> list = appGoodsService.goodsIntroduce(goodsId);
			result.setData(list);
		}
		catch (Exception e)
		{
			result.set(ErrorEnum.SYSTEM_ERROR);
			logger.error(e.getMessage(),e);
		}
		return result;
	}
	
	/**
	 * 根据店铺id查询商品列表
	 * 
	 * @author tianbotao
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/mall1311GetStoreGoods.htm", method =
	{ RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public Result mall1311GetStoreGoods(HttpServletRequest request, HttpServletResponse response){
		// 接口返回对象
		Result result = new Result();
		try{
			// 商品分类id
			String storeId = request.getParameter("storeId");// 店铺id
			String orderType = request.getParameter("orderType");// 排序类型（如正序或者倒序）
			String type = request.getParameter("type");// 查询类型（如价格、销量、收藏次数）
			if ("1".equals(type)){
				type = "store_price";
			}
			else if ("2".equals(type)){
				type = "goods_salenum";
			}
			else if ("3".equals(type)){
				type = "goods_collect";
			}
			else{
				type = "goods_collect";
			}
			if(StringUtils.isNullOrEmpty(orderType) || (!"desc".equals(orderType.toLowerCase()) && !"asc".equals(orderType.toLowerCase()))){
				orderType = "desc";
			}
			String currentPage = request.getParameter("currentPage");
			String pageSize = request.getParameter("pageSize");
			// 默认查询第一页
			if (StringUtils.isNullOrEmpty(currentPage)){
				currentPage = "1";
			}
			// 默认查询十条数据
			if (StringUtils.isNullOrEmpty(pageSize)){
				pageSize = "10";
			}
			IPageList pList = appGoodsService.getGoodsForStoreId(storeId, type, orderType, currentPage, pageSize);
			List<Goods> goodsList = pList.getResult();
			// 组装data数据
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("storeId", storeId); // 商品分类id
			data.put("orderType", orderType); // 排序类型（如正序或者倒序）
			data.put("type", type); // 查询类型（如价格、销量、人气）
			data.put("currentPage", currentPage);
			data.put("pageSize", pageSize);
			data.put("totalCount", pList.getRowCount());
			data.put("pageCount", pList.getPages());
			List<Map<String, String>> list = new ArrayList<Map<String, String>>();
			if (CommUtil.null2Int(currentPage) <= pList.getPages()){
				list = this.exchangeGoodsList(goodsList);
			}
			data.put("list", list); // 产品列表
			result.setData(data);
		}
		catch (Exception e){
			result.set(ErrorEnum.SYSTEM_ERROR);
			logger.error(e.getMessage(),e);
		}
		return result;
	}
	
	/**
	 * 查询商品规格库存
	 * 
	 * @author tianbotao
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/mall1312LoadGoodsGsp.htm", method =
	{ RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	@Token(userNullable = true)
	public Object mall1312LoadGoodsGsp(HttpServletRequest request, HttpServletResponse response, User user){
		String id = request.getParameter("id");
		String gsp = request.getParameter("gsp");
		return appGoodsService.loadGoodsGsp(id, gsp, user);
	}
	
	/**
     * 查询最新上架商品
     * 
     * @author tianbotao
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/mall1313QueryNewGoods.htm", method =
    { RequestMethod.GET, RequestMethod.POST })
    @ResponseBody
    public Object mall13123QueryNewGoods(HttpServletRequest request, HttpServletResponse response, User user){
        Result result = new Result();
        try
        {
            String currentPage = request.getParameter("currentPage");
            String pageSize = request.getParameter("pageSize");
            // 查询产品列表
            IPageList pList = appGoodsService.getNewGoods(currentPage, pageSize);
            List<Goods> goodsList = pList.getResult();
            // 组装data数据
            Map<String, Object> data = new HashMap<String, Object>();
            // Goods集合转换成传输格式
            data.put("currentPage", currentPage);
            data.put("pageSize", pageSize);
            data.put("totalCount", pList.getRowCount());
            data.put("pageCount", pList.getPages());
            List<Map<String, String>> list = new ArrayList<Map<String, String>>();
            if (CommUtil.null2Int(currentPage) <= pList.getPages()){
                list = this.exchangeGoodsList(goodsList);
            }
            data.put("list", list); // 产品列表
            result.setData(data);
        }
        catch (Exception e)
        {
            result.set(ErrorEnum.SYSTEM_ERROR);
            logger.error(e.getMessage(),e);
        }
        return result;
    }
    
}
