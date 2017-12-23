package com.iskyshop.core.tools;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * 
 * <p>
 * Title: XMLUtil.java
 * </p>
 * 
 * <p>
 * Description: XML解析工具类，用来解析xml数据并封装为对应的数据格式，如解析XML数据封装为map，解析xml为json等等
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2014
 * </p>
 * 
 * <p>
 * Company: 沈阳网之商科技有限公司 www.iskyshop.com
 * </p>
 * 
 * @author erikzhang
 * 
 * @version iskyshop_b2b2c v2.0 2015版
 */
public final class XMLUtil {
	/**
	 * 私有化构造方法
	 */
	private XMLUtil() {
	}

	private static Logger logger = Logger.getLogger(XMLUtil.class);

	/**
	 * 解析xml数据，并将xml数据所有节点和值封装到map中返回，试用试用map.get("noteName")读取xml中节点值， 该方法只适合节点不重复的简单xml解析，系统用在充值接口回调数据的解析
	 * 
	 * @param xml
	 *            待解析的xml
	 * @param igoreNull
	 *            是否忽略空值节点
	 * @return 解析结果
	 */
	public static Map<String, String> parseXML(String xml, boolean igoreNull) {

		Map<String, String> map = new HashMap<String, String>();
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new InputSource(new StringReader(xml)));
			Element root = doc.getDocumentElement();
			NodeList optionNodeList = root.getChildNodes();
			if (optionNodeList != null) {
				int totalNode = optionNodeList.getLength();
				for (int i = 0; i < totalNode; i++) {
					Node optionNode = optionNodeList.item(i);

					if (igoreNull) {
						if (!"".equals(CommUtil.null2String(optionNode.getTextContent()))) {
							map.put(optionNode.getNodeName(), CommUtil.null2String(optionNode.getTextContent()));
						}
					} else {
						map.put(optionNode.getNodeName(), CommUtil.null2String(optionNode.getTextContent()));
					}

				}
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return map;
	}

	/**
	 * 将封装好的map转化为json数据并返回
	 * 
	 * @param map
	 *            map对象
	 * @return json串
	 */
	@SuppressWarnings("rawtypes")
	public static String map2Json(Map map) {
		String json = Json.toJson(map, JsonFormat.compact());
		return json;
	}
}
