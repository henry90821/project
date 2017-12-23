package com.iskyshop.manage.admin.tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.foundation.domain.ChinaPayBank;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.domain.Payment;
import com.iskyshop.foundation.domain.Predeposit;
import com.iskyshop.foundation.service.IChinaPayBankService;
import com.iskyshop.foundation.service.IPaymentService;
import com.iskyshop.foundation.service.IUserService;

/**
 * 支付方式处理工具类，用来管理支付方式信息，主要包括查询支付方式等
 * 
 */
@Component
public class PaymentTools {
	@Autowired
	private IPaymentService paymentService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IChinaPayBankService chinaPayBankService;

	public boolean queryPayment(String mark) {
		Map params = new HashMap();
		params.put("mark", mark);
		List<Payment> objs = this.paymentService.query("select obj from Payment obj where obj.mark=:mark", params, -1, -1);
		if (objs.size() > 0) {
			return objs.get(0).isInstall();
		} else {
			return false;
		}
	}

	public Map queryShopPayment(String mark) {
		Map ret = new HashMap();
		Map params = new HashMap();
		params.put("mark", mark);
		List<Payment> objs = this.paymentService.query("select obj from Payment obj where obj.mark=:mark", params, -1, -1);
		if (objs.size() == 1) {
			ret.put("install", objs.get(0).isInstall());
			ret.put("content", objs.get(0).getContent());
		} else {
			ret.put("install", false);
			ret.put("content", "");
		}
		return ret;
	}

	/**
	 * 按对应平台查询已启用的支付方式
	 * 
	 * @author dengyuqi
	 * @since 2015-9-14
	 * @param terminalMark
	 *            对应的平台(all,pc,wap,app)
	 * @return 支付方式列表
	 */
	public List<Payment> queryByTerminalMark(String... terminalMarks) {
		if (null == terminalMarks || terminalMarks.length < 1) {
			return Collections.emptyList();
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("install", true);
		StringBuffer terminalMark = new StringBuffer();
		for (int i = 0; i < terminalMarks.length; i++) {
			terminalMark.append(terminalMarks[i]);
			if (i < terminalMarks.length - 1) {
				terminalMark.append(",");
			}
		}
		params.put("terminal_mark", "%" + terminalMark + "%");
		return this.paymentService.query(
				"select obj from Payment obj where obj.install=:install and obj.terminal_mark like :terminal_mark", params,
				-1, -1);
	}

	/**
	 * 查询所有的支付方式 //解决Bug 616 后台订单管理页面，支付方式下拉项数据没有银联在线有卡和无卡;时增加的方法
	 * 
	 * @return List<Payment>
	 */
	public List<Payment> queryAllBank() {
		return this.paymentService.query("select obj from Payment obj", null, -1, -1);
	}

	/***
	 * 
	 * @return
	 */
	public List<ChinaPayBank> queryChinaPayBank(int position) {
		List<ChinaPayBank> chinaPayBankList = new ArrayList<ChinaPayBank>();
		boolean flag = this.queryPayment("chinapay");
		if (flag) {
			Map params = new HashMap();
			params.put("bank_position", position);
			String query = "select obj from ChinaPayBank obj where obj.bank_position=:bank_position order by obj.bank_index asc";
			chinaPayBankList = this.chinaPayBankService.query(query, params, -1, -1);
		}
		return chinaPayBankList;
	}

	
	/**
	 * 获取购物订单的支付方式。若订单未支付，则返回“未支付”。若参数of为null，则返回null
	 * @param of
	 * @return
	 */
	public String getOrderPaymentName(OrderForm of) {
		if(of != null) {			
			if(of.getPayment() != null) { 
				return of.getPayment().getName();
			} else {
				return "未支付";
			}
		}
		
		return null;
	}
	
	
	/**
	 * 获取充值订单的支付方式。若订单未支付，则返回“未支付”。若参数of为null，则返回null。若示找到对应的支付方式，则返回"未识别的支付方式"
	 * @param pd
	 * @return
	 */
	public String getPredepositOrderPaymentName(Predeposit pd) {
		if(pd != null) {			
			if(StringUtils.isNullOrEmpty(pd.getPd_payment())) { 
				return "未支付";
			} else {
				if("outline".equals(pd.getPd_payment())) {//线下汇款
					return "汇款";
				}
				
				Payment payment = paymentService.getObjByProperty(null, "mark", pd.getPd_payment());
				if(payment != null) {
					return payment.getName();
				} else {					
					return "未识别的支付方式";
				}
			}
		}
		
		return null;
	}
}
