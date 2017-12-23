package com.iskyshop.smilife.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.nutz.json.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.foundation.domain.Activity;
import com.iskyshop.foundation.domain.ActivityGoods;
import com.iskyshop.foundation.domain.Coupon;
import com.iskyshop.foundation.domain.CouponInfo;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.Subject;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.IActivityService;
import com.iskyshop.foundation.service.ICouponInfoService;
import com.iskyshop.foundation.service.ICouponService;
import com.iskyshop.foundation.service.IGoodsBrandService;
import com.iskyshop.foundation.service.IGoodsClassService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISubjectService;
import com.tydic.framework.util.PropertyUtil;

/**
 * 获取首页专题带3个商品的广告接口
 * @author herendian
 */
@Service
@Transactional
public class AppSubjectServiceImpl implements IAppSubjectService {

	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private ISubjectService subjectService;
	@Autowired
	private IActivityService activityService;
	@Autowired
	private ICouponService couponService;
	@Autowired
	private IGoodsBrandService brandService;
	@Autowired
	private IGoodsClassService classService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private ICouponInfoService couponinfoService;

	/**
     * 获取专题列表信息和商品
     * @method getSubjectList
     * @return List<Map>
     */
	public List<Map> getSubjectList() {
		List<Map> map_list = new ArrayList<Map>();
		
		//获取专题指定的所有数据
		Map allSubjecParams = new HashMap();
		allSubjecParams.put("displayAPP20", 1);
		allSubjecParams.put("ifcType", "H5");
		List<Subject> allSubjecList = this.subjectService.query(
				"select obj from Subject obj where obj.displayAPP20=:displayAPP20 and obj.ifcType=:ifcType",allSubjecParams, -1, -1);
		
		List<Map> goods_list = null;
		String advPicUrl = null;
		Map subjectMap = null;
		Goods goods = null;
		int goodsMaxCount;
		for (Subject subject : allSubjecList) {
			subjectMap = new HashMap();
			goods_list = new ArrayList<Map>();
			//获取所有专题图片
			advPicUrl = subject.getBanner().getPath()+ "/" + subject.getBanner().getName();
			subjectMap.put("id", subject.getId());
			subjectMap.put("seq", "");
			subjectMap.put("title", subject.getTitle());
			subjectMap.put("detailUrl", "");
			subjectMap.put("picUrl", advPicUrl);
			
			if (!StringUtils.isNullOrEmpty(subject.getSubject_detail())) {
				List<Map> subject_detail = (List<Map>) Json.fromJson(subject.getSubject_detail());
				goodsMaxCount  = 0;
				
				for (Map temp_map : subject_detail) {					
					if ("goods".equals(temp_map.get("type").toString()) && !StringUtils.isNullOrEmpty(temp_map.get("goods_ids"))){
						String ids[] = temp_map.get("goods_ids").toString().split(",");
						for(String gId: ids) {
							if(!StringUtils.isNullOrEmpty(gId)) {
								goods = this.goodsService.getObjById(CommUtil.null2Long(gId));
								if(goods != null && goods.getGoods_status() == 0) {
									Map mapGoods = new HashMap();
									mapGoods.put("goodId", goods.getId());
									mapGoods.put("goodName", goods.getGoods_name());
									mapGoods.put("goodPrice", goods.getGoods_current_price());
									if(goods.getGoods_price() != null){
										mapGoods.put("originalPrice",goods.getGoods_price());
									}else{
										mapGoods.put("originalPrice","");
									}
									mapGoods.put("goodPicUrl", goods.getGoods_main_photo().getPath() + "/" + goods.getGoods_main_photo().getName());
									mapGoods.put("goodDetailUrl", "");
									goods_list.add(mapGoods);
									if(++goodsMaxCount == 20) {
										break;
									}
								}								
							}
						}
						if(goodsMaxCount == 20) {
							break;
						}
					}
				}				
			}
			subjectMap.put("list", goods_list);
			
			map_list.add(subjectMap);
		}
		return map_list;
	}

