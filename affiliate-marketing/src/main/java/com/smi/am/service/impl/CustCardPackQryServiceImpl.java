package com.smi.am.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aspectj.apache.bcel.classfile.Code;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.smi.am.constant.SmiConstants;
import com.smi.am.dao.model.AmCounponsDetails;
import com.smi.am.dao.model.AmCoupons;
import com.smi.am.dao.model.AmGiftPackage;
import com.smi.am.service.CustQryService;
import com.smi.am.service.ICouponsService;
import com.smi.am.service.ICustCardPackQryService;
import com.smi.am.service.IGiftPackageService;
import com.smi.am.service.vo.CouponsVo;
import com.smi.am.service.vo.CustCouponsVo;
import com.smi.am.service.vo.CustVo;
import com.smi.am.service.vo.GiftPackageResVo;
import com.smi.am.utils.CustResult;
import com.smi.am.utils.SmiResult;
import com.smilife.core.common.valueobject.enums.CodeEnum;
import com.smilife.core.exception.SmiBusinessException;

/**
 * 会员卡包服务基类
 * 
 * @author smi
 *
 */
@Service
public class CustCardPackQryServiceImpl implements ICustCardPackQryService {

	@Autowired
	private CustQryService custQryService;

	@Autowired
	private ICouponsService iCouponsService;

	@Autowired
	private IGiftPackageService iGiftPackageService;

	/**
	 * 会员卡包查询
	 */
	@Override
	public SmiResult<Object> qryCustCardPack(Map<String, Object> param) throws SmiBusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String number = (String) param.get("number");
		String wraparoundType = (String) param.get("wraparoundType");
		Integer pageNum = (Integer) param.get("pageNum");
		Integer pageSize = (Integer) param.get("pageSize");
		CustResult<Object> smiResults = new CustResult<Object>();
		if (number.length() > 11) {
			paramMap.put("custNbr", number);
		} else {
			paramMap.put("contactMobile", number);
		}
		paramMap.put("pageNum", pageNum);
		paramMap.put("pageSize", pageSize);
		try {
			// 发送http请求会员中心，查询会员信息
			SmiResult<List<CustVo>> smiResult = this.custQryService.qryCustList(paramMap);

			if (CodeEnum.SUCCESS.code().equals(smiResult.getCode())) {
				List<CustVo> returnList = smiResult.getData();
				for (CustVo cust : returnList) {
					String custNbr = cust.getCustNbr();
					if (!"".equals(custNbr) || custNbr != null) {
						paramMap.put("custNbr", custNbr);
						if (SmiConstants.COUPONS_TYPE.equals(wraparoundType)) {
							SmiResult<List<CustCouponsVo>> counponsDetails = this.iCouponsService
									.findCounponsDetailsJoinTable(paramMap);
							if (counponsDetails.getData() != null) {
								smiResults.setData(counponsDetails.getData());
								smiResults.setCode(CodeEnum.SUCCESS);
								smiResults.setTotal(counponsDetails.getTotal());
								smiResults.setPages(counponsDetails.getPages());
								smiResults.setPageSize(counponsDetails.getPageSize());
							} else {
								smiResults.setCode(CodeEnum.SUCCESS);
								smiResults.setData(counponsDetails.getData());
							}
						} else if (SmiConstants.GIFT_PACKAGE_TYPE.equals(wraparoundType)) {
							// 根据custNbr查询礼包详情
							SmiResult<List<GiftPackageResVo>> AmGiftPackage = this.iGiftPackageService
									.selectGiftPackageByCustNbr(paramMap);
							if (AmGiftPackage.getData() != null) {
								smiResults.setData(AmGiftPackage.getData());
								smiResults.setCode(CodeEnum.SUCCESS);
								smiResults.setTotal(AmGiftPackage.getTotal());
								smiResults.setPages(AmGiftPackage.getPages());
								smiResults.setPageSize(AmGiftPackage.getPageSize());
							} else {
								smiResults.setCode(CodeEnum.SUCCESS);
								smiResults.setData(AmGiftPackage.getData());
							}
						}
					}
					smiResults.setCust(cust);
					smiResults.setMsg("成功");
				}
			} else {
				smiResults.setCode(CodeEnum.REQUEST_ERROR);
				smiResults.setMsg(smiResult.getMsg());
			}
		} catch (Exception e) {
			throw new SmiBusinessException("会员卡包查询异常");
		}

		return smiResults;
	}

}
