package com.iskyshop.o2o.tools;

import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class GetInventory {
	private static Logger logger = Logger.getLogger(GetInventory.class);
	
	public static String createReq(String[] itemidarr, String[] postidarr) {
		String str = "<?xml version=\"1.0\" encoding=\"utf-8\"?><DATASTORE xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"><PARAMS><POSID>postid_replace</POSID></PARAMS><LISTS><ITEM_ID>itemid_replace</ITEM_ID></LISTS></DATASTORE>";
		String itemidstr = "";
		String postidstr = "";

		for (String itemid : itemidarr) {
			itemidstr = itemidstr + itemid + ";";
		}
		for (String postid : postidarr) {
			postidstr = postidstr + postid + ";";
		}
		str = str.replaceAll("postid_replace", postidstr);
		str = str.replaceAll("itemid_replace", itemidstr);
		return str;
	}

	/**
	 * 若远程调用失败，则返回null
	 * @param req
	 * @param endpoint
	 * @param targetNamespace
	 * @param method
	 * @return
	 */
	public static String invokeRemoteFuc(String req, String endpoint, String targetNamespace, String method) {
		// 远程调用路径
		String result = null;
		Service service = new Service();
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
			result = (String) call.invoke(new Object[] { req });// 远程调用
		} catch (Exception e) {
			logger.error("调用海信系统接口失败。调用参数：req=" + req + "  endpoint=" + endpoint + "  targetNamespace=" + targetNamespace + "  method=" + method, e);
		}
		return result;
	}

	/**
	 * 解析海信系统返回的商品库存报文。key：商品编码, value：为对应商品在各影院中的库存信息，为Map类型(key： 影院编码， value：对应海信商品在对应影院中的库存值。若报文错误则返回null
	 * @param str  海信系统返回的表示商品库存的报文
	 * @return
	 */
	public static Map<String, Map<String, Integer>> getResultList(String str) {
		SAXReader saxReader = new SAXReader();
		Document document = null;
		// 读入XML文本
		try {
			document = saxReader.read(new StringReader(str));
		} catch (Exception e) {
			logger.error("解析XML文本错误。被解析的文本为：" + str, e);
			return null;
		}
		
		// 获取根元素
		Element root = document.getRootElement();

		List<Element> RESULT = root.selectNodes("//OK");
		if (!"1".equals(RESULT.get(0).getText())) {
			List<Element>  reason = root.selectNodes("//REASON");
			logger.error("从海信系统返回的商品库存报文错误（结果标志字段的值=" + RESULT.get(0).getText() + "）：" + reason.get(0).getText());
			return null;
		}
		
		Map<String, Map<String, Integer>>  result = new HashMap<String, Map<String, Integer>>();//key：商品编码, value：为对应商品在各影院中的库存信息，为Map类型(key： 影院编码， value：对应海信商品在对应影院中的库存值
		List<Element> items = root.selectNodes("//ITEMINV");
		for(Element el: items) {
			String goodsCode = el.element("ITEM_ID").getText();
			String[] cinemaCodes = el.element("POSID").getText().split(";");
			String[] inventories = el.element("QTY").getText().split(";");
			Map<String, Integer> goodsInventories = new HashMap<String, Integer>();
			for(int i = 0; i < cinemaCodes.length; i++) {
				int qty = 0;
				if(!inventories[i].contains(".")) {
					qty = Integer.parseInt(inventories[i]);
				}
				goodsInventories.put(cinemaCodes[i], qty);
			}
			result.put(goodsCode, goodsInventories);
		}		
		
		return result;
	}
}