	/**
     * 获取优惠券礼包
     * @param user 用户
     * @param type 礼包类型
     * @param activityId 活动ID
     * @return List<Map<String,Object> 
     */
	public List<Map<String,Object>> getRegister680CouponBag(User user, Long activityId) {
		List<Map<String,Object>> data=new ArrayList<Map<String,Object>>();
		Map params=new HashMap();
		Date currentDate=new Date();
		
		//判断是否是register680注册的新用户
		if(!isRegister680NewUser(user))
			return data;
		
		params.put("activityId", 2);
		Activity activity=activityService.getObjById(activityId);
		//判断活动有效性
		if(!isValidCouponBag(activity))
			return data;
		
		List<ActivityGoods> ags=activity.getAgs();
		
		//没有配置优惠券
		if(ags==null || ags.size()<=0)
			return data;
		
		for(ActivityGoods ag : ags){
			Coupon curCoupon =ag.getCoupon();
			//检查优惠券有效期
			if(ag.getAg_status()==1 && currentDate.before(curCoupon.getCoupon_end_time())){
				Map<String,Object> couponMap=new HashMap<String,Object>();
				couponMap.put("couponId", curCoupon.getId());
				couponMap.put("couponName", curCoupon.getCoupon_name());
				couponMap.put("couponAmount", curCoupon.getCoupon_amount());
				//订单金额
				couponMap.put("couponOrderAmount", curCoupon.getCoupon_order_amount());
				//关联的类型(有品牌，品类) ,一旦关联必然有关联的ids
				couponMap.put("associatedType", curCoupon.getAssociated_type());
				if((curCoupon.getAssociated_type()==1 || curCoupon.getAssociated_type()==2) && (curCoupon.getAssociated_ids()==null || curCoupon.getAssociated_ids().length()<=0)){
					continue;
				}
				
				String associatedNames="";
				//位店铺优惠券
				if(curCoupon.getAssociated_type()==0){
					if(curCoupon.getStore()!=null){
						associatedNames=curCoupon.getStore().getStore_name();
						couponMap.put("storeId",curCoupon.getStore().getId());
						couponMap.put("storeName",associatedNames);
					}else{
						associatedNames="自营平台";
						couponMap.put("storeName",associatedNames);
					}
				}
				//品类名称列表
				if(curCoupon.getAssociated_type()==1)
					associatedNames=classService.getClassNamesStr(curCoupon.getAssociated_ids());
				
				//品牌名称列表
				if(curCoupon.getAssociated_type()==2)
					associatedNames=brandService.getBrandNamesStr(curCoupon.getAssociated_ids());
				
				couponMap.put("associatedNames", associatedNames.toString());
				couponMap.put("couponBeginTime", curCoupon.getCoupon_begin_time());
				couponMap.put("couponEndTime", curCoupon.getCoupon_end_time());
				couponMap.put("couponLimitAmount", curCoupon.getCoupon_order_amount());
				couponMap.put("couponType", curCoupon.getCoupon_type());
				
				data.add(couponMap);
			}
		}
				
		return data;
	}
	
	private boolean isRegister680NewUser(User user)
	{
		//判断是否是register680注册的新用户
		String newUserTimeStr=PropertyUtil.getProperty("registeredValidDays");
		Long newUserTime=CommUtil.null2Long(newUserTimeStr);
		if(newUserTime==-1L)
			return false;
		
		if(user.getAddTime()==null)
			return false;
		
		if(user.getAddTime().getTime()+newUserTime*24*3600*1000 <= (new Date()).getTime())
			return false;
		
		return true;
	}
	
	/**
	 * 判断优惠券礼包活动的时间的合法性 同时判断活动里面是否均包含的是优惠券
	 * @param activity
	 * @return
	 */
	private boolean isValidCouponBag(Activity activity)
	{
		Date currentDate=new Date();

		boolean result=activity!=null && activity.getAcType()==2 && activity.getAc_status()==1 && 
				currentDate.after(activity.getAc_begin_time()) && currentDate.before(activity.getAc_end_time());
		
		if(!result)
			return false;
		//判断是不是都是优惠券礼包
		List<ActivityGoods> ags=activity.getAgs();
			
		//没有配置优惠券
		if(ags==null || ags.size()<=0)
			return false;
		
		for(ActivityGoods ag : ags){
			Coupon curCoupon =ag.getCoupon();
			if(curCoupon==null)
				return false;
		}
		
		return true;
	}
	
