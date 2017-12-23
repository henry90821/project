package com.smi.tools.kits;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

/**
 * XML工具类<br>
 */
public class XmlKit {

	/** 在XML中无效的字符 正则 */
	public final static String INVALID_REGEX = "[\\x00-\\x08\\x0b-\\x0c\\x0e-\\x1f]";

	/**
	 * 生成空的xml文件头
	 * @param xmlPath 文件路径
	 * @return
	 */
	public static Document createEmptyXmlFile(String xmlPath) {
		if (xmlPath == null || xmlPath.equals("")) {
			return null;
		}
		XMLWriter output;
		Document document = DocumentHelper.createDocument();
		OutputFormat format = OutputFormat.createPrettyPrint();
		try {
			output = new XMLWriter(new FileWriter(xmlPath), format);
			output.write(document);
			output.close();
		} catch (IOException e) {
			return null;
		}
		return document;
	}

	/**
	 * 去除XML文本中的无效字符
	 * 
	 * @param xmlContent
	 *            XML文本
	 * @return 当传入为null时返回null
	 */
	public static String cleanInvalid(String xmlContent) {
		if (xmlContent == null)
			return null;
		return xmlContent.replaceAll(INVALID_REGEX, "");
	}

	/**
	 * 将xml字符串转换为Document对象
	 * 
	 * @param strXml
	 *            xml字符串
	 * @return document对象
	 */
	public static Document parserXml(String strXml) {
		String xmlStr = cleanInvalid(strXml);
		Document doc = null;
		try {
			doc = DocumentHelper.parseText(xmlStr);
			return doc;
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return doc;
	}

	/**
	 * 根据xml文件路径取得document对象
	 * 
	 * @param xmlPath
	 * @return
	 * @throws DocumentException
	 */
	public static Document getDocument(String xmlPath) {
		if (xmlPath == null || xmlPath.equals(""))
			return null;

		File file = new File(xmlPath);
		if (!file.exists()) {
			return createEmptyXmlFile(xmlPath);
		}

		SAXReader reader = new SAXReader();
		Document document = null;
		try {
			document = reader.read(xmlPath);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return document;
	}

	/**
	 * 根据xpath 获取指定的元素
	 * 
	 * @param xpath
	 * @param doc
	 * @return
	 */
	public static Element getElementByXpath(String xpath, Document doc) {
		@SuppressWarnings("unchecked")
		List<Element> parameterList = doc.selectNodes(xpath);
		if (parameterList != null && parameterList.size() > 0) {
			return (Element) parameterList.get(0);
		}
		return null;
	}

	/**
	 * 得到根节点
	 * @param document 
	 * @return 如果docment为null则返回null，否则返回element
	 */
	public static Element getRootNode(Document document) {
		if (null == document) {
			return null;
		}

		Element root = document.getRootElement();
		return root;
	}

	/**
	 * 获取节点eleName下的文本值，若eleName不存在则返回默认值defaultValue
	 *
	 * @param eleName
	 * @param defaultValue
	 * @return
	 */
	public static String getElementValue(Element eleName, String defaultValue) {
		if (ObjectKit.isNull(eleName)) {
			return defaultValue == null ? "" : defaultValue;
		} else {
			return eleName.getTextTrim();
		}
	}

	public static String getElementValue(String eleName, Element parentElement) {
		if (ObjectKit.isNull(parentElement)) {
			return null;
		} else {
			Element element = parentElement.element(eleName);
			if (ObjectKit.isNotNull(element)) {
				return element.getTextTrim();
			} else {
				try {
					throw new Exception("找不到节点" + eleName);
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}
		}
	}

	/**
	 * 获取节点eleName下的文本值
	 *
	 * @param eleName
	 * @return
	 */
	public static String getElementValue(Element eleName) {
		return getElementValue(eleName, null);
	}

	/**
	 * 获取CDATA内容
	 * 
	 * @param body
	 *            Document
	 * @param path
	 *            路径
	 * @return
	 */
	public static Document findCDATA(Document body, String path) {
		return XmlKit.parserXml(XmlKit.getElementValue(path, body.getRootElement()));
	}

	/**
	 * 持久化Document 
	 * @param doc Docment
	 * @param file 文件
	 * @param charset 字符集
	 * @throws Exception 异常
	 */
	public static void xmltoFile(Document doc, File file, String charset) throws Exception {
		if (ObjectKit.isNull(doc)) {
			throw new NullPointerException("doc cant not null");
		}
		if (ObjectKit.isNull(charset)) {
			throw new NullPointerException("charset cant not null");
		}
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding(charset);
		FileOutputStream os = new FileOutputStream(file);
		OutputStreamWriter osw = new OutputStreamWriter(os, charset);
		XMLWriter xmlWriter = new XMLWriter(osw, format);
		try {
			xmlWriter.write(doc);
			xmlWriter.close();
			if (osw != null) {
				osw.close();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 将document保持到文件
	 * @param doc document
	 * @param filePath 保存的文件路径
	 * @param charset 字符集
	 * @throws Exception 异常
	 */
	public static void xmltoFile(Document doc, String filePath, String charset) throws Exception {
		xmltoFile(doc, new File(filePath), charset);
	}
	
	/**
	 * 将document保持到文件(默认采用UTF-8编码)
	 * @param doc document
	 * @param filePath 保存的文件路径
	 * @throws Exception 异常
	 */
	public static void xmltoFile(Document doc, String filePath) throws Exception {
		xmltoFile(doc, new File(filePath), "UTF-8");
	}

	/**
	 * 获取指定id的xml元素(单个xml中id唯一的情况下使用)
	 * 
	 * @param id
	 * @param doc
	 * @return
	 */
	public static Element getElementById(String id, Document doc) {
		return getElementByXpath("//*[@id='" + id + "']", doc);
	}

	/**
	 * 在指定id的元素后面插入元素
	 * 
	 * @param pid
	 * @param newele
	 * @param doc
	 * @return
	 */
	public static Document insertElement(String pid, Element newele, Document doc) {
		return insertElement(pid, newele, doc, 1);
	}

	/**
	 * 在指定id的元素后面或前面插入元素
	 * 
	 * @param pid
	 * @param newele
	 * @param doc
	 * @param position
	 *            为1表示在坐标元素之后----- 为0 则在坐标元素之前
	 * @return
	 */
	public static Document insertElement(String pid, Element newele, Document doc, int position) {
		Element element = getElementById(pid, doc);// 坐标元素
		@SuppressWarnings("unchecked")
		List<Element> list = element.getParent().content();// 获取坐标元素父元素下的所有元素
		list.add(list.indexOf(element) + position, newele);
		return doc;
	}

	/**
	 * 删除文档doc的指定路径下的所有子节点（包含元素，属性等） <br/>
	 * 如果路径相同一并删除
	 * 
	 * @param doc
	 *            文档对象
	 * @param xpath
	 *            指定元素的路径 根据路径可删除元素、属性
	 * @return 删除成功时返回true，否则false
	 */
	public static boolean deleteNodes(Document doc, String xpath) {
		boolean flag = true;
		try {
			@SuppressWarnings("unchecked")
			List<Node> nlist = doc.selectNodes(xpath);
			for (Node node : nlist) {
				node.detach();
			}
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}

	/**
	 * 删除一个父元素下所有的子节点（包含元素，属性等）
	 * 
	 * @param element
	 *            父元素
	 * @return 删除成功时返回true，否则false
	 */
	public static boolean deleteChildren(Element element) {
		boolean flag = true;
		try {
			@SuppressWarnings("unchecked")
			List<Node> nlist = element.elements();
			for (Node node : nlist) {
				node.detach();
			}
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}

	/**
	 * 删除指定的元素
	 * 
	 * @param document
	 * @param ele
	 * @return
	 */
	public static boolean deleteElement(Element ele) {
		@SuppressWarnings("unchecked")
		List<Element> list = ele.getParent().content();
		list.remove(list.indexOf(ele));
		return true;
	}

}
