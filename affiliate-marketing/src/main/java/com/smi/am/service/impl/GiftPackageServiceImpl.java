package com.smi.am.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.smi.am.constant.SmiConstants;
import com.smi.am.dao.AmCounponsDetailsMapper;
import com.smi.am.dao.AmGiftPackageMapper;
import com.smi.am.dao.model.AmCounponsDetails;
import com.smi.am.dao.model.AmGiftPackage;
import com.smi.am.service.IGiftPackageService;
import com.smi.am.service.vo.GiftPackageInfo;
import com.smi.am.service.vo.GiftPackageReqVo;
import com.smi.am.service.vo.GiftPackageResVo;
import com.smi.am.service.vo.GiftPackageVo;
import com.smi.am.service.vo.UserVo;
import com.smi.am.service.vo.WraparoundResVo;
import com.smi.am.utils.DateUtils;
import com.smi.am.utils.MemberCenterConfiguration;
import com.smi.am.utils.SessionManager;
import com.smi.am.utils.SmiResult;
import com.smi.am.utils.ValidatorUtil;
import com.smi.tools.http.HttpKit;
import com.smi.tools.kits.JsonKit;
import com.smilife.core.common.valueobject.BaseValueObject;
import com.smilife.core.common.valueobject.enums.CodeEnum;
import com.smilife.core.exception.SmiBusinessException;
import com.smilife.core.utils.logger.Logger;
import com.smilife.core.utils.logger.LoggerUtils;

/**
 * 礼包服务基类
 * 
 * @author smi
 *
 */
@Service
public class GiftPackageServiceImpl implements IGiftPackageService {

	@Autowired
	private AmGiftPackageMapper amGiftPackageMapper;

	@Autowired
	private AmCounponsDetailsMapper amCounponsDetailsMapper;
	
	@Autowired
	private MemberCenterConfiguration mcConfig;

	private final Logger LOGGER = LoggerUtils.getLogger(this.getClass());

	/**
	 * 根据会员卡号获取礼包信息
	 */
	@Override
	public SmiResult<List<GiftPackageResVo>> selectGiftPackageByCustNbr(Map<String, Object> custNbrMap) {
		List<GiftPackageResVo> listGiftRes = new ArrayList<GiftPackageResVo>();
		SmiResult<List<GiftPackageResVo>> smiResult = new SmiResult<List<GiftPackageResVo>>();
		try {
			// 设置分页
			PageHelper.startPage((Integer) custNbrMap.get("pageNum"), (Integer) custNbrMap.get("pageSize"));
			String custNbr = (String) custNbrMap.get("custNbr");
			List<AmGiftPackage> packageListpackageList = this.amGiftPackageMapper.selectGiftPackageByCustNbr(custNbr);
			PageInfo<AmGiftPackage> page = new PageInfo<AmGiftPackage>(packageListpackageList);
			if (packageListpackageList.size() > 0) {
				for (AmGiftPackage pack : packageListpackageList) {
					GiftPackageResVo gv = new GiftPackageResVo();
					gv.setGiftPackageName(pack.getGpName());
					gv.setPgId(pack.getGpId());
					gv.setStartTime(DateUtils.format(pack.getGpStarttime()));
					gv.setEndTime(DateUtils.format(pack.getGpEndtime()));
					listGiftRes.add(gv);
				}
				smiResult.setData(listGiftRes);
			}
			smiResult.setPages(page.getPages());
			smiResult.setTotal(page.getTotal());
			smiResult.setPageSize(page.getPageSize());
		} catch (Exception e) {
			this.LOGGER.error("根据会员卡号获取礼包信息异常" + e);
			throw new SmiBusinessException("根据会员卡号获取礼包信息异常");
		}
		return smiResult;
	}

