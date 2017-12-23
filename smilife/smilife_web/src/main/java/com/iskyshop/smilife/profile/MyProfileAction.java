package com.iskyshop.smilife.profile;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.foundation.domain.Accessory;
import com.iskyshop.foundation.domain.CouponInfo;
import com.iskyshop.foundation.domain.FootPoint;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsCart;
import com.iskyshop.foundation.domain.Message;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.CouponInfoQueryObject;
import com.iskyshop.foundation.domain.query.FootPointQueryObject;
import com.iskyshop.foundation.domain.query.MessageQueryObject;
import com.iskyshop.foundation.domain.virtual.FootPointView;
import com.iskyshop.foundation.service.IAccessoryService;
import com.iskyshop.foundation.service.ICouponInfoService;
import com.iskyshop.foundation.service.IFootPointService;
import com.iskyshop.foundation.service.IGoodsBrandService;
import com.iskyshop.foundation.service.IGoodsCartService;
import com.iskyshop.foundation.service.IGoodsClassService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IMessageService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.manage.buyer.tools.FootPointTools;
import com.iskyshop.manage.ftp.tools.FTPServerTools;
import com.iskyshop.smilife.common.CommUtils;
import com.iskyshop.smilife.common.Result;
import com.iskyshop.smilife.enums.ErrorEnum;

/**
 * @info APP个人中心接口
 * @since V1.0
 * @author 沈阳网之商科技有限公司 www.iskyshop.com hezeng
 */
@Controller
@RequestMapping(value="/api/app")
public class MyProfileAction {
	@Autowired
	private IUserService userService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IGoodsCartService goodscartService;
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private FTPServerTools ftpServerTools;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private IFootPointService footPointService;
	@Autowired
	private FootPointTools footPointTools; 
	@Autowired
	private ICouponInfoService couponInfoService;
	@Autowired
	private IMessageService messageService;
	@Autowired
	private IGoodsClassService classService;
	@Autowired
	private IGoodsBrandService brandService;
	
    /**
	  * APP获取用户头像信息接口
	  * @param token
    */
	@RequestMapping(value="/mall1701MyPortrait.htm", produces={"application/json"} )
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@Token
	public Object MyPortrait(HttpServletRequest request, HttpServletResponse response,User user) {
		String acc = null ;
		String msg = ErrorEnum.SUCCESS.getDescr();
		int code = ErrorEnum.SUCCESS.getIndex();
				Accessory acc1 = user.getPhoto();
				if(acc1 == null){
					code = ErrorEnum.USER_INCOMPLETE.getIndex();
					msg = "用户未上传头像！";
				}else{
					acc = acc1.getPath()+"/"+acc1.getName();
				}
				Map rest = new HashMap();
				rest.put("value", acc);
		return CommUtils.buidResult(code, msg, rest);
	}
	
	/**
	  * APP修改用户头像接口
	  * @param token
    */
	@RequestMapping(value="/mall1702UdPortrait.htm", method=RequestMethod.POST, produces={"application/json"} )
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@Token
	public Result UdPortrait(HttpServletRequest request, User user) {
		Result result = new Result();
		
		String uploadFilePath = this.configService.getSysConfig().getUploadFilePath();
		String filePath = CommUtil.getServerRealPathFromRequest(request) + uploadFilePath + File.separator + "cache";
		String portraitName = user.getId() + "_big";
		Map portraitInfo = null;
		try {
			portraitInfo = CommUtil.savePostFileToServer(request, "file", filePath, portraitName, null);
		} catch (IOException e) {
			result.set(ErrorEnum.SYSTEM_ERROR);
			result.setMsg("保存上传头像到服务器本地失败：" + e.getMessage());
			return result;
		}
		if(StringUtils.isNullOrEmpty(portraitInfo.get("fileName"))) {
			result.set(ErrorEnum.SYSTEM_ERROR);
			result.setMsg("未取到用户上传上来的头像");
			return result;
		} else {
			Accessory photo = null;
			if (user.getPhoto() != null) {
				photo = user.getPhoto();
			} else {
				photo = new Accessory();
				photo.setAddTime(new Date());
				photo.setWidth(132);
				photo.setHeight(132);				
			}
			photo.setName(portraitName + "." + portraitInfo.get("mime"));
			photo.setExt(portraitInfo.get("mime").toString());
			photo.setPath(this.ftpServerTools.userUpload(photo.getName(), "/account", CommUtil.null2String(user.getId())));
			if (user.getPhoto() == null) {
				this.accessoryService.save(photo);
			} else {
				this.accessoryService.update(photo);
			}
			user.setPhoto(photo);
			this.userService.update(user);		
		}
		Map rest = new HashMap();
		rest.put("value", user.getPhoto().getPath()+"/"+user.getPhoto().getName());
		result.setData(rest);
		return result;
	}
	
