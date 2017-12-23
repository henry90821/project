package com.iskyshop.foundation.service.impl;

import java.io.StringReader;
import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.ServiceException;

import org.apache.axis.client.Call;
import org.apache.axis.encoding.XMLType;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.nutz.json.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iskyshop.core.beans.exception.CallException;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.domain.ProductMapping;
import com.iskyshop.foundation.domain.ShipAddress;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IOrderDistributeService;
import com.iskyshop.foundation.service.IProductMappingService;
import com.iskyshop.foundation.service.IShipAddressService;
import com.tydic.framework.util.PropertyUtil;

@Service("orderDistributeServiceHisenseImpl")
public class OrderDistributeServiceHisenseImpl implements IOrderDistributeService {

	private Logger logger = Logger.getLogger(this.getClass());

	@Autowired
	private IShipAddressService shipAddressService;

	@Autowired
	private IGoodsService goodsService;

	@Autowired
	private IProductMappingService productMappingService;

	/**
	 * 调用海信订单推送方法
	 */
	@Transactional(readOnly = true)
	public boolean distribte(String message, String url) throws CallException {
		// 远程调用路径
		String endpoint = url;
		// 结果
		String result = "";
		// 海信订单推送namespace
		String targetNamespace = PropertyUtil.getProperty("new_hisense_targetNamespace");
		// 海信订单推送方法名
		String method = "Req_Order";
		org.apache.axis.client.Service service = new org.apache.axis.client.Service();
		Call call;
		try {
			call = (Call) service.createCall();
			call.setTargetEndpointAddress(endpoint);
			// 调用的方法名
			call.setOperationName(new QName(targetNamespace, method));
			// 设置参数名
			call.addParameter(new QName(targetNamespace, "req"), // 参数名
					XMLType.XSD_STRING, // 参数类型:String
					ParameterMode.IN); // 参数模式：'IN' or 'OUT'

			// 设置返回值类型
			call.setReturnType(XMLType.XSD_STRING); // 返回值类型：String
			call.setSOAPActionURI(targetNamespace + method);
			result = (String) call.invoke(new Object[] { message }); // 远程调用
		} catch (RemoteException e) {
			logger.info(e);
			throw new CallException("远程调用通信异常");
		} catch (ServiceException e1) {
			logger.info(e1);
			throw new CallException("服务异常");
		}
		if ("1".equals(getResult(result))) {
			return true;
		} else {
			throw new CallException("调用报文参数错误");
		}
	}