	/**
	 * 礼包列表查询
	 */
	@Override
	public SmiResult<List<GiftPackageResVo>> selectGiftPackageList(GiftPackageVo custPackVo) {

		List<GiftPackageResVo> packageList = new ArrayList<GiftPackageResVo>();
		SmiResult<List<GiftPackageResVo>> result = new SmiResult<List<GiftPackageResVo>>();
		try {
			String areaId = custPackVo.getActivityArea();
			if (!ValidatorUtil.isEmptyIgnoreBlank(areaId)) {
				String[] str = custPackVo.getActivityArea().split(",");
				// Collections.sort(str);
				custPackVo.setActivityAreaID(str);
			}
			PageHelper.startPage(custPackVo.getPageNum(), custPackVo.getPageSize());// 执行分页
			List<AmGiftPackage> giftList = this.amGiftPackageMapper.selectGiftPackageByParam(custPackVo);
			if (giftList.size() > 0) {
				PageInfo<AmGiftPackage> pageInfo = new PageInfo<>(giftList); // 取分页值
				for (AmGiftPackage gift : giftList) {
					Integer gpId = gift.getGpId();
					String status=gift.getGpStatus();
					int gpStatus = 0;
					if(!ValidatorUtil.isEmptyIgnoreBlank(status)){
					  gpStatus=Integer.valueOf(status);
					}
					GiftPackageResVo agp = new GiftPackageResVo();
					int remainNum = gift.getGpRemainNum();
					int preSendNum = 0;
					if (!ValidatorUtil.isEmptyIgnoreBlank(gift.getGpSendnum())) {
						preSendNum = Integer.valueOf(gift.getGpSendnum());
					}
					if(SmiConstants.HANDEDOUT.equals(gpStatus)){
					 agp.setSendNum(preSendNum - remainNum);
					}
					agp.setPreSendNum(preSendNum);
					agp.setPgId(gpId);
					agp.setPutChannel(gift.getGpChannel());
					agp.setGpActivityarea(gift.getGpActivityarea());
					agp.setStartTime(DateUtils.format(gift.getGpStarttime()));
					agp.setEndTime(DateUtils.format(gift.getGpEndtime()));
					agp.setStatus(gift.getGpStatus());
					agp.setGiftPackageName(gift.getGpName());
					packageList.add(agp);
				}
				result.setPageSize(pageInfo.getPageSize());
				result.setTotal(pageInfo.getTotal());
				result.setPages(pageInfo.getPages());
				result.setData(packageList);
				result.setCode(CodeEnum.SUCCESS);
			} else {
				result.setCode(CodeEnum.REQUEST_ERROR);
				result.setMsg("获取礼包列表查无数据");
			}
		} catch (Exception e) {
			result.setMsg("获取礼包列表异常");
			this.LOGGER.error("获取礼包列表异常" + e);
			throw new SmiBusinessException("获取礼包列表异常");
		}
		return result;
	}

