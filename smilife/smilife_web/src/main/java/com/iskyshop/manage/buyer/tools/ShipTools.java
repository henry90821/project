package com.iskyshop.manage.buyer.tools;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.nutz.json.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.Md5Encrypt;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.foundation.domain.ExpressCompany;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.domain.virtual.TransInfo;
import com.iskyshop.foundation.service.IExpressCompanyService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.kuaidi100.domain.ExpressInfo;
import com.iskyshop.kuaidi100.pojo.TaskRequest;
import com.iskyshop.kuaidi100.pojo.TaskResponse;
import com.iskyshop.kuaidi100.post.HttpRequest;
import com.iskyshop.kuaidi100.post.JacksonHelper;
import com.iskyshop.kuaidi100.service.IExpressInfoService;

/**
 * 
 * 
 * <p>
 * Ship
 * </p>
 * 
 * <p>
 * Description:物流查询工具类
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
 * @author jinxinzhe
 * 
 * @date 2014年5月23日
 * 
 * @version iskyshop_b2b2c 2.0
 */
@Component
public class ShipTools {
	private Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IExpressCompanyService expressCompanyService;
	@Autowired
	private IExpressInfoService expressInfoService;

	public TransInfo query_ship_getData(String id) {
		TransInfo info = new TransInfo();
		OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
		try {
			ExpressCompany ec = this.queryExpressCompany(obj.getExpress_info());
			String companyMark = ec.getCompany_mark().split("_")[0];
			String query_url = "http://api.kuaidi100.com/api?id=" + this.configService.getSysConfig().getKuaidi_id()
					+ "&com=" + (ec != null ? companyMark : "") + "&nu=" + obj.getShipCode()
					+ "&show=0&muti=1&order=asc";
			URL url = new URL(query_url);
			URLConnection con = url.openConnection();
			con.setAllowUserInteraction(false);
			InputStream urlStream = url.openStream();
			String type = con.guessContentTypeFromStream(urlStream);
			String charSet = null;
			if (type == null) {
				type = con.getContentType();
			}
			if (type == null || type.trim().length() == 0 || type.trim().indexOf("text/html") < 0) {
				return info;
			}
			if (type.indexOf("charset=") > 0) {
				charSet = type.substring(type.indexOf("charset=") + 8);
			}
			byte[] b = new byte[10000];
			int numRead = urlStream.read(b);
			String content = new String(b, 0, numRead, charSet);
			while (numRead != -1) {
				numRead = urlStream.read(b);
				if (numRead != -1) {
					// String newContent = new String(b, 0, numRead);
					String newContent = new String(b, 0, numRead, charSet);
					content += newContent;
				}
			}
			info = Json.fromJson(TransInfo.class, content);
			urlStream.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return info;
	}

	public ExpressCompany queryExpressCompany(String json) {
		ExpressCompany ec = null;
		if (!StringUtils.isNullOrEmpty(json)) {
			HashMap map = Json.fromJson(HashMap.class, json);
			ec = this.expressCompanyService.getObjById(CommUtil.null2Long(map.get("express_company_id")));
		}
		return ec;
	}
	
	/**
	 * 建立订单物流查询推送订阅
	 * @param obj orderFrom
	 * @param url url
	 */
	public void postOrder(OrderForm obj, String url) {
		TaskRequest req = new TaskRequest();
		Map express_map = Json.fromJson(Map.class, obj.getExpress_info());
		req.setCompany(CommUtil.null2String(express_map.get("express_company_mark")).split("_")[0]);
		String from_addr = obj.getShip_addr();
		req.setFrom(from_addr);
		req.setTo(obj.getReceiver_area());
		req.setNumber(obj.getShipCode());
		req.getParameters().put("callbackurl", url + "/kuaidi100_callback.htm?order_id=" + obj.getId() + "&orderType=0");
		req.getParameters().put("salt", Md5Encrypt.md5(CommUtil.null2String(obj.getId())).toLowerCase());
		req.setKey(this.configService.getSysConfig().getKuaidi_id2());

		HashMap<String, String> p = new HashMap<String, String>();
		p.put("schema", "json");
		p.put("param", JacksonHelper.toJSON(req));
		try {
			String ret = HttpRequest.postData("http://www.kuaidi100.com/poll", p, "UTF-8");
			TaskResponse resp = JacksonHelper.fromJSON(ret, TaskResponse.class);
			logger.info(obj.getOrder_id() + "发货订阅返回信息：" + Json.toJson(resp));
			if (resp.getResult() == true) {
				ExpressInfo ei = new ExpressInfo();
				ei.setAddTime(new Date());
				ei.setOrder_id(obj.getId());
				ei.setOrder_express_id(obj.getShipCode());
				ei.setOrder_type(0);
				if (express_map != null) {
					ei.setOrder_express_name(CommUtil.null2String(express_map.get("express_company_name")));
				}
				this.expressInfoService.save(ei);
				logger.info(obj.getOrder_id() + "订阅成功");
			} else {
				logger.info(obj.getOrder_id() + "订阅失败");
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}
}
