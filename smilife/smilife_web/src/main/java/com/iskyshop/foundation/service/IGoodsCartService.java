package com.iskyshop.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsCart;

public interface IGoodsCartService {
	/**
	 * 保存一个GoodsCart，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(GoodsCart instance);
	
	/**
	 * 根据一个ID得到GoodsCart
	 * 
	 * @param id
	 * @return
	 */
	GoodsCart getObjById(Long id);
	
	/**
	 * 删除一个GoodsCart
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除GoodsCart
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到GoodsCart
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个GoodsCart
	 * 
	 * @param id
	 *            需要更新的GoodsCart的id
	 * @param dir
	 *            需要更新的GoodsCart
	 */
	boolean update(GoodsCart instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<GoodsCart> query(String query, Map params, int begin, int max);
	
	/**
	 * 删除指定Id的购物车
	 * @param list_ids 要删除的购物车id集
	 * @return
	 */
	public String remove_carts(List<String> list_ids);
	
	/**
	 * 取出商品的所有规格的第一个规格属性的id，这些规格属性的id以逗号拼接然后返回(序号越小的规格的规格属性的id在列表中排得越后)。若商品不是规格库存，则返回空串
	 * 
	 * @param goods
	 *            商品
	 * @return 默认规格id组合，如1,2,
	 */
	public String genericDefaultGsp(Goods goods);
	
	/**
	 * 返回商品的规格库存和价格。若此商品参加了团购或秒杀活动，则返回此商品的团购价或秒杀价及对应库存；若此商品为全局库存商品，则返回全局库存的单价和库存；若此商品为规格库存商品，则返回对应gspIds参数的单价和库存（若gspIds为空或为错误的规格属性id列表，则使用默认的规格属性列表）；若此商品参加了商城活动且当前用户已登录，则会返回此商品折扣后的价格（是前面的价格基础上再折扣的价格））
	 * 
	 * @param goods
	 * @param gsp
	 * @return key=price:商品对应规格的价格（可能是折扣后的价格）；key=count：商品对应规格的库存
	 */
	public Map genericDefaultInfo(Goods goods, String gsp);

	
	/**
	 * 添加商品到购物车
	 * @param request
	 * @param response
	 * @param id 添加到购物车的商品id
	 * @param count 添加到购物车的商品数量
	 * @param gsp 商品的属性值，这里传递id值，如12,1,21
	 * @param suit_gsp 组合套装中各商品的规格值
	 * @param buy_type 购买的商品类型，组合销售时用于判断是套装购买还是配件购买,普通商品：不传值，配件组合:parts,组合套装：suit
	 * @param combin_ids 组合搭配中配件id
	 * @param combin_version 组合套装中套装版本
	 * @return
	 */
	public Map add_goodsCart(HttpServletRequest request, HttpServletResponse response, Long id, Integer count, String gsp, String suit_gsp,
			String buy_type, String combin_ids, Long combin_version) ;



	/**
	 * 设置购物车的规格文字说明，将原有规格的名字替换成自定义的，同时设置购物车与这些规格的关联关系
	 * 
	 * @param goods
	 * @param cart  
	 * @param gspIds
	 */
	public void setGoodsCartSpec(Goods goods, GoodsCart cart, String[] gspIds);
	
	/**
	 * 获得当前请求对应的购物车。若当前用户未登录，则返回cookie中的购物车；若用户已登录，则将cookie购物车合并到用户购物车（不同规格的相同商品不合并）(对于组合套装，只会返回套装中的主体商品对应的购物车)(若当前用户已登录，
	 * 则购物车cookie会被删除)(此函数会刷新购物车中商品的信息)
	 * @param request
	 * @param response
	 * @return
	 */
	public List<GoodsCart> cart_calc(HttpServletRequest request, HttpServletResponse response);


	/**
	 * 刷新PC端和H5端购物车中的价格、套装、规格等信息(此函数中，会将最新信息保存到数据库中)(在商品被删除时，商品对应的购物车也会被删除，所以此函数不必须对此情况进行处理)。不对下架的商品对应的购物车进行删除(要在用户购物车界面进行提示)
	 * @param carts  用户购物车（不包含套装中的非主体商品）
	 */
	public void refreshGoodsCarts(List<GoodsCart> carts);

}