	/**
	 * 编辑礼包
	 */
	@Transactional
	@Override
	public BaseValueObject updateGiftPackage(GiftPackageReqVo custPackVo, HttpServletRequest requset) {
		int num = 0;
		BaseValueObject smiResult = new BaseValueObject();
		try {
			Integer cdGitpackageid = custPackVo.getPgId();
			if (cdGitpackageid != null) {
				AmGiftPackage amGiftPackage = this.amGiftPackageMapper.selectByPrimaryKey(cdGitpackageid);
				if (amGiftPackage != null) {
					Integer giftStatus = Integer.valueOf(amGiftPackage.getGpStatus());
					if (SmiConstants.UNCOMMITTED.equals(giftStatus) || SmiConstants.FAILED.equals(giftStatus)) {
						UserVo userVo = (UserVo) SessionManager.getUserInfo(requset);
//						Map<String, Object> paramMap = new HashMap<String, Object>();
						Map<String, Object> custMap = new HashMap<String, Object>();
						String coup = custPackVo.getCouponsList();
						String providerUser = custPackVo.getCustNbrList();
						String preNum = custPackVo.getPreSendNum();
						int preSendNum = 0;
						if (ValidatorUtil.isNotEmptyIgnoreBlank(preNum)) {
							preSendNum = Integer.valueOf(preNum);
						}
						String[] couList = coup.split(",");
						String[] custList = providerUser.split(",");
						custMap.put("loginMobiles", providerUser);
						// 调会员中心接口,根据前端的手机账号获取对应的custId
						String jsonstr = HttpKit.post(mcConfig.getCustUrl() + "/cust/getCustIdByLoginMobiles",
								custMap);
						@SuppressWarnings("unchecked")
						SmiResult<List<Map<String, Object>>> smiResults = (SmiResult<List<Map<String, Object>>>) JsonKit
								.parseObject(jsonstr, SmiResult.class);
						if (smiResults.getCode() != 1) {
							throw new SmiBusinessException("调会员中心接口查询会员标识异常");
						}
						List<Map<String, Object>> custData = smiResults.getData();
//						paramMap.put("cdGitpackageid", cdGitpackageid);
						List<AmCounponsDetails> amList = new ArrayList<AmCounponsDetails>();
						//遍历会员绑定优惠券对象批量更新
						for (Map<String, Object> data : custData) {
							for (String cCouponsid : couList) {
								AmCounponsDetails coupons = new AmCounponsDetails();
								coupons.setCdCustid((String) data.get("CUST_ID"));
								coupons.setCdLoginmobile((String) data.get("LOGIN_USER"));
								coupons.setCdGitpackageid(amGiftPackage.getGpId());
								coupons.setCdCounponsid(cCouponsid);
								amList.add(coupons);
							}
						}
						//遍历没有会员绑定优惠券的情况，剩余优惠券批量更新
						for (int i = 0; i < preSendNum - custData.size(); i++) {
							for (String cCouponsid : couList) {
								AmCounponsDetails coupons = new AmCounponsDetails();
								coupons.setCdGitpackageid(amGiftPackage.getGpId());
								coupons.setCdCounponsid(cCouponsid);
								amList.add(coupons);
							}
						}
						this.amCounponsDetailsMapper.batchUpdateCouponsIdByPackageId(amList);
						// 根据礼包id更新优惠详情表
						AmGiftPackage pack = new AmGiftPackage();
						pack.setGpId(custPackVo.getPgId());
						pack.setGpName(custPackVo.getGiftPackageName());
						pack.setGpActivityshop(custPackVo.getActivityShop());
						pack.setGpActivityarea(custPackVo.getActivityArea());
						int GpDeliveringWay = custPackVo.getGpDeliveringWay();
						byte[] pUser = pack.listToByte(custList);
						pack.setGpProvideuser(pUser);
						if (SmiConstants.USER_RECEIVE.equals(GpDeliveringWay)) {
							pack.setGpChannel(custPackVo.getChannel());
							pack.setGpRemainNum(Integer.valueOf(custPackVo.getPreSendNum()));
						}
						pack.setGpDeliveringWay(GpDeliveringWay);
						pack.setGpStarttime(DateUtils.getDate(custPackVo.getStartTime()));
						pack.setGpEndtime(DateUtils.getDate(custPackVo.getEndTime()));
						pack.setGpLastmoduser(userVo.getuName());
						pack.setGpLastmoddate(DateUtils.now());

						pack.setGpStatus(custPackVo.getStatus());
						// 更新礼包表的属性
						num = this.amGiftPackageMapper.updateByPrimaryKey(pack);
						if (num > 0) {
							smiResult.setCode(CodeEnum.SUCCESS);
							smiResult.setMsg("编辑礼包成功");
						} else {
							smiResult.setCode(CodeEnum.REQUEST_ERROR);
							smiResult.setMsg("编辑礼包失败");
						}
					} else {
						smiResult.setCode(CodeEnum.PROFILE_MISSING);
						smiResult.setMsg("编辑该礼包是不可编辑状态");
					}
				} else {
					smiResult.setCode(CodeEnum.PROFILE_MISSING);
					smiResult.setMsg("编辑该礼包不存在");
				}
			}
		} catch (Exception e) {
			this.LOGGER.error("礼包编辑异常" + e);
			throw new SmiBusinessException("礼包编辑异常");
		}
		return smiResult;
	}

