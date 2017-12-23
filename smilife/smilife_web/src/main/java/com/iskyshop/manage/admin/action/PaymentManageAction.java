package com.iskyshop.manage.admin.action;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.annotation.SecurityMapping;
import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.query.PageList;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.core.tools.WebForm;
import com.iskyshop.foundation.domain.ChinaPayBank;
import com.iskyshop.foundation.domain.FTPServer;
import com.iskyshop.foundation.domain.Payment;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.domain.query.ChinaPayBankQueryObject;
import com.iskyshop.foundation.domain.query.CouponQueryObject;
import com.iskyshop.foundation.service.IChinaPayBankService;
import com.iskyshop.foundation.service.IPaymentService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.manage.admin.tools.PaymentTools;
import com.iskyshop.manage.ftp.tools.FTPServerTools;

/**
 * 
 * <p>
 * Title: PaymentManageAction.java
 * </p>
 * 
 * <p>
 * Description:支付方式控制器,配置系统接受支付的所有支付方式，B2B2C由平台统一收款，只需要运营商配置收款方式，商家无需关心收款方式
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2015
 * </p>
 * 
 * <p>
 * Company: 沈阳网之商科技有限公司 www.iskyshop.com
 * </p>
 * 
 * @author erikzhang
 * 
s * 
 * @version iskyshop_b2b2c v2.0 2015版
 */
@Controller
public class PaymentManageAction {
	private static Logger logger = Logger.getLogger(PaymentManageAction.class);
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IPaymentService paymentService;
	@Autowired
	private PaymentTools paymentTools;
	@Autowired
	private FTPServerTools FTPTools;
	@Autowired
	private IChinaPayBankService chinaPayBankService;

	@SecurityMapping(title = "支付方式列表", value = "/admin/payment_list.htm*", rtype = "admin", rname = "支付方式", rcode = "payment_set", rgroup = "设置")
	@RequestMapping("/admin/payment_list.htm")
	public ModelAndView payment_list(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/payment_list.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		SysConfig config = this.configService.getSysConfig();
		String store_payment = CommUtil.null2String(config.getStore_payment());
		Map map = Json.fromJson(HashMap.class, store_payment);
		if (map != null) {
			for (Iterator it = map.keySet().iterator(); it.hasNext();) {
				String key = (String) it.next();
				Object val = map.get(key);
				mv.addObject(key, val);
			}
		}
		mv.addObject("paymentTools", paymentTools);
		return mv;
	}

	@SecurityMapping(title = "支付方式设置", value = "/admin/payment_set.htm*", rtype = "admin", rname = "支付方式", rcode = "payment_set", rgroup = "设置")
	@RequestMapping("/admin/payment_set.htm")
	public ModelAndView payment_set(HttpServletRequest request, HttpServletResponse response, String mark, String type,
			String pay, String config_id) {
		ModelAndView mv = new JModelAndView("admin/blue/success.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if ("admin".equals(CommUtil.null2String(type))) {
			Map params = new HashMap();
			params.put("mark", mark);
			List<Payment> objs = this.paymentService.query("select obj from Payment obj where obj.mark=:mark", params, -1,
					-1);
			Payment obj = null;
			if (objs.size() > 0) {
				obj = objs.get(0);
			} else
				obj = new Payment();
			obj.setAddTime(new Date());
			obj.setMark(mark);
			obj.setInstall(!CommUtil.null2Boolean(pay));
			if (StringUtils.isNullOrEmpty(obj.getName())) {
				if ("alipay".equals(mark.trim())) {
					obj.setName("支付宝");
				}
				if ("balance".equals(mark.trim())) {
					obj.setName("预存款支付");
				}
				if ("outline".equals(mark.trim())) {
					obj.setName("线下支付");
				}
				if ("tenpay".equals(mark.trim())) {
					obj.setName("财付通");
				}
				if ("bill".equals(mark.trim())) {
					obj.setName("快钱支付");
				}
				if ("chinabank".equals(mark.trim())) {
					obj.setName("网银在线");
				}
				if ("alipay_wap".equals(mark.trim())) {
					obj.setName("支付宝手机网页支付");
				}
			}
			if (objs.size() > 0) {
				this.paymentService.update(obj);
			} else {
				this.paymentService.save(obj);
			}
		}
		if ("user".equals(CommUtil.null2String(type))) {
			SysConfig config = this.configService.getSysConfig();
			String store_payment = CommUtil.null2String(config.getStore_payment());
			Map map = Json.fromJson(HashMap.class, store_payment);
			if (map == null) {
				map = new HashMap();
			}
			map.put(mark, !CommUtil.null2Boolean(pay));
			store_payment = Json.toJson(map, JsonFormat.compact());
			config.setStore_payment(store_payment);
			if (!StringUtils.isNullOrEmpty(config_id)) {
				this.configService.update(config);
			} else {
				this.configService.save(config);
			}
		}
		mv.addObject("list_url", CommUtil.getURL(request) + "/admin/payment_list.htm?type=" + type);
		mv.addObject("op_title", "设置支付方式成功");
		mv.addObject("paymentTools", paymentTools);
		return mv;
	}