	/**
	 * 检查用户是否领取过优惠券
	 * @param user
	 * @param activity
	 * @return
	 */
	private boolean hadReceiveCouponBagActivity(User user,Activity activity)
	{
		boolean result=false;
		List<Activity> userActivities=user.getActivities();
		//检查是否参见过优惠券活动
		if(userActivities!=null && userActivities.size()>0){
			for(Activity act:userActivities){
				if(act.getId().equals(activity.getId())){
					result=true;
					break;
				}
			}			
		}
		
		if(!result)
			return false;
		
		//检查是否领取到了优惠券
		//检查是否从前领取过优惠礼包活动
		Object obj=couponinfoService.query("select obj.coupon.id from CouponInfo obj where obj.user.id='"+user.getId()+"'", null, 0, -1); 
		if(obj==null)
			return false;
		
		List<Long> joinedCouponIds=(List<Long>)obj;
		if(joinedCouponIds.size()<=0)
			return false;
		
		for(ActivityGoods ags: activity.getAgs()){
			if(joinedCouponIds.indexOf(ags.getCoupon().getId())!=-1)
				return true;
		}
		
		return false;
	}
	
	/**
     * 领取优惠礼包，优惠礼包不可重复领取
     * @param user 用户
     * @param type 礼包类型
     * @param activityId 活动ID
     * @return int 1:表示成功 -1:合法的优惠礼包 -2:已经参加优惠礼包 -3：没有足够的优惠券
     */
	@Transactional(readOnly=false)
	public int saveToUserCouponBag(User user, String type, long activityId) throws Exception {
		Activity activity=activityService.getObjById(activityId);
		Date currentDate=new Date();
		
		//是否合法的优惠礼包
		if(!isValidCouponBag(activity))
			return -1;
		
		if(hadReceiveCouponBagActivity(user, activity))
			return -2;
		
		//处理优惠礼包
		if("register680".equals(type)){
			//判断是否是register680注册的新用户
			if(!isRegister680NewUser(user))
				return -4;
			
			List<Coupon> preUpdateCoupon=new ArrayList<Coupon>();//打算发送的优惠券
			List<ActivityGoods> ags=activity.getAgs();
			if(ags==null || ags.size()<=0)
				return -3;
				
			for(ActivityGoods ag: ags){
				if(ag.getAg_status()!=1)//审核通过的优惠券
					continue;
				
				Coupon coupon=ag.getCoupon();
				//优惠券有效期检查
				if(currentDate.after(coupon.getCoupon_end_time()))
					continue;
				
				//优惠券是否已经使用完了(注意：0表示不限制)
				if(coupon.getCoupon_count()>0 && coupon.getCouponinfos().size()>=coupon.getCoupon_count())
					continue;
					
				preUpdateCoupon.add(coupon);
			}
			
			//优惠券已经领完了
			if(preUpdateCoupon.size()<=0)
				return -3;
			
			//只有领取了优惠券才能表示参加了活动
			activity.getUsers().add(user);
			if(!activityService.save(activity))//防止重复领用
				throw new Exception("保存参加新手礼包的用户失败");
			
			//领取优惠券
			for(Coupon coupon:preUpdateCoupon){
				CouponInfo couponInfo=new CouponInfo();
				couponInfo.setCoupon(coupon);
				couponInfo.setCoupon_sn(UUID.randomUUID().toString());
				couponInfo.setUser(user);
				couponInfo.setAddTime(new Date());
				couponinfoService.save(couponInfo);
			}
			
		}else{
			//@todo 后期新类型活动开发
		}
		
		return 1;
	}
}