	/**
	 * 新增礼包
	 */
	@Transactional
	@Override
	public int addGiftPackage(GiftPackageVo giftPackageVo, HttpServletRequest requset) {
		int num = 0;
		int updateNum = 0;
		try {
			UserVo user = (UserVo) SessionManager.getUserInfo(requset);
			AmGiftPackage amGiftPackage = new AmGiftPackage();
			amGiftPackage.setGpName(giftPackageVo.getGiftPackageName());
			amGiftPackage.setGpActivityarea(giftPackageVo.getActivityArea());
			amGiftPackage.setGpActivityshop(giftPackageVo.getActivityShop());
			amGiftPackage.setGpStarttime(DateUtils.getDate(giftPackageVo.getStartTime()));
			amGiftPackage.setGpEndtime(DateUtils.getDate(giftPackageVo.getEndTime()));
			amGiftPackage.setGpCreatedate(DateUtils.now());
			amGiftPackage.setGpLastmoddate(DateUtils.now());
			amGiftPackage.setGpStatus(giftPackageVo.getStatus());
			String preNum = giftPackageVo.getPreSendNum();
			int preSendNum = 0;
			if (ValidatorUtil.isNotEmptyIgnoreBlank(preNum)) {
				preSendNum = Integer.valueOf(preNum);
			}
			amGiftPackage.setGpSendnum(preNum);
			String custNbr = giftPackageVo.getCustNbrList();
			String[] custNbrList = custNbr.split(",");
			byte[] pUser = amGiftPackage.listToByte(custNbrList);
			amGiftPackage.setGpProvideuser(pUser);
			amGiftPackage.setGpCreateuser(user.getuName());
			amGiftPackage.setGpLastmoduser(user.getuName());
			Integer gpDeliveringWay = giftPackageVo.getGpDeliveringWay();
			amGiftPackage.setGpDeliveringWay(gpDeliveringWay);
			if (SmiConstants.USER_RECEIVE.equals(gpDeliveringWay)) {
				amGiftPackage.setGpChannel(giftPackageVo.getChannel());
				amGiftPackage.setGpRemainNum(Integer.valueOf(giftPackageVo.getPreSendNum()));
			} else {
				amGiftPackage.setGpRemainNum(0);
			}
			this.LOGGER.info("礼包新增开始。。。。");
			num = this.amGiftPackageMapper.insertAmGiftPackage(amGiftPackage);
//			Map<String, Object> paramMap = new HashMap<String, Object>();
			Map<String, Object> custMap = new HashMap<String, Object>();
			String coup = giftPackageVo.getCouponsList();
			String providerUser = giftPackageVo.getCustNbrList();
			custMap.put("loginMobiles", providerUser);
			// 调会员中心接口,根据前端的手机账号获取对应的custId
			String jsonstr = HttpKit.post(mcConfig.getCustUrl() + "/cust/getCustIdByLoginMobiles", custMap);
			@SuppressWarnings("unchecked")
			SmiResult<List<Map<String, Object>>> smiResult = (SmiResult<List<Map<String, Object>>>) JsonKit
					.parseObject(jsonstr, SmiResult.class);
			if (smiResult.getCode() != 1) {
				throw new SmiBusinessException("调会员中心接口查询会员标识异常");
			}
			List<Map<String, Object>> custData = smiResult.getData();
			String[] couList = coup.split(",");
			// paramMap.put("cdGitpackageid", amGiftPackage.getGpId());
			List<AmCounponsDetails> amList = new ArrayList<AmCounponsDetails>();
			//遍历会员绑定优惠券对象批量更新
			for (Map<String, Object> data : custData) {
				for (String cCouponsid : couList) {
					AmCounponsDetails coupons = new AmCounponsDetails();
					coupons.setCdCustid((String) data.get("CUST_ID"));
					coupons.setCdLoginmobile((String) data.get("LOGIN_USER"));
					coupons.setCdGitpackageid(amGiftPackage.getGpId());
					coupons.setCdCounponsid(cCouponsid);
					amList.add(coupons);
				}
			}
			//遍历没有会员绑定优惠券的情况，剩余优惠券批量更新
			for (int i = 0; i < preSendNum - custData.size(); i++) {
				for (String cCouponsid : couList) {
					AmCounponsDetails coupons = new AmCounponsDetails();
					coupons.setCdGitpackageid(amGiftPackage.getGpId());
					coupons.setCdCounponsid(cCouponsid);
					amList.add(coupons);
				}
			}
			updateNum = this.amCounponsDetailsMapper.batchUpdatePackageIdByCouponsIds(amList);
			// 更新优惠券详情表的信息
			// for (String cCouponsid : couList) {
			// this.LOGGER.info("更新优惠券详情表的优惠券id" + cCouponsid);
			// paramMap.put("cCouponsid", cCouponsid);
			// for (Map<String, Object> data: custData) {
			// paramMap.put("cdCustid", data.get("CUST_ID"));
			// paramMap.put("cdLoginMobile", data.get("LOGIN_USER"));
			// updateNum =
			// this.amCounponsDetailsMapper.batchUpdatePackageIdByCouponsId(paramMap);
			// updateNum++;
			// }
			// }
			this.LOGGER.info("礼包新增条数" + num + ",更新优惠券详情表的优惠券id" + updateNum);
		} catch (

		Exception e) {
			this.LOGGER.error("礼包新增异常" + e);
			throw new SmiBusinessException("礼包新增异常");
		}

		return num;
	}

