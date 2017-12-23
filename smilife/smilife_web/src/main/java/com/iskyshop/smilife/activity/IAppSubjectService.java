package com.iskyshop.smilife.activity;

import java.util.List;
import java.util.Map;

import com.iskyshop.foundation.domain.User;

/**
 * 获取首页专题带3个商品的广告接口
 * @author herendian
 */
public interface IAppSubjectService {
	
	/**
     * 获取专题列表信息和商品
     * @method getSubjectList
     * @return List<Map>
     */
	public List<Map> getSubjectList();

	/**
	 * 获取优惠券礼包
	 * @param user 用户
	 * @param type 礼包类型
	 * @param activityId 活动ID
	 * @return List<Map<String,Object> 
	 */
	public List<Map<String,Object>> getRegister680CouponBag(User user, Long activityId);
	
	/**
	 * 领取优惠礼包，优惠礼包不可重复领取
	 * @param user 用户
	 * @param type 礼包类型
	 * @param activityId 活动ID
	 * @return int 1:表示成功 -1:不合法的优惠礼包 -2:已经参加优惠礼包 -3：没有足够的优惠券
	 */
	public int saveToUserCouponBag(User user, String type, long activityId) throws Exception;
}
