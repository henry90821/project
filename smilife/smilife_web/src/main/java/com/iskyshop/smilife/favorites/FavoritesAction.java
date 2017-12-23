package com.iskyshop.smilife.favorites;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.iskyshop.core.annotation.Token;
import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Favorite;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsLog;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.FavoriteQueryObject;
import com.iskyshop.foundation.service.IFavoriteService;
import com.iskyshop.foundation.service.IGoodsLogService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.lucene.LuceneUtil;
import com.iskyshop.lucene.tools.LuceneVoTools;
import com.iskyshop.smilife.common.CommUtils;
import com.iskyshop.smilife.common.Constants;
import com.iskyshop.smilife.enums.ErrorEnum;
import com.iskyshop.view.web.tools.GoodsViewTools;

/**
 * 收藏--11
 * 获取收藏商品列表接口
 * 添加收藏商品接口
 * 删除收藏商品接口
 * 清空收藏商品接口
 * @author pengfukang
 *
 */
@Controller
@RequestMapping("/api/app")
public class FavoritesAction {
	
	private Logger logger=Logger.getLogger(this.getClass());
	
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IFavoriteService favoriteService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private LuceneVoTools luceneVoTools;
	@Autowired
	private GoodsViewTools goodsViewTools;
	@Autowired
	private IGoodsLogService goodsLogService;
	@Autowired
	private IUserService userService;
	