	/**
	 * 礼包的逻辑删除
	 */
	@Override
	@Transactional
	public BaseValueObject deleteLogicGiftPackage(Map<String, Object> paramMap, HttpServletRequest requset) {
		BaseValueObject baseValueObject = new BaseValueObject();
		try {
			String param = (String) paramMap.get("gpGiftPackageId");
			int status = 0;
			if (!ValidatorUtil.isEmptyIgnoreBlank(param)) {
				Integer gpGiftPackageId = Integer.valueOf(param);
				AmGiftPackage amGiftPackage = this.amGiftPackageMapper.selectByPrimaryKey(gpGiftPackageId);
				if (amGiftPackage != null) {
					String reviewStatus = amGiftPackage.getGpStatus();
					UserVo user=(UserVo) SessionManager.getUserInfo(requset);
					String area=amGiftPackage.getGpActivityarea();
					String areaId=String.valueOf(user.getAreaId());
					if(!area.contains(areaId)){
						baseValueObject.setCode(CodeEnum.REQUEST_ERROR);
						baseValueObject.setMsg("没有权限删除该礼包");
						return baseValueObject;
					}
					if (reviewStatus != null) {
						status = Integer.valueOf(reviewStatus);
					}
					List<Integer> listId = new ArrayList<Integer>();
					if (!ValidatorUtil.isEmpty(param)) {
						String[] str = param.split(",");
						for (String gpId : str) {
							listId.add(Integer.valueOf(gpId));
						}
					}
					if (SmiConstants.UNCOMMITTED.equals(status) || SmiConstants.FAILED.equals(status)) {
						int num = this.amGiftPackageMapper.updateByPrimaryKeySelective(listId);
						if (num > 0) {
							baseValueObject.setCode(CodeEnum.SUCCESS);
							baseValueObject.setMsg("删除礼包成功");
						} else {
							baseValueObject.setCode(CodeEnum.REQUEST_ERROR);
							baseValueObject.setMsg("删除礼包失败");
						}
						this.LOGGER.info("更新条数" + num);
					} else {
						baseValueObject.setCode(CodeEnum.REQUEST_ERROR);
						baseValueObject.setMsg("删除的目标礼包状态不符合删除要求");
					}
				} else {
					baseValueObject.setCode(CodeEnum.PROFILE_MISSING);
					baseValueObject.setMsg("删除的目标礼包不存在,请稍后再删");
				}
			}
		} catch (Exception e) {
			this.LOGGER.error("礼包删除异常" + e);
			throw new SmiBusinessException("礼包删除异常");
		}
		return baseValueObject;
	}

	/**
	 * 根据状态查询礼包信息
	 */
	@Override
	public SmiResult<List<WraparoundResVo>> selectGiftPackageByStatus(Map<String, Object> param) {
		List<WraparoundResVo> wrapList = new ArrayList<WraparoundResVo>();
		SmiResult<List<WraparoundResVo>> smiResult = new SmiResult<List<WraparoundResVo>>();
		try {
			// 设置分页
			PageHelper.startPage((Integer) param.get("pageNum"), (Integer) param.get("pageSize"));
			List<AmGiftPackage> amgift = this.amGiftPackageMapper.selectGiftPackageByStatus(param);
			PageInfo<AmGiftPackage> pageGift = new PageInfo<AmGiftPackage>(amgift);
			if (amgift.size() > 0) {
				for (AmGiftPackage am : amgift) {
					WraparoundResVo vo = new WraparoundResVo();
					vo.setActivityArea(am.getGpActivityarea());
					vo.setId(String.valueOf(am.getGpId()));
					vo.setPreSendNum(String.valueOf(am.getGpSendnum()));
					vo.setWraparoundName(am.getGpName());
					vo.setPutChannel(am.getGpChannel());
					vo.setActivityEndTime(am.getGpStarttime());
					vo.setActivityStartTime(am.getGpStarttime());
					vo.setStatus(am.getGpStatus());
					vo.setcRemark(am.getGpRemark());
					wrapList.add(vo);
				}
				smiResult.setCode(CodeEnum.SUCCESS);
				smiResult.setData(wrapList);
				smiResult.setMsg("成功");
			} else {
				smiResult.setCode(CodeEnum.SUCCESS);
				smiResult.setData(wrapList);
				smiResult.setMsg("查无数据");
			}
			smiResult.setPages(pageGift.getPages());
			smiResult.setTotal(pageGift.getTotal());
			smiResult.setPageSize(pageGift.getPageSize());
		} catch (Exception e) {
			this.LOGGER.error("根据状态查询礼包信息异常", e);
			throw new SmiBusinessException("根据状态查询礼包信息异常");
		}
		return smiResult;
	}

