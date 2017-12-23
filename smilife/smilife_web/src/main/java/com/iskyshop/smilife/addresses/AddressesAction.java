package com.iskyshop.smilife.addresses;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.iskyshop.core.annotation.Token;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.foundation.domain.Address;
import com.iskyshop.foundation.domain.Area;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.IAddressService;
import com.iskyshop.foundation.service.IAreaService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.smilife.enums.ErrorEnum;

/**
 * 收货地址--03
 * 获取省市区接口
 * 获取收货地址列表接口
 * 获取收货地址信息接口
 * 新增收货地址接口
 * 修改收货地址接口
 * @author pengfukang
 *
 */
@Controller
@RequestMapping("/api/app")
public class AddressesAction {
	
	@Autowired
	private AddressessesService addressessesService;
	@Autowired
	private IAreaService areaService;
	@Autowired
	private IAddressService addressService;
	@Autowired
	private IUserService userService;
	
	/**
	 * 获取省市区接口--0301
	 * @param areaId 区域ID 
	 * @return
	 */
	@RequestMapping(value="/mall0301GetArea.htm",method=RequestMethod.POST)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public Object mall0301GetArea(String areaId){
		Map<String,Object> result = new HashMap<String,Object>();
		List<Map<String, Object>> entity = new ArrayList<Map<String, Object>>();
		
		try {
			List<Area> areas = new ArrayList<Area>();
			if(StringUtils.isNullOrEmpty(areaId)){
				areas = this.areaService.query("select obj from Area obj where obj.parent.id is null", null, -1, -1);
			}else{
				Map params = new HashMap();
				params.put("pid", CommUtil.null2Long(areaId));
				areas = this.areaService.query("select obj from Area obj where obj.parent.id=:pid", params, -1, -1);
			}
			
			for (Area area : areas) {
				Map<String, Object> areaMap = new HashMap<String,Object>();//区域信息
				areaMap.put("areaId", area.getId());//区域ID
				areaMap.put("areaName", area.getAreaName());//区域名称
				entity.add(areaMap);
			}
			
				
			result.put("code", ErrorEnum.SUCCESS.getIndex());//返回结果代码
			result.put("msg", ErrorEnum.SUCCESS.getDescr());//返回结果描述
		} catch (Exception e) {
			result.put("code", ErrorEnum.SYSTEM_ERROR.getIndex());//返回结果代码
			result.put("msg", ErrorEnum.SYSTEM_ERROR.getDescr());//返回结果描述
		}
		
		result.put("entity", entity);//返回数据集
		return result;
	}
	
