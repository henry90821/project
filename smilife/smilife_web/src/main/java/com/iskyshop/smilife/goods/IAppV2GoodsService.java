package com.iskyshop.smilife.goods;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.foundation.domain.Evaluate;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsClass;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.lucene.LuceneResult;
import com.iskyshop.smilife.common.Result;

/**
 * 商城的商品接口
 * 
 * @author hanhua
 * @version 1.0
 * @date 2016-3-19 下午2:18:04
 */
public interface IAppV2GoodsService {


    /**
     * 获取商品分类列表
     * 
     * @author hanhua
     * @method getGoodsClassList
     * @param id
     * @return
     * @date 2016年3月22日 下午3:21:09
     * @return List<GoodsClass>
     */
    public List<GoodsClass> getGoodsClassList(String id);

    /**
     * 获取分类下商品列表
     * 
     * @author hanhua
     * @method getClassGoods
     * @param gc_id
     * @param orderBy
     * @param orderType
     * @return
     * @date 2016年3月22日 下午3:21:01
     * @return List<Goods>
     */
    public List<Goods> getClassGoods(String gc_id, String orderBy, String orderType);

    /**
     * 获滚动鼠标加载分类商品列表
     * 
     * @author hanhua
     * @method getClassGoods
     * @param gc_id
     * @param store_id
     * @param type
     * @param orderBy
     * @param orderType
     * @param beginCount
     * @param count
     * @return
     * @date 2016年3月22日 下午3:20:51
     * @return List<Goods>
     */
    public List<Goods> getClassGoods(String gc_id, String store_id, String type, String orderBy, String orderType, String beginCount, String count);

    /**
     * 搜索接口
     * 
     * @author hanhua
     * @method searchGoods
     * @param keyword
     * @return
     * @date 2016年3月22日 下午3:20:43
     * @return List<LuceneVo>
     */
    public LuceneResult searchGoods(String keyword, String orderType, String type, String currentPage, String pageSize);

    /**
     * 商品评价
     * 
     * @author hanhua
     * @method getGoodsEva
     * @param keyword
     * @return
     * @date 2016年3月22日 下午3:20:27
     * @return List<Evaluate>
     */
    public List<Evaluate> getGoodsEva(String goodsId, String type);
    
    /**
     * 用户领取优惠券
     * @author hanhua
     * @method getGoodsStoreCoupon
     * @param goodsId 商品Id
     * @param couponId 优惠券id
     * @return 
     * @date 2016年3月22日 下午5:07:56
     * @return List<Evaluate>
     */
    public String getGoodsStoreCoupon(User user, String goodsId, String couponId);
    /**
     * 通过影院Id获取影院两公里商品列表
     * @author chuzhisheng
     * @version 1.0
     * @date 2016年3月23日 下午2:05:57
     * @param request
     * @param id
     * @param cinemaId
     * @return
     */
    public Result getCenimaGoods(String cinemaId,String currentPage,String pageSize);
    
    /**
     * 分页查询商品评价列表、成交记录列表接口
     * @param goodsId
     * @param type
     * @param currentPage
     * @param pageSize
     * @return
     */
	public IPageList getGoodsEva(String goodsId, String type, String currentPage, String pageSize);

	/**
	 * 获取商品详情接口
	 * @param token
	 * @param goodsId
	 * @return
	 */
	public Map<String, Object> getGoodsDetails(HttpServletRequest request,User user, String goodsId);

	/**
	 * 分页查询分类下子分类下所有商品
	 * @author tianbotao
	 * @param classId
	 * @param type
	 * @param orderType
	 * @param currentPage
	 * @param pageSize
	 * @return
	 */
	public IPageList getClassGoodsList(String classId, String store_id, String type, String orderBy, String orderType, String currentPage, String pageSize);
	
	/**
	 * 获取商品详情接口
	 * @author tianbotao 
	 */
	public List<Map<String, Object>> getSubjectDetails(String subjectId);

	/**
	 * 客户端商品详细介绍
	 * @author tianbotao 
	 */
	public Map<String, Object> goodsIntroduce(String goodsId);
	
	/**
	 * 根据店铺id查询商品列表
	 * @param classId
	 * @param type
	 * @param orderType
	 * @param currentPage
	 * @param pageSize
	 * @return
	 */
	public IPageList getGoodsForStoreId(String classId, String type, String orderType, String currentPage, String pageSize);
	
	/**
	 * 查看商品规格库存
	 * @author tianbotao
	 * @param id
	 * @param gsp
	 * @return
	 */
	public Result loadGoodsGsp(String id, String gsp, User user);
	
	/**
	 * 获取新上架的商品
	 * @author hanhua
	 * @version 1.0
	 * @date 2016年5月17日 下午2:53:37
	 * @return
	 */
    public IPageList getNewGoods(String currentPage, String pageSize);
}