	/**
	 * 复制礼包
	 */
	@Override
	@Transactional
	public int copyGiftPackage(String param, HttpServletRequest requset) {
		int num = 0;
		try {
			UserVo userVo = (UserVo) SessionManager.getUserInfo(requset);
			Integer gpId = Integer.valueOf(param);
			AmGiftPackage amPackage = this.amGiftPackageMapper.selectByPrimaryKey(gpId);
			if (amPackage != null) {
				AmGiftPackage amPackagevo = new AmGiftPackage();
				BeanUtils.copyProperties(amPackagevo, amPackage);
				amPackagevo.setGpStatus(String.valueOf(SmiConstants.UNCOMMITTED));
				amPackagevo.setGpCreateuser(userVo.getuName());
				amPackagevo.setGpCreatedate(DateUtils.now());
				amPackagevo.setGpLastmoduser(userVo.getuName());
				amPackagevo.setGpLastmoddate(DateUtils.now());
				int preSendNum = 0;
				if (!ValidatorUtil.isEmpty(amPackage.getGpSendnum())) {
					preSendNum = Integer.valueOf(amPackage.getGpSendnum());
				}
				amPackagevo.setGpRemainNum(preSendNum);
				amPackagevo.setGpChannel(amPackage.getGpChannel());
				num = this.amGiftPackageMapper.insertAmGiftPackage(amPackagevo);
			}
		} catch (Exception e) {
			this.LOGGER.error("复制礼包异常", e);
			throw new SmiBusinessException("复制礼包异常");
		}
		return num;
	}

	/**
	 * 礼包查看
	 */
	@Override
	public SmiResult<GiftPackageInfo> findGiftByPackageId(String param) {
		SmiResult<GiftPackageInfo> smiResult = new SmiResult<GiftPackageInfo>();
		AmGiftPackage amGiftPackage = null;
		GiftPackageInfo packageInfo = new GiftPackageInfo();
		try {
			if (!ValidatorUtil.isEmpty(param)) {
				Integer gpId = Integer.valueOf(param);
				amGiftPackage = this.amGiftPackageMapper.selectByPrimaryKey(gpId);
				if (amGiftPackage != null) {
					BeanUtils.copyProperties(packageInfo, amGiftPackage);
					byte[] b = amGiftPackage.getGpProvideuser();
					String provideUser = amGiftPackage.byteToStr(b);
					packageInfo.setGpProvideuser(provideUser);
					String preSendNum = amGiftPackage.getGpSendnum();
					packageInfo.setPreSendNum(preSendNum);
					packageInfo.setGpSendnum(Integer.valueOf(preSendNum) - amGiftPackage.getGpRemainNum());
					String area = amGiftPackage.getGpActivityshop();
					JSONArray json = JSON.parseArray(area);
					packageInfo.setGpActivityShop(json);
					smiResult.setCode(CodeEnum.SUCCESS);
					smiResult.setMsg("查看成功");
					smiResult.setData(packageInfo);
				} else {
					smiResult.setCode(CodeEnum.PROFILE_MISSING);
					smiResult.setMsg("查无数据");
				}
			} else {
				smiResult.setCode(CodeEnum.REQUEST_ERROR);
				smiResult.setMsg("请求参数有误");
			}
		} catch (Exception e) {
			this.LOGGER.error("礼包查看异常", e);
			throw new SmiBusinessException("礼包查看异常");

		}
		return smiResult;
	}