	@SecurityMapping(title = "支付方式编辑", value = "/admin/payment_edit.htm*", rtype = "admin", rname = "支付方式", rcode = "payment_set", rgroup = "设置")
	@RequestMapping("/admin/payment_edit.htm")
	public ModelAndView payment_edit(HttpServletRequest request, HttpServletResponse response, String mark) {
		ModelAndView mv = new JModelAndView("admin/blue/payment_info.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Map params = new HashMap();
		params.put("mark", mark);
		List<Payment> objs = this.paymentService.query("select obj from Payment obj where obj.mark=:mark", params, -1, -1);
		Payment obj = null;
		if (objs.size() > 0) {
			obj = objs.get(0);
			
			//dengyuqi 2015-9-14 编辑时加载已选的所属平台
			if(null != obj.getTerminal_mark()){
				Map<String,Boolean> terminal_marks_map = new HashMap<String,Boolean>();
				String[] terminal_marks = obj.getTerminal_mark().split(",");
				for(String terminal_mark :terminal_marks){
					terminal_marks_map.put(terminal_mark, true);
				}
				mv.addObject("terminal_marks", terminal_marks_map);
			}
			//end
		} else {
			obj = new Payment();
			obj.setMark(mark);
		}
		mv.addObject("obj", obj);
		mv.addObject("edit", true);
		return mv;
	}

	@SecurityMapping(title = "支付方式保存", value = "/admin/payment_save.htm*", rtype = "admin", rname = "支付方式", rcode = "payment_set", rgroup = "设置")
	@RequestMapping("/admin/payment_save.htm")
	public ModelAndView payment_save(HttpServletRequest request, HttpServletResponse response, String mark,
			String list_url) {
		ModelAndView mv = new JModelAndView("admin/blue/success.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Map params = new HashMap();
		params.put("mark", mark);
		List<Payment> objs = this.paymentService.query("select obj from Payment obj where obj.mark=:mark", params, -1, -1);
		Payment obj = null;
		if (objs.size() > 0) {
			Payment temp = objs.get(0);
			WebForm wf = new WebForm();
			obj = (Payment) wf.toPo(request, temp);
			
			//dengyuqi 2015-9-14 add
			String[] terminal_marks = request.getParameterValues("terminal_mark"); //所对应平台
			if(null != terminal_marks){
				StringBuffer sb = new StringBuffer();
				for(String terminal_mark : terminal_marks){
					sb.append(terminal_mark).append(",");
				}
				obj.setTerminal_mark(sb.toString().substring(0, sb.toString().lastIndexOf(",")));
			}else{
				obj.setTerminal_mark(null);
			}
			//end
		} else {
			WebForm wf = new WebForm();
			obj = wf.toPo(request, Payment.class);
			obj.setAddTime(new Date());
		}
		if (objs.size() > 0) {
			this.paymentService.update(obj);
		} else {
			this.paymentService.save(obj);
		}
		mv.addObject("op_title", "保存支付方式成功");
		mv.addObject("list_url", list_url);
		return mv;
	}
	
	@SecurityMapping(title = "银联在线列表", value = "/admin/chinapaybank_list.htm*", rtype = "admin", rname = "支付方式", rcode = "payment_set", rgroup = "设置")
	@RequestMapping("/admin/chinapaybank_list.htm")
	public ModelAndView chinapay_list(HttpServletRequest request, HttpServletResponse response, String currentPage,
			String orderBy, String orderType,String bank_name,String bank_position,String bank_gate_id) {
		ModelAndView mv = new JModelAndView("admin/blue/chinapaybank_list.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		ChinaPayBankQueryObject qo = new ChinaPayBankQueryObject(currentPage, mv, orderBy, orderType);
		if (!"".equals(CommUtil.null2String(bank_name))) {
			qo.addQuery("obj.bank_name", new SysMap("bank_name", "%"+bank_name+"%"), "like");
			mv.addObject("bank_name", bank_name);
		}
		
		if (!"".equals(CommUtil.null2String(bank_position))) {
			qo.addQuery("obj.bank_position", new SysMap("bank_position", Integer.parseInt(bank_position)), "=");
			mv.addObject("bank_position", bank_position);
		}
		
		if (!"".equals(CommUtil.null2String(bank_gate_id))) {
			qo.addQuery("obj.bank_gate_id", new SysMap("bank_gate_id", bank_gate_id), "=");
			mv.addObject("bank_gate_id", bank_gate_id);
		}
		
		qo.setOrderBy("bank_position desc,obj.bank_index");
		qo.setOrderType("asc");
		
		IPageList pList=this.chinaPayBankService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}
	
	@SecurityMapping(title = "新增银行", value = "/admin/chinapaybank_add.htm*", rtype = "admin", rname = "支付方式", rcode = "payment_set", rgroup = "设置")
	@RequestMapping("/admin/chinapaybank_add.htm")
	public ModelAndView chinapaybank_add(HttpServletRequest request, HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/chinapaybank_add.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("currentPage", currentPage);
		return mv;
	}
	
	@SecurityMapping(title = "编辑银行", value = "/admin/chinapaybank_edit.htm*", rtype = "admin", rname = "支付方式", rcode = "payment_set", rgroup = "设置")
	@RequestMapping("/admin/chinapaybank_edit.htm")
	public ModelAndView ftpserver_edit(HttpServletRequest request, HttpServletResponse response, String id,
			String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/chinapaybank_add.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (!StringUtils.isNullOrEmpty(id)) {
			ChinaPayBank chinaPayBank = this.chinaPayBankService.getObjById(Long.parseLong(id));
			mv.addObject("obj", chinaPayBank);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", true);
		}
		return mv;
	}
	
	@SecurityMapping(title = "银行保存", value = "/admin/chinapaybank_save.htm*", rtype = "admin", rname = "支付方式", rcode = "payment_set", rgroup = "设置")
	@RequestMapping("/admin/chinapaybank_save.htm")
	public String chinapaybank_save(HttpServletRequest request, HttpServletResponse response, String currentPage, String id) {
		WebForm webForm=new WebForm();
		ChinaPayBank chinaPayBank=new ChinaPayBank();
		if("".equals(id)){
			chinaPayBank=webForm.toPo(request, ChinaPayBank.class);
			chinaPayBank.setAddTime(new Date());
		}else{
			chinaPayBank = this.chinaPayBankService.getObjById(Long.parseLong(id));
			chinaPayBank=(ChinaPayBank) webForm.toPo(request, chinaPayBank);
		}
		
		String uploadFilePath = this.configService.getSysConfig()
				.getUploadFilePath();
		String saveFilePathName = CommUtil.getServerRealPathFromRequest(request) + uploadFilePath + File.separator + "cache";
		CommUtil.createFolder(saveFilePathName);
		Map map = new HashMap();
		try {
			map = CommUtil.saveFileToServer(request, "fileUploadField", saveFilePathName, null, null);
			String fileName=map.get("fileName").toString();
			if (!"".equals(fileName)) {
				String bank_img_url=this.FTPTools.systemUpload(map.get("fileName").toString(), "/bankimg");
				if(!StringUtils.isNullOrEmpty(bank_img_url) && !bank_img_url.endsWith("/")) {
					bank_img_url += "/";
				}
				bank_img_url=bank_img_url+fileName;
				chinaPayBank.setBank_img_url(bank_img_url);
			}
		} catch (Exception e) {
			logger.error(e);
		}
		
		if("".equals(id)){
			this.chinaPayBankService.save(chinaPayBank);
		}else{
			this.chinaPayBankService.update(chinaPayBank);
		}
		
		return "redirect:bank_add_success.htm?currentPage=" + currentPage;
	}
	
	@SecurityMapping(title = "新增银行保存成功", value = "/admin/bank_add_success.htm*", rtype = "admin", rname = "支付方式", rcode = "payment_set", rgroup = "设置")
	@RequestMapping("/admin/bank_add_success.htm")
	public ModelAndView bank_add_success(HttpServletRequest request, HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/success.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", CommUtil.getURL(request) + "/admin/chinapaybank_list.htm");
		mv.addObject("op_title", "新增银行保存成功");
		mv.addObject("add_url", CommUtil.getURL(request) + "/admin/chinapaybank_add.htm" + "?currentPage=" + currentPage);
		return mv;
	}
	
	@SecurityMapping(title = "银行删除", value = "/admin/chinapaybank_del.htm*", rtype = "admin", rname = "支付方式", rcode = "payment_set", rgroup = "设置")
	@RequestMapping("/admin/chinapaybank_del.htm")
	public String ftpserver_del(HttpServletRequest request, HttpServletResponse response, String mulitId, String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!"".equals(id)) {
				this.chinaPayBankService.delete(Long.parseLong(id));
			}
		}
		return "redirect:chinapaybank_list.htm?currentPage=" + currentPage;
	}
	
}