	/**
	  * APP猜你喜欢接口
	  * @param gcIdU
    */
	@RequestMapping(value="/mall1703MyLikes.htm", produces={"application/json"} )
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public Object MyLikes(HttpServletRequest request, HttpServletResponse response,Long gcId, Integer count, Integer startPos) {
		// 猜您喜欢 根据cookie商品的分类 销量查询 如果没有cookie则按销量查询
	    if(count == null ) {
	        count = 10;
	    }
	    
	    if(startPos == null) {
	        startPos = 0;
	    }
	    
		List<Goods> your_like_goods = new ArrayList<Goods>();
		String msg = ErrorEnum.SUCCESS.getDescr();
		int code = ErrorEnum.SUCCESS.getIndex();
		if (gcId != null && gcId != 0) {
			your_like_goods = this.goodsService
					.query("select obj from Goods obj where obj.goods_status=0 and obj.gc.id = "
							+ gcId
							+ " order by obj.goods_salenum desc", null, startPos, count);
		} else {
			your_like_goods = this.goodsService
					.query("select obj from Goods obj where obj.goods_status=0 order by obj.goods_salenum desc",
							null, startPos, count);
		}
		List<Map> like_goods = new ArrayList<Map>();
		for (Goods good : your_like_goods) {
			Map g = new HashMap();
			Map goodsprice = goodscartService.genericDefaultInfo(good, null);//等定义公共函数
			g.put("goodId", good.getId());
			g.put("goodName", good.getGoods_name());
			g.put("goodPrice", goodsprice.get("price"));
			g.put("goodsSalenum", good.getGoods_salenum());
			if(good.getGoods_price() != null){
				g.put("originalPrice" ,good.getGoods_price());
			}else{
				g.put("originalPrice" ,"");
			}
			g.put("goodPicUrl", good.getGoods_main_photo() == null? "": good.getGoods_main_photo().getPath()+File.separator+good.getGoods_main_photo().getName());
			g.put("goodDetailUrl", "http://www.baidu.com"/*等APP定义商品详情Url*/);
			like_goods.add(g);
		}
		Map res_map = new HashMap();
		res_map.put("list", like_goods);
		return CommUtils.buidResult(code, msg, res_map);
	}
	