	/**
	 * 发放礼包
	 */
	@Override
	@Transactional
	public BaseValueObject sendGiftPackage(Map<String, Object> param, HttpServletRequest request) {
		BaseValueObject baseValueObject = new BaseValueObject();
		String packId = (String) param.get("gpGiftPackageId");
		Integer sendStatus = 0;
		try {
			if (!ValidatorUtil.isEmpty(packId)) {
				Integer giftPackId = Integer.valueOf(packId);
				AmGiftPackage amGiftPackage = this.amGiftPackageMapper.selectByPrimaryKey(giftPackId);
				if (amGiftPackage != null) {
					String reviewStatus = amGiftPackage.getGpStatus();
					if (reviewStatus != null) {
						sendStatus = Integer.valueOf(reviewStatus);
					}
					if (SmiConstants.STAYOUT.equals(sendStatus)) {
						Map<String, Object> paramMap = new HashMap<String, Object>();
						paramMap.put("packageId", giftPackId);
						paramMap.put("startStatus", String.valueOf(SmiConstants.STAYOUT));
						paramMap.put("endStatus", SmiConstants.CONFIRMONLINE);
						int num = this.amGiftPackageMapper.updateStatusByPackageId(paramMap);
						if (num > 0) {
							baseValueObject.setCode(CodeEnum.SUCCESS);
							baseValueObject.setMsg("礼包发送操作成功");
						} else {
							baseValueObject.setCode(CodeEnum.REQUEST_ERROR);
							baseValueObject.setMsg("礼包发送失败");
						}
					} else {
						baseValueObject.setCode(CodeEnum.PROFILE_MISSING);
						baseValueObject.setMsg("该礼包不是待发送的状态");
					}
				} else {
					baseValueObject.setCode(CodeEnum.PROFILE_MISSING);
					baseValueObject.setMsg("该礼包不存在");
				}
			}
		} catch (Exception e) {
			this.LOGGER.error("发送礼包异常", e);
			throw new SmiBusinessException("发送礼包异常");
		}
		return baseValueObject;

	}

	/**
	 * 礼包提交审核
	 */
	@Override
	@Transactional
	public BaseValueObject reviewGiftPackage(Map<String, Object> paramMap, HttpServletRequest request) {

		BaseValueObject baseValueObject = new BaseValueObject();
		try {
			String param = (String) paramMap.get("gpGiftPackageId");
			int status = 0;
			if (!ValidatorUtil.isEmptyIgnoreBlank(param)) {
				Integer gpId = Integer.valueOf(param);
				AmGiftPackage amGiftPackage = this.amGiftPackageMapper.selectByPrimaryKey(gpId);
				if (amGiftPackage != null) {
					String reviewStatus = amGiftPackage.getGpStatus();
					if (reviewStatus != null) {
						status = Integer.valueOf(reviewStatus);
					}
					if (SmiConstants.UNCOMMITTED.equals(status)) {
						Map<String, Object> params = new HashMap<String, Object>();
						params.put("packageId", param);
						params.put("startStatus", status);
						params.put("endStatus", SmiConstants.INAUDIT);
						int num = this.amGiftPackageMapper.updateStatusByPackageId(params);
						if (num > 0) {
							baseValueObject.setCode(CodeEnum.SUCCESS);
							baseValueObject.setMsg("提交审核礼包成功");
						} else {
							baseValueObject.setCode(CodeEnum.PROFILE_MISSING);
							baseValueObject.setMsg("审核的目标礼包不存在,请稍后再审");
						}
						this.LOGGER.info("更新条数" + num);
					} else {
						baseValueObject.setCode(CodeEnum.REQUEST_ERROR);
						baseValueObject.setMsg("审核的目标礼包状态不符合审核要求");
					}
				} else {
					baseValueObject.setCode(CodeEnum.PROFILE_MISSING);
					baseValueObject.setMsg("该礼包不存在或已被删除");
				}
			} else {
				baseValueObject.setCode(CodeEnum.REQUEST_ERROR);
				baseValueObject.setMsg("礼包id输入有误");
			}
		} catch (Exception e) {
			this.LOGGER.error("礼包审核异常" + e);
			throw new SmiBusinessException("审核礼包异常");
		}
		return baseValueObject;

	}

}
