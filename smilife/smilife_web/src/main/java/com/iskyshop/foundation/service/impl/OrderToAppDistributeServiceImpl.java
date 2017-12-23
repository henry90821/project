package com.iskyshop.foundation.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.iskyshop.core.beans.exception.CallException;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.HttpUtils;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.service.IOrderDistributeService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.IShipAddressService;
import com.iskyshop.manage.admin.tools.OrderFormTools;
import com.smi.tools.http.HttpKit;

import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component(value = "orderToAppDistributeService")
public class OrderToAppDistributeServiceImpl implements IOrderDistributeService {
	private Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private OrderFormTools orderFormTools;
	@Autowired
	private IShipAddressService shipAddressService;
	@Autowired
	private IOrderFormService orderFormService;

	@Override
	@Transactional(readOnly = true)
	public boolean distribte(String message, String url) throws CallException {

		/**
		 * params.put("config_code", "ddapp"); List<OrderSyn> list = this.orderSynService.query(
		 * "select obj from OrderSyn obj where obj.synStatus!=1 and obj.goodsConfig.configCode=:config_code", params, -1,
		 * -1); String url = "http://localhost:8080/xmh_order_web/recodercontrol/synoder"; if
		 * (!StringUtils.isNullOrEmpty(list) && !StringUtils.isNullOrEmpty(list.get(0).getGoodsConfig())) { url =
		 * list.get(0).getGoodsConfig().getSynIUrl(); }
		 */
		logger.info("推送到掌上订单的数据内容："+message);
		if (!StringUtils.isNullOrEmpty(url)) {
			try {
				String retValue = HttpKit.post(url, message);
				Map map = JSONObject.fromObject(retValue);
				if ("100".equals(CommUtil.null2String(map.get("status")))) {
					return true;
				} else {
					throw new CallException(map.get("message").toString());
				}
			} catch (Exception e) {
				throw new CallException("接口调用异常.");
			}
		}
		return false;
	}

	@Transactional(readOnly = true)
	public String createPushMessage(OrderForm orderFormOld, String destination) {
		OrderForm orderForm = orderFormService.getObjById(CommUtil.null2Long(orderFormOld.getId()));
		
		Map<String, Object> mainMap = new HashMap<String, Object>();
		List arr = new ArrayList();
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> map1 = new HashMap<String, Object>();
		List arr2 = new ArrayList();
		if (!StringUtils.isNullOrEmpty(orderForm)) {
			map.put("order_sn", orderForm.getOrder_id());
		}
		map.put("order_amount", orderForm.getTotalPrice());
		map.put("add_time", CommUtil.formatLongDate(orderForm.getPayTime()));
		// ShipAddress shipAddress = this.shipAddressService.getObjById(orderForm.getShip_addr_id());
		map.put("store_id", StringUtils.isNullOrEmpty(destination) ? "" : destination);
		map.put("province_id", orderForm.getReceiverArea().getParent().getParent().getId());
		map.put("city_id", orderForm.getReceiverArea().getParent().getId());
		map.put("area_id", orderForm.getReceiverArea().getId());
		map.put("reciver_name", orderForm.getReceiver_Name());
		map.put("order_message", orderForm.getMsg()); // 订单附言
		map.put("delivery_type", orderForm.getDelivery_type()); // 配送方式
		map.put("delivery_time", orderForm.getDelivery_time()); // 配送时间

		List<Map> maps = this.orderFormTools.queryGoodsInfo(orderForm.getGoods_info());
		for (Map map3 : maps) {
			Map<String, Object> map2 = new HashMap<String, Object>();
			map2.put("goods_id", map3.get("goods_id"));
			map2.put("goods_image", map3.get("goods_mainphoto_path"));
			map2.put("goods_name", map3.get("goods_name"));
			map2.put("goods_price", map3.get("goods_price"));
			map2.put("goods_num", map3.get("goods_count"));
			map2.put("brand", "");
			map2.put("goods_attribute", map3.get("goods_gsp_val"));
			arr2.add(map2);
		}
		map1.put("address", orderForm.getReceiver_area() + orderForm.getReceiver_area_info());
		map1.put("phone", StringUtils.isNullOrEmpty(orderForm.getReceiver_telephone()) ? orderForm.getReceiver_mobile()
				: orderForm.getReceiver_telephone());
		map.put("reciver_info", map1);
		map.put("extend_order_goods", arr2);
		arr.add(map);
		String jsonStr = JSONArray.toJSONString(arr);
		logger.info("json data:" + jsonStr);
		return jsonStr;
	}
}