	/**
	 * 获取收货地址列表接口--0302
	 * @param token 用户ID
	 * @return
	 */
	@RequestMapping(value="/mall0302GetAddressesList.htm",method=RequestMethod.POST)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@Token
	public Object mall0302GetAddressesList(HttpServletRequest request, User userA,int targetPage,int pageSize){
		
		Map<String,Object> result = new HashMap<String,Object>();
		Map<String, Object> entity = new HashMap<String,Object>();
		
		try {
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();//收货地址列表
			//页码默认为1 每页条数默认为8
			if(targetPage==0){
				targetPage = 1;
			}
			if(pageSize==0){
				pageSize = 8;
			}
			
			IPageList pageList = addressessesService.getAddressesList(userA.getId().toString(),targetPage,pageSize);
			List<Address> resultList = pageList.getResult();
			//2016-4-12 17:23:01 请求的页码需小于等于总页数 ，否则返回空集 （韩华） 
			if(pageList.getPages()>=targetPage){
				for (Address ads :resultList) {
					
					Map<String, Object> addressMap = new HashMap<String,Object>();//区域信息
					addressMap.put("addressId", ads.getId());//地址ID
					addressMap.put("trueName", ads.getTrueName());//收货人姓名
					addressMap.put("card", ads.getCard());//身份证
					addressMap.put("phone", ads.getTelephone());//电话号码
					addressMap.put("cellphone", ads.getMobile());//手机号码
					
					Area area = ads.getArea();
					if(area==null)continue;
					int lv = ads.getArea().getLevel();
			
					String provinceName = "";
					String provinceId = "";
					String cityName = "";
					String cityId = "";
					String areaName = "";
					String areaId = "";
					if(lv==2){//三级区域
						provinceName = area.getParent().getParent().getAreaName();
						provinceId = area.getParent().getParent().getId().toString();
						cityName = area.getParent().getAreaName();
						cityId = area.getParent().getId().toString();
						areaName = area.getAreaName();
						areaId = area.getId().toString();
						
					}else if(lv==1){//二级区域
						provinceName = area.getParent().getAreaName();
						provinceId = area.getParent().getId().toString();
						cityName = area.getAreaName();
						cityId = area.getId().toString();
					}//省市区 至少填到 市 
					addressMap.put("provinceName", provinceName);//省份 名称
					addressMap.put("provinceId", provinceId);//省份 ID
					addressMap.put("cityName", cityName);//市 名称
					addressMap.put("cityId", cityId);//市 ID
					addressMap.put("areaName", areaName);//区 名称
					addressMap.put("areaId", areaId);//区 ID	
		
					addressMap.put("detailAddress", ads.getArea_info());//详细地址
					addressMap.put("defaultVal", ads.getDefault_val());//是否为默认收货地址 1为默认
					addressMap.put("postcode", ads.getZip());//邮政编码
					
					list.add(addressMap);
				}
			}
			
			entity.put("totalCount", pageList.getRowCount());//总数据条数
			entity.put("currentPage", pageList.getCurrentPage());//当前页码
			entity.put("pageSize", pageList.getPageSize());//每页条数
			entity.put("pageCount", pageList.getPages());//总页数
			entity.put("list", list);//所有数据
			
			

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
	 * 获取收货地址信息接口--0303
	 * @param adsId 收货地址ID
	 * @return
	 */
	@RequestMapping(value="/mall0303GetAddressesInfo.htm",method=RequestMethod.POST)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public Object mall0303GetAddressesInfo(String adsId){
		Map<String,Object> result = new HashMap<String,Object>();
		Map<String, Object> entity = new HashMap<String,Object>();
		

		try {
			Address ads = this.addressService.getObjById(CommUtil.null2Long(adsId));

			entity.put("addressId", ads.getId());//地址Id
			entity.put("trueName", ads.getTrueName());//收货人姓名
			entity.put("card", ads.getCard());//身份证
			entity.put("phone", ads.getTelephone());//电话号码
			entity.put("cellphone", ads.getMobile());//手机号码
			
			
			Area area = ads.getArea();
			
			String provinceName = "";
			String provinceId = "";
			String cityName = "";
			String cityId = "";
			String areaName = "";
			String areaId = "";
			if(area.getLevel()==2){//三级区域
				provinceName = area.getParent().getParent().getAreaName();
				provinceId = area.getParent().getParent().getId().toString();
				cityName = area.getParent().getAreaName();
				cityId = area.getParent().getId().toString();
				areaName = area.getAreaName();
				areaId = area.getId().toString();
				
			}else if(area.getLevel()==1){//二级区域
				provinceName = area.getParent().getAreaName();
				provinceId = area.getParent().getId().toString();
				cityName = area.getAreaName();
				cityId = area.getId().toString();
			}//省市区 至少填到 市 
			
			entity.put("provinceName", provinceName);//省份 名称
			entity.put("provinceId", provinceId);//省份 ID
			entity.put("cityName", cityName);//市 名称
			entity.put("cityId", cityId);//市 ID
			entity.put("areaName", areaName);//区 名称
			entity.put("areaId", areaId);//区 ID	
			
			entity.put("detailAddress", ads.getArea_info());//详细地址
			entity.put("defaultVal", ads.getDefault_val());//是否为默认收货地址 1为默认
			entity.put("postcode", ads.getZip());//邮政编码
			

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
	 * 新增收货地址接口--0304
	 * @param token	用户ID
	 * @param card	身份证
	 * @param trueName	收货人姓名
	 * @param phone	联系电话
	 * @param cellphone	手机号码
	 * @param areaId	区域ID
	 * @param detailAddress	详细地址
	 * @param postcode	邮编
	 * @return
	 */
	@RequestMapping(value="/mall0304AddAddresses.htm",method=RequestMethod.POST)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@Token
	public Object mall0304AddAddresses(HttpServletRequest request, User user, String card,String trueName,String cellphone,String areaId,String detailAddress,String postcode){
		
		
		Map<String,Object> result = new HashMap<String,Object>();
		Map<String, Object> entity = new HashMap<String,Object>();
		try {
			Area area = this.areaService.getObjById(CommUtil.null2Long(areaId));
			Address address = new Address();	
			IPageList pageList = addressessesService.getAddressesList(user.getId().toString(),1,1);
			//用户没有保存的收货地址时，添加的第一个收货地址保存为默认
			if(pageList.getRowCount()==0){
				address.setDefault_val(1);
			}
			address.setUser(user);
			address.setCard(card);
			address.setArea(area);
			address.setTrueName(trueName);
			address.setMobile(cellphone);
			address.setArea_info(detailAddress);
			address.setZip(postcode);
			address.setAddTime(new Date());
		
		
			this.addressService.save(address);
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
	 * 修改收货地址接口--0305
	 *  @param token	用户ID
	 *  @param adsId	收货地址ID
	 *  @param card		身份证
	 * @param trueName	收货人姓名
	 * @param phone	联系电话
	 * @param cellphone	手机号码
	 * @param areaId	区域ID
	 * @param detailAddress	详细地址
	 * @param postcode	邮编
	 * @param defaultVal	是否为默认收货地址 1为默认
	 * @return
	 */
	@RequestMapping(value="/mall0305UpdateAddresses.htm",method=RequestMethod.POST)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@Token
	public Object mall0305UpdateAddresses(HttpServletRequest request, User userA, String card, String adsId,String trueName,String cellphone,String areaId,String detailAddress,String postcode){
		
		
		Map<String,Object> result = new HashMap<String,Object>();
		Map<String, Object> entity = new HashMap<String,Object>();
		try {
			Address address = new Address();
			if(!StringUtils.isNullOrEmpty(adsId)){
				address = addressService.getObjById(Long.parseLong(adsId));
			}
			address.setUser(userA);
			address.setCard(card);
			Area area = this.areaService.getObjById(CommUtil.null2Long(areaId));
			if(area==null){
				result.put("code", ErrorEnum.REQUEST_ERROR.getIndex());//返回结果代码
				result.put("msg", ErrorEnum.REQUEST_ERROR.getDescr());//返回结果描述
				result.put("entity", entity);//返回数据集
				return result;
			}
			address.setArea(area);
			address.setTrueName(trueName);
			address.setMobile(cellphone);
			address.setArea_info(detailAddress);
			address.setZip(postcode);
			address.setId(Long.parseLong(adsId));
			
			address.setLatitude(null);// 修改地址后要重新计算经纬度
			address.setLongitude(null);
		
		
			this.addressService.update(address);
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
	 * 删除收货地址--0306
	 * @param adsId 收货地址ID
	 * @return
	 */
	@RequestMapping(value="/mall0306DeleteAddressesInfo.htm",method=RequestMethod.POST)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@Token
	public Object mall0306DeleteAddressesInfo(HttpServletRequest request, User userA, String adsId){
		Map<String,Object> result = new HashMap<String,Object>();
		Map<String, Object> entity = new HashMap<String,Object>();
		

		try {
			Address address = this.addressService.getObjById(CommUtil.null2Long(adsId));
			if (address != null && address.getUser().getId().equals(userA.getId())) {
				this.addressService.delete(Long.parseLong(adsId));
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
	 * 设置地址为默认--0307
	 * @param adsId 收货地址ID
	 * @return
	 */
	@RequestMapping(value="/mall0307SetAddressesDefault.htm",method=RequestMethod.POST)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@Token
	public Object mall0307SetAddressesDefault(HttpServletRequest request, User userA, String adsId){
		Map<String,Object> result = new HashMap<String,Object>();
		Map<String, Object> entity = new HashMap<String,Object>();
		

		try {
			Address address = this.addressService.getObjById(Long.parseLong(adsId));
			if (address.getUser().getId().equals(userA.getId())) { // 只允许修改自己的地址信息
				Map params = new HashMap();
				params.put("user_id", userA.getId());
				params.put("id", CommUtil.null2Long(adsId));
				params.put("default_val", 1);
				List<Address> addrs = this.addressService
						.query("select obj from Address obj where obj.user.id=:user_id and obj.id!=:id and obj.default_val=:default_val",
								params, -1, -1);
				for (Address addr1 : addrs) {
					addr1.setDefault_val(0);
					this.addressService.update(addr1);
				}
				address.setDefault_val(1);
				this.addressService.update(address);
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

}