	/**
	 * 拼装订单推送报文
	 */
	@Transactional(readOnly = true)
	public String createPushMessage(OrderForm orderform, String destination) {
		// TODO Auto-generated method stub
		String detail = PropertyUtil.getProperty("hisense_pushorder_detail");
		String headitem = PropertyUtil.getProperty("hisense_pushorder_headitem");
		String req = PropertyUtil.getProperty("hisense_pushorder_req");

		headitem = headitem.replaceFirst("CUST_ORDERCODE_VAL", CommUtil.null2String(orderform.getOrder_id()));
		String dispatchday = "0";
		String dispatchtime = "0";
		String request_arrival_time = "";

		if (StringUtils.isNullOrEmpty(orderform.getDelivery_time())) {
			dispatchday = "0";
			dispatchtime = "0";
		} else if ("工作日9点-18点可配送".equals(orderform.getDelivery_time())) {
			dispatchday = "1";
			dispatchtime = "1";
		} else if ("工作日、双休日、法定节假日均可配送".equals(orderform.getDelivery_time())) {
			dispatchday = "0";
			dispatchtime = "0";
		} else {
			String datetime = orderform.getDelivery_time().toString().substring(0, 5);
			if ("01".equals(datetime.substring(0, 2)) || new Date().getMonth() == 12) {
				request_arrival_time = new Date().getYear() + 1 + "-" + datetime.substring(0, 4);
			} else {
				request_arrival_time = new Date().getYear() + "-" + datetime.substring(0, 4);
			}
		}
		headitem = headitem.replaceFirst("DISPATCHDAYDEMAND_VAL", dispatchday);
		headitem = headitem.replaceFirst("DISPATCHTIMEDEMAND_VAL", dispatchtime);
		if ("".equals(request_arrival_time)) {
			headitem = headitem.replaceFirst("REQUEST_ARRIVAL_TIME_VAL", "2000-01-01");
		} else {
			headitem = headitem.replaceFirst("REQUEST_ARRIVAL_TIME_VAL", CommUtil.null2String(request_arrival_time));
		}

		headitem = headitem.replaceFirst("PRIORITY_FLAG_VAL", "1");
		headitem = headitem.replaceFirst("BUYER_NAME_VAL", CommUtil.null2String(orderform.getReceiver_Name()));
		headitem = headitem.replaceFirst("BUYER_POSTCODE_VAL", CommUtil.null2String(orderform.getReceiver_zip()));
		headitem = headitem.replaceFirst("BUYER_PHONENUMBER_VAL", CommUtil.null2String(orderform.getReceiver_telephone()));
		headitem = headitem.replaceFirst("BUYER_MOBILENUMBER_VAL", CommUtil.null2String(orderform.getReceiver_mobile()));
		headitem = headitem.replaceFirst("BUYER_PROVICE_VAL", "");
		headitem = headitem.replaceFirst("BUYER_CITY_VAL", "");
		headitem = headitem.replaceFirst("BUYER_DISTRICT_VAL", "");
		headitem = headitem.replaceFirst("BUYER_SHIPPINGADDRESS_VAL",
				CommUtil.null2String(orderform.getReceiver_area_info()));
		headitem = headitem.replaceFirst("BUYER_EMAIL_VAL", "");
		headitem = headitem.replaceFirst("SIGN_STANDARD_VAL", CommUtil.null2String(orderform.getReceiver_mobile()));

		headitem = headitem.replaceFirst("REMARK_VAL", CommUtil.null2String(orderform.getOrder_id()));

		headitem = headitem.replaceFirst("FREIGHT_VAL", CommUtil.null2String(orderform.getShip_price()));
		headitem = headitem.replaceFirst("SHIPPING_VAL", CommUtil.null2String(orderform.getTransport()));
		headitem = headitem.replaceFirst("PAYMENT_VAL", CommUtil.null2String(orderform.getTotalPrice()));

		SimpleDateFormat myFmt2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		headitem = headitem.replaceFirst("CREATETIME_VAL", CommUtil.null2String(myFmt2.format(orderform.getAddTime())));
		headitem = headitem.replaceFirst("RECEIVABLESPRICE_VAL", CommUtil.null2String(orderform.getTotalPrice()));
		headitem = headitem.replaceFirst("COD_FLAG_VAL", CommUtil.null2String("0"));

		ShipAddress sa = this.shipAddressService.getObjByProperty(null, "sa_code", destination);

		headitem = headitem.replaceFirst("RECEIVE_POSID_VAL", CommUtil.null2String(destination));
		headitem = headitem.replaceFirst("RECEIVE_POSNAME_VAL", CommUtil.null2String(sa.getSa_name()));

		headitem = headitem.replaceFirst("RECEIVE_ISPOS_VAL", "1");
		headitem = headitem.replaceFirst("RECEIVE_COD_VAL", "0");
		// ******************

		String details = "";
		List<Map> list2 = Json.fromJson(List.class, CommUtil.null2String(orderform.getGoods_info()));

		BigDecimal allweight = new BigDecimal(0);
		int goods_count = 0;
		int line_count = 0;
		try {
			for (Map map : list2) {
				String detail_sub = detail;
				logger.debug(map.toString());
				detail_sub = detail_sub.replaceFirst("CUST_ORDERCODE_VAL", CommUtil.null2String(orderform.getOrder_id()));
				detail_sub = detail_sub.replaceFirst("NO_VAL", line_count + "");

				// 通过goods_id到ProductMapping表中查找goodscode;
				Map params = new HashMap();
				params.put("goods_id", CommUtil.null2Long(map.get("goods_id")));
				List<ProductMapping> list = productMappingService
						.query("select obj from ProductMapping obj where obj.goods.id=:goods_id", params, -1, -1);
				ProductMapping pm = list.get(0);
				detail_sub = detail_sub.replaceFirst("ITEM_ID_VAL", pm.getGoodsCode());

				detail_sub = detail_sub.replaceFirst("ITEM_NAME_VAL", map.get("goods_name").toString());
				detail_sub = detail_sub.replaceFirst("QTY_VAL", map.get("goods_count").toString());
				detail_sub = detail_sub.replaceFirst("PRICE_VAL", map.get("goods_price").toString());
				detail_sub = detail_sub.replaceFirst("TOTALPRICE_VAL", map.get("goods_all_price").toString());

				Long id = new Long(map.get("goods_id").toString());
				BigDecimal weight = this.goodsService.getObjById(id).getGoods_weight();
				if (null == weight) {
					weight = new BigDecimal(0);
				}
				allweight = allweight.add(weight.multiply(new BigDecimal(map.get("goods_count").toString())));
				goods_count = goods_count + new Integer(map.get("goods_count").toString());
				details = details + detail_sub;
				line_count++;
			}
		} catch (NullPointerException e) {
			throw new CallException("报文格式不符", e);
		}
		headitem = headitem.replaceFirst("LINECOUNT_VAL", CommUtil.null2String(line_count));
		headitem = headitem.replaceFirst("ITEMNUM_VAL", CommUtil.null2String(goods_count));
		headitem = headitem.replaceFirst("WEIGHT_VAL", CommUtil.null2String(allweight.toString()));
		req = req.replaceFirst("OUTORDER_VAL", headitem);
		req = req.replaceFirst("OUTORDERDETAIL_VAL", details);
		return req;
	}

	/*
	 * 获取调用结果私有方法
	 */
	private String getResult(String response) {
		SAXReader saxReader = new SAXReader();
		Document document = null;
		try {
			document = saxReader.read(new StringReader(response));
		} catch (DocumentException e) {
			logger.error("getResult 获取调用结果异常: " + e);
		}
		Element root = document.getRootElement();
		Node node = root.selectSingleNode("//OK");
		return node.getStringValue();
	}

}