	/**
	 * 获取收藏商品列表接口--1101
	 * @param custId	用户custId
	 * @param targetPage	当前页码, 默认为1
	 * @param pageSize	每页条数,默认8
	 * @return result
	 */
	@RequestMapping(value="/mall1101GetFavoritesList.htm", method=RequestMethod.POST)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)	
	@Token
	public Object mall1101GetFavoritesList(HttpServletRequest request, User user, int targetPage, int pageSize){
		Map<String,Object> result = new HashMap<String,Object>();
		Map<String, Object> entity = new HashMap<String,Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		try {			
			//页码默认为1 每页条数默认为8
			if(targetPage==0){
				targetPage = 1;
			}
			if(pageSize==0){
				pageSize = 8;
			}
			FavoriteQueryObject qo = new FavoriteQueryObject();
			qo.addQuery("obj.type", new SysMap("type", 0), "=");
			qo.addQuery("obj.user_id", new SysMap("user_id", user.getId()), "=");
			qo.setOrderBy("addTime");
			qo.setPageSize(pageSize);
			qo.setCurrentPage(targetPage);
			IPageList pList = this.favoriteService.list(qo);
			
			List<Favorite> resultList = pList.getResult();
			
			for(Favorite favo : resultList){
				Map<String, Object> listMap = new HashMap<String,Object>();

				Goods goods = this.goodsService.getObjByProperty(null, "id", CommUtil.null2Long(favo.getGoods_id()));
				
				//2016-4-27 13:59:39 当商品原价为null 返回空字符串
				if(goods.getGoods_price()==null){
					listMap.put("originalPrice", "");
				}else{
					listMap.put("originalPrice", goods.getGoods_price());
				}
				listMap.put("goodPicUrl", goods.getGoods_main_photo().getPath()+"/"+goods.getGoods_main_photo().getName());
				listMap.put("goodId", favo.getGoods_id());//商品ID	
				listMap.put("favoriteId", favo.getId());//收藏ID	
				listMap.put("goodName", favo.getGoods_name());//商品名称		
				listMap.put("goodPrice", favo.getGoods_current_price());//商品价格	
				listMap.put("favoriteTime", favo.getAddTime());//收藏时间		
				list.add(listMap);
			}
			
			
			
			entity.put("currentPage", pList.getCurrentPage());//当前页码
			entity.put("pageSize", pList.getPageSize());//每页条数
			entity.put("pageCount", pList.getPages());//总页数
			entity.put("list", list);//商品列表
			
			result.put("code", ErrorEnum.SUCCESS.getIndex());//返回结果代码
			result.put("msg", ErrorEnum.SUCCESS.getDescr());//返回结果描述
		} catch (Exception e) {
			result.put("code", ErrorEnum.REQUEST_ERROR.getIndex());//返回结果代码
			result.put("msg", ErrorEnum.REQUEST_ERROR.getDescr());//返回结果描述
		}
		result.put("entity", entity);//返回数据集
		return result;
	}
	/**
	 * 添加收藏商品接口--1102
	 * @param custId	用户ID
	 * @param goodId	商品ID
	 * @return
	 */
	@RequestMapping(value="/mall1102AddFavorites.htm",method=RequestMethod.POST)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@Token
	public Object mall1102AddFavorites(HttpServletRequest request, User user,String goodId){
		

		Map<String,Object> result = new HashMap<String,Object>();
		Map<String, Object> entity = new HashMap<String,Object>();

		try {			
			
			Map params = new HashMap();
			params.put("user_id", CommUtil.null2Long(user.getId()));
			params.put("goods_id", CommUtil.null2Long(goodId));
			List<Favorite> list = this.favoriteService.query(
					"select obj from Favorite obj where obj.user_id=:user_id and obj.goods_id=:goods_id", params, -1, -1);
			if (list.size() == 0) {
				Goods goods = this.goodsService.getObjById(CommUtil.null2Long(goodId));
				Favorite obj = new Favorite();
				obj.setAddTime(new Date());
				obj.setType(0);
				obj.setUser_name(user.getUserName());
				obj.setUser_id(user.getId());
				obj.setGoods_id(goods.getId());
				obj.setGoods_name(goods.getGoods_name());
				obj.setGoods_photo(goods.getGoods_main_photo().getPath() + "/" + goods.getGoods_main_photo().getName());
				obj.setGoods_photo_ext(goods.getGoods_main_photo().getExt());
				obj.setGoods_store_id(goods.getGoods_store() == null ? null : goods.getGoods_store().getId());
				obj.setGoods_type(goods.getGoods_type());
				obj.setGoods_current_price(goods.getGoods_current_price());
				if (this.configService.getSysConfig().isSecond_domain_open()) {
					Store store = this.storeService.getObjById(obj.getStore_id());
					obj.setGoods_store_second_domain(store.getStore_second_domain());
				}
				this.favoriteService.save(obj);
				goods.setGoods_collect(goods.getGoods_collect() + 1);
				this.goodsService.update(goods);
				GoodsLog todayGoodsLog = this.goodsViewTools.getTodayGoodsLog(goods.getId());
				todayGoodsLog.setGoods_collect(todayGoodsLog.getGoods_collect() + 1);
				this.goodsLogService.update(todayGoodsLog);
				// 更新lucene索引
				String goods_lucene_path = CommUtil.getServerRealPathFromSystemProp() + "luence" + File.separator
						+ "goods";
				File file = new File(goods_lucene_path);
				if (!file.exists()) {
					CommUtil.createFolder(goods_lucene_path);
				}
				LuceneUtil lucene = LuceneUtil.instance();
				lucene.setIndex_path(goods_lucene_path);
				lucene.update(CommUtil.null2String(goods.getId()), luceneVoTools.updateGoodsIndex(goods));
			}
			result.put("code", ErrorEnum.SUCCESS.getIndex());//返回结果代码
			result.put("msg", ErrorEnum.SUCCESS.getDescr());//返回结果描述
		} catch (Exception e) {
			result.put("code", ErrorEnum.REQUEST_ERROR.getIndex());//返回结果代码
			result.put("msg", ErrorEnum.REQUEST_ERROR.getDescr());//返回结果描述
		}
		result.put("entity", entity);//返回数据集
		return result;
	}
	
	/**
	 * 删除收藏商品接口--1103
	 * @param custId	用户ID
	 * @param goodId	商品ID
	 * @return
	 */
	@RequestMapping(value="/mall1103DeleteFavorites.htm",method=RequestMethod.POST)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@Token
	public Object mall1103DeleteFavorites(HttpServletRequest request, User user,String goodId){
		Map<String,Object> result = new HashMap<String,Object>();
		Map<String, Object> entity = new HashMap<String,Object>();
		
		try {			
			Map params = new HashMap<String,Object>();
			params.put("user_id", CommUtil.null2Long(user.getId()));
			params.put("goods_id", CommUtil.null2Long(goodId));
			List<Favorite> list = this.favoriteService.query("select obj from Favorite obj where obj.user_id=:user_id and obj.goods_id=:goods_id", params, -1, -1);
			Favorite favorite = null;
			if(list.size()>0){
				favorite = list.get(0);
			}
			
			if (favorite != null && favorite.getUser_id().equals(user.getId())) {
				this.favoriteService.delete(CommUtil.null2Long(favorite.getId()));
			}
			
			result.put("code", ErrorEnum.SUCCESS.getIndex());//返回结果代码
			result.put("msg", ErrorEnum.SUCCESS.getDescr());//返回结果描述	
		} catch (Exception e) {
			result.put("code", ErrorEnum.REQUEST_ERROR.getIndex());//返回结果代码
			result.put("msg", ErrorEnum.REQUEST_ERROR.getDescr());//返回结果描述	
		}
		result.put("entity", entity);//返回数据集
		return result;
	}
	
	
	/**
	 * 判断收藏的商品是否应存在
	 * @version 1.0
	 * @date 2016年4月5日 下午6:20:42
	 * @param request
	 * @param user
	 * @param goodId
	 * @return
	 */
	@RequestMapping(value="/mall1105CheckFavorites.htm",method=RequestMethod.POST)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@Token
	public Object mall1105CheckFavorites(HttpServletRequest request, User user,String goodId){
		int code =Constants.RESPONSECODE_SUCCESS;
		String msg = Constants.RESPONSECODE_SUCCESS_DESC;
		Map<String, Object> entity = new HashMap<String,Object>();

		try {
			Map params = new HashMap();
			params.put("user_id", CommUtil.null2Long(user.getId()));
			params.put("goods_id", CommUtil.null2Long(goodId));
			List<Favorite> list = this.favoriteService.query(
					"select obj from Favorite obj where obj.user_id=:user_id and obj.goods_id=:goods_id", params, -1, -1);
			if(list.size()>0){
				entity.put("exist", true);
			}else{
				entity.put("exist", false);
			}
			
		} catch (Exception e) {
			logger.error(e);
			return CommUtils.buidResult(Constants.RESPONSECODE_ERROR, Constants.RESPONSECODE_ERROR_DESC, null);
		}
		return CommUtils.buidResult(code, msg, entity);
	}
	
	
}
