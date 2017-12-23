package com.iskyshop.smilife.charge;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.foundation.domain.Predeposit;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.IPredepositService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.manage.admin.tools.UserTools;
import com.iskyshop.pay.tenpay.util.TenpayUtil;
import com.iskyshop.smilife.common.Result;
import com.iskyshop.smilife.enums.ErrorEnum;
@Service
@Transactional
public class ChargeServiceImpl implements IChargeService{
    /**日志*/
	private Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IPredepositService predepositService;
	@Autowired
	private UserTools			userTools;
	
	 /**
     * 给指定用户的账户充值：在充值表Predeposit中生成一个充值记录（并未支付）
     * @param user    充值的用户，即将出钱的用户
     * @param amount  充值金额
     * @param custId  被充值用户的custId
     * @param remark  充值备注
     * @param url 暂时无用
     * @return 
     */
	@Override
	public Result charge(User user, String amount, String custId, String remark, String url) {
		//构建默认返回结果
		Result result = new Result();
		logger.info("充值下单参数：amount=" + amount + ",被充值用户的custId=" + custId + ",remark=" + remark + ",充值用户的id=" + user.getId());
		try{
			if (this.configService.getSysConfig().isDeposit()) {
				User custUser = userTools.syncBuyerFromCRM(custId, false, true);				
				
				//新建预存款管理实体
				Predeposit obj = new Predeposit();
				//设置充值时间
				obj.setAddTime(new Date());
				//设置支付状态 0为等待支付，1为线下提交支付完成申请，2为支付成功
				obj.setPd_pay_status(0);
				// 被充值会员(被充值的账户)
				obj.setPd_user(custUser);
				// 充值唯一编号记录，使用pd为前缀
				obj.setPd_sn("pd" + CommUtil.formatTime("yyyyMMddHHmmssSSS", new Date())+TenpayUtil.buildRandom(6));
				//充值会员(出钱的账户)
				obj.setPayUser(user);
				//充值金额
				obj.setPd_amount(new BigDecimal(CommUtil.null2Double(amount)));
				//请求备注
				obj.setPd_admin_info(remark);
				//保存充值信息
				this.predepositService.save(obj);
				Map<String,Object> map=new HashMap<String, Object>();
				map.put("value", obj.getPd_sn());
				//返回数据
				result.setData(map);
			} else if(StringUtils.isNullOrEmpty(custId)) {
				return result.set(ErrorEnum.REQUEST_ERROR).setMsg("被充值的custId不能为空");
			} else {//系统未开启预存款
				return result.set(ErrorEnum.REQUEST_ERROR).setMsg("系统未开启预存款");
			}
		}catch(Exception e){
			 result.set(ErrorEnum.SYSTEM_ERROR).setMsg(e.getMessage());
	         logger.error("[com.iskyshop.smilife.charge] 充值接口异常:", e);
		}
		return result;
	}
}