	/**
	  * APP我的我的足迹接口
	  * @param token
	  * @param targetPage
	  * @param pageSize
    */
	@RequestMapping(value="/mall1704MyFoots.htm", produces={"application/json"} )
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@Token
	public Object MyFoots(HttpServletRequest request, HttpServletResponse response,User user,int targetPage,int pageSize ) {
		String msg = ErrorEnum.SUCCESS.getDescr();
		int code = ErrorEnum.SUCCESS.getIndex();
		Map result = new HashMap();
		FootPointQueryObject qo = new FootPointQueryObject();
		List<Map> my_foots = new ArrayList<Map>();
		qo.setCurrentPage(CommUtil.null2Int(-1));
		qo.setPageSize(CommUtil.null2Int(-1));
		qo.addQuery("obj.fp_user_id", new SysMap("fp_user_id", user.getId()), "=");
		qo.setOrderBy("addTime");
		qo.setOrderType("desc");
		IPageList pList = this.footPointService.list(qo);
		List<FootPoint> footlist = pList.getResult();
		Map repeatIds = new HashMap();
		for (FootPoint foot : footlist) {
			List<FootPointView> list_foot = this.footPointTools.generic_fpv(foot.getFp_goods_content());
			for (FootPointView footPointView : list_foot) {
					Map foots = new HashMap();
					foots.put("goodId",footPointView.getFpv_goods_id());
					foots.put("goodName", footPointView.getFpv_goods_name());
					foots.put("goodPrice", footPointView.getFpv_goods_price());
					foots.put("goodPicUrl", footPointView.getFpv_goods_img_path());
					foots.put("goodDetailUrl", "http://www.baidu.com");//等APP定义商品详情Url
					foots.put("browsingTime", footPointView.getFpv_goods_time());
					my_foots.add(foots);
			}
		}
		Map[] m1 = my_foots.toArray(new HashMap[my_foots.size()]);
		Map m2 = null;
		for (int i = 0; i < m1.length - 1; i++){
			Date date1,date2;
			for (int j = 0; j < m1.length - 1 - i; j++){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				try {
					date1 = sdf.parse((String)m1[j].get("browsingTime"));
					date2 = sdf.parse((String)m1[j+1].get("browsingTime"));
					if (date1.before(date2)) {
						m2  = m1[j];
						m1[j] = m1[j + 1];
						m1[j + 1] = m2;
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}
		my_foots = Arrays.asList(m1);
		List<Map> forfoots = new ArrayList<Map>();
		
		for(int begin = (targetPage - 1) * pageSize, i = (targetPage - 1) * pageSize;forfoots.size() < pageSize&&i<my_foots.size(); i++){
			if(begin < my_foots.size()){
				if(repeatIds.get(my_foots.get(begin).get("goodId")) == null){
					 forfoots.add(my_foots.get(begin));
					 repeatIds.put(my_foots.get(begin).get("goodId"), my_foots.get(begin).get("goodId"));
					 begin++;
				}
			}
		}
		result.put("totalCount", my_foots.size());
		result.put("currentPage", targetPage);
		result.put("pageSize", pageSize);
		result.put("list", forfoots);
		return CommUtils.buidResult(code, msg, result);
	}
	
	/**
	  * APP获取我的优惠券列表接口
	  * @param token
	  * @param targetPage
	  * @param pageSize
    */
	@RequestMapping(value="/mall1705MyCoupons.htm", produces={"application/json"} )
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@Token
	public Object MyCoupons(HttpServletRequest request, HttpServletResponse response,User user,int targetPage,int pageSize ) {
		String msg = ErrorEnum.SUCCESS.getDescr();
		int code = ErrorEnum.SUCCESS.getIndex();
		Map result_info = new HashMap(); 
		CouponInfoQueryObject qo = new CouponInfoQueryObject();
		qo.setCurrentPage(CommUtil.null2Int(targetPage));
		qo.setPageSize(CommUtil.null2Int(pageSize));
		qo.addQuery("obj.user.id", new SysMap("user_id", user.getId()), "=");
		qo.setOrderBy("addTime");
		qo.setOrderType("desc");
		IPageList pList = this.couponInfoService.list(qo);
		List<Map> my_couponInfo = new ArrayList<Map>();
		List<CouponInfo> coupon = pList.getResult();
		for (CouponInfo couponInfo : coupon) {
			Map info = new HashMap();
			info.put("couponId", couponInfo.getCoupon().getId());
			info.put("couponName", couponInfo.getCoupon().getCoupon_name());
			info.put("couponAmount", couponInfo.getCoupon().getCoupon_amount());
			info.put("couponBeginTime", couponInfo.getCoupon().getCoupon_begin_time());
			info.put("couponEndTime", couponInfo.getCoupon().getCoupon_end_time());
			info.put("couponLimitAmount", couponInfo.getCoupon().getCoupon_order_amount());
			info.put("couponType", couponInfo.getCoupon().getCoupon_type());
			info.put("status", couponInfo.getStatus());
			String associatedNames="";
			//为自营商品时返回固定值 (self)
			//2为全场优惠券 0表示自营  ,全场优惠券的商铺信息是空的
			if(couponInfo.getCoupon().getCoupon_type() == 0){
				info.put("storeId", "self");
				info.put("storeName", "self");
				associatedNames="自营平台";
			}else if(couponInfo.getCoupon().getCoupon_type() == 1 ){
				associatedNames=couponInfo.getCoupon().getStore().getStore_name();
				info.put("storeId", couponInfo.getCoupon().getStore().getId());
				info.put("storeName", associatedNames);
			}else if(couponInfo.getCoupon().getCoupon_type() == 2){
				//品类名称列表
				if(couponInfo.getCoupon().getAssociated_type()==1)
					associatedNames=classService.getClassNamesStr(couponInfo.getCoupon().getAssociated_ids());
				
				//品牌名称列表
				if(couponInfo.getCoupon().getAssociated_type()==2)
					associatedNames=brandService.getBrandNamesStr(couponInfo.getCoupon().getAssociated_ids());
			}
			
			info.put("associatedNames", associatedNames);
			my_couponInfo.add(info);
		}
		result_info.put("totalCount", pList.getRowCount());
		result_info.put("currentPage", targetPage);
		result_info.put("pageSize", pageSize);
		result_info.put("list", my_couponInfo);
		return CommUtils.buidResult(code, msg, result_info);
	}
	
	 /**
	  * APP获取我的系统消息接口
	  * @param token
	  * @param targetPage
	  * @param pageSize
    */
	@RequestMapping(value="/mall1706MyMessage.htm", produces={"application/json"} )
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@Token
	public Object MyMessage(HttpServletRequest request, HttpServletResponse response,User user,int targetPage,int pageSize ) {
		String msg = ErrorEnum.SUCCESS.getDescr();
		int code = ErrorEnum.SUCCESS.getIndex();
		Map result_message = new HashMap(); 
		MessageQueryObject qo = new MessageQueryObject();
		qo.addQuery("obj.toUser.id", new SysMap("user_id",user.getId()), "=");
		qo.addQuery("obj.type", new SysMap("type", 0), "=");
		qo.addQuery("obj.parent.id is null", null);
		qo.setOrderBy("addTime");
		qo.setOrderType("desc");
		qo.setCurrentPage(CommUtil.null2Int(targetPage));
		qo.setPageSize(CommUtil.null2Int(pageSize));
		IPageList pList = this.messageService.list(qo);
		List<Map> my_message = new ArrayList<Map>();
		List<Message> messagef = pList.getResult();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for (Message message : messagef) {
			Map message_info = new HashMap(); 
			message_info.put("id", message.getId());
			message_info.put("status", message.getStatus());
			message_info.put("replyStatus", message.getReply_status());
			message_info.put("title", message.getTitle());
			message_info.put("content", message.getContent());
			message_info.put("addtime", format.format(message.getAddTime()));
			my_message.add(message_info);
		}
		result_message.put("totalCount", pList.getRowCount());
		result_message.put("currentPage", targetPage);
		result_message.put("pageSize", pageSize);
		result_message.put("list", my_message);
		return CommUtils.buidResult(code, msg, result_message);
	}
	
	/**
	  * APP获取我的系统未读消息接口
	  * @param token
	  * @param id
	*/
	@RequestMapping(value="/mall1707UnreadMessage.htm", produces={"application/json"} )
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@Token
	public Object UnreadMessage(HttpServletRequest request, HttpServletResponse response,User user) {
		String msg = ErrorEnum.SUCCESS.getDescr();
		int code = ErrorEnum.SUCCESS.getIndex();
		List<Message> list_message = new ArrayList<Message>();
		Map result_count = new HashMap();
		list_message = this.messageService.query("select obj from Message obj where obj.toUser = "
				+user.getId()+" and obj.status=0", null, -1, -1);
		result_count.put("value", list_message.size());
		return CommUtils.buidResult(code, msg,result_count );
	}
	
	/**
	  * APP修改我的系统消息状态为已读
	  * @param token
	*/
	@RequestMapping(value="/mall1708UdMessage.htm", produces={"application/json"} )
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@Token
	public Object UdMessage(HttpServletRequest request, HttpServletResponse response,User user, Long id) {
		String msg = ErrorEnum.SUCCESS.getDescr();
		int code = ErrorEnum.SUCCESS.getIndex();
		Map couponParams = new HashMap();
		couponParams.put("toUser_id", user.getId());
		List<Message> message = this.messageService.query("select obj from Message obj where obj.status = 0 and obj.toUser.id =:toUser_id", couponParams, -1, -1);
		for (Message message2 : message) {
			message2.setStatus(1);
			this.messageService.update(message2);
		}
		return CommUtils.buidResult(code, msg, "");
	}
	
	/**
	  * APP刷新临时购物车（异步处理）
	  * @param token
	  * @param sessionId
	*/
	@RequestMapping(value="/mall1709UdGoods.htm", produces={"application/json"} )
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@Token(updateUser = true)
	public Object UdGoods(HttpServletRequest request, HttpServletResponse response,User user ,String sessionId) {
		String msg = ErrorEnum.SUCCESS.getDescr();
		int code = ErrorEnum.SUCCESS.getIndex();
		Map couponParams = new HashMap();
		couponParams.put("sessionId", sessionId);
		List<GoodsCart> goodscart= this.goodscartService.query("select obj from GoodsCart obj where obj.cart_session_id=:sessionId", couponParams, -1, -1);
		for (GoodsCart forgoods : goodscart) {
			forgoods.setCart_session_id(null);
			forgoods.setUser(user);
			this.goodscartService.update(forgoods);
		}
		return CommUtils.buidResult(code, msg, "");
	}
	
}
