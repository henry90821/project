/**
 * 文件名称：JsonUtil.java
 * 版权：CopyRight 2014-2015 Digione Tech. Co. Ltd. All Rigths Reserved. 
 * 描述：JSON转换工具类
 * 修改人：caols
 * 修改时间：2014-10-14 - 上午10:06:11
 * BugId: 无
 * 修改内容：
 */
package com.smi.tools.kits;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * 功能描述：JSON转换工具类 <br/>
 * 作者： carson <br/>
 * 时间： 2015年2月15日下午3:21:44 <br/>
 * BugID: <br/>
 * 修改内容： <br/>
 */
public final class JsonKit {

	private JsonKit() {
	}

	/**
	 * 将对象转换为json字符串
	 * 
	 * @param obj
	 *            待转换的对象
	 * @return json字符串
	 */
	public static String toJsonString(Object obj) {
		if (null == obj) {
			return null;
		}
		return JSON.toJSONString(obj);
	}

	/**
	 * 将对象转换为json字符串, 并将时间字段进行格式化
	 * 
	 * @param obj
	 *            待转换的对象
	 * @param dateFormat
	 *            时间格式，若该字段为null，将使用默认的格式 "yyyy-MM-dd HH:mm:ss"
	 * @return 转换后的json字符串
	 */
	public static String toJsonStringWithDateFormat(Object obj, String dateFormat) {
		if (null == obj) {
			return null;
		}
		if (StrKit.isBlank(dateFormat)) {
			return JSON.toJSONStringWithDateFormat(obj, DateKit.NORM_DATETIME_PATTERN);
		} else {
			return JSON.toJSONStringWithDateFormat(obj, dateFormat);
		}
	}

	/**
	 * 将对象转换为json字符串, 并将时间字段进行格式化为yyyy-MM-dd HH:mm:ss格式
	 * 
	 * @param obj
	 *            待转换的对象
	 * @return 转换后的json字符串
	 */
	public static String toJsonStringWithDateFormat(Object obj) {
		return toJsonStringWithDateFormat(obj, DateKit.NORM_DATETIME_PATTERN);
	}

	/**
	 * 将对象转换为json字符串(将中文转换为unicode编码以兼容IE6同时将时间字段格式化)
	 * 
	 * @param obj
	 *            待转换的对象
	 * @return 转换后的json字符串
	 */
	public static String toJsonWithChineseToUnicode(Object obj) {
		if (null == obj) {
			return null;
		}
		return JSON.toJSONStringWithDateFormat(obj, DateKit.NORM_DATETIME_PATTERN, SerializerFeature.BrowserCompatible);
	}

	/**
	 * 将对象转换为json字符串(关闭引用检测和生成)
	 * 
	 * @param obj
	 *            待转换的字符串
	 * @return 转换后的json字符串
	 */
	public static String toJsonWithNoRefrenceDetect(Object obj) {

		if (null == obj) {
			return null;
		}
		return JSON.toJSONStringWithDateFormat(obj, DateKit.NORM_DATETIME_PATTERN,
				SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 将json字符串转换为list对象
	 * 
	 * <pre>
	 * 使用方法：<br />
	 * List<User> list = JsonUtil.parserToList(jsonStr, User.class);
	 * </pre>
	 * 
	 * @param jsonStr
	 *            待转换的json字符串
	 * @param clazz
	 *            对象类型
	 * @param <T>
	 *            对象类型标识
	 * @return List<T> 转换后的list对象
	 */
	public static <T> List<T> parserToList(String jsonStr, Class<T> clazz) {
		if (null == jsonStr) {
			return null;
		}
		return JSON.parseArray(jsonStr, clazz);
	}

	/**
	 * 将json对象转换为Map
	 * 
	 * @param jsonStr
	 *            待转换的json字符串
	 * @return map对象
	 */
	public static Map<String, Object> parserToMap(String jsonStr) {
		if (null == jsonStr) {
			return null;
		}

		Map<String, Object> map = new HashMap<String, Object>();
		// 最外层解析
		JSONObject json = JSON.parseObject(jsonStr);

		for (Object k : json.keySet()) {
			Object v = json.get(k);
			// 如果内层还是数组的话，继续解析
			if (v instanceof JSONArray) {
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				Iterator<Object> it = ((JSONArray) v).iterator();
				while (it.hasNext()) {
					JSONObject json2 = (JSONObject) it.next();
					list.add(parserToMap(json2.toString()));
				}
				map.put(k.toString(), list);
			} else {
				map.put(k.toString(), v);
			}
		}
		return map;
	}

	/**
	 * 将Json字符串解析程Map对象(只支持Map<String,T> 和 Map<String, List<T>) 类型的转换)
	 * 
	 * <pre>
	 *  该方法目前只支持两种类型的json到map的转换，
	 *  (1) Map key为字符串，Value为一个非集合类型对象
	 *  (2) Map key为字符串， Value为一个List类型，list中存放非集合类型对象
	 *  如果为更为负责的集合对象，请使用parserToMap方法，得到后在分别进行转换
	 *  例如：
	 *      Map<String, User>, Map<String, List<User>)
	 *  使用方法：
	 *      Map<String, User> map = parserToMap2(jsonStr, User.class);
	 *          User u = (User)map.get(key);
	 *      Map<String, Integer> map = parserToMap2(jsonStr, Integer.class);
	 *          Integer num = (Integer)map.get(key);
	 *      Map<String, String> map = parserToMap2(jsonStr, String.class);
	 *          String name = (String)map.get(key);
	 *      
	 *      Map<String, List<User>> map = parserToMap2(jsonStr, User.class);
	 *          List<User> users = (List<User>)map.get(key);
	 *      Map<String, List<Integer>> map = parserToMap2(jsonStr, Integer.class);
	 *          List<Integer> nums = (List<Integer>)map.get(key);
	 * </pre>
	 * 
	 * @param jsonStr
	 *            待转换的字符串
	 * @param clazz
	 *            集合中的元素 类 型
	 * @param <T>
	 *            泛型
	 * @return Map集合
	 */
	public static <T> Map<String, Object> parserToMap2(String jsonStr, Class<T> clazz) {
		if (null == jsonStr) {
			return null;
		}

		Map<String, Object> map = new HashMap<String, Object>();
		// 最外层解析
		JSONObject json = JSON.parseObject(jsonStr);

		for (Object k : json.keySet()) {
			Object v = json.get(k);
			String vStr = v.toString();

			// 如果内层还是数组的话，继续解析
			if (v instanceof JSONArray) {
				List<T> list = new ArrayList<T>();
				list = JsonKit.parserToList(vStr, clazz);
				map.put(k.toString(), list);
			} else {
				// 转换为对象
				T obj = JSON.parseObject(vStr, clazz);
				map.put(k.toString(), obj);
			}
		}
		return map;
	}

	/**
	 * 将json字符串转换为JsonArray对象
	 * 
	 * @param jsonStr
	 *            json字符串
	 * @return 转换后的JSONArray
	 */
	public static JSONArray parserToJsonArray(String jsonStr) {
		if (null == jsonStr) {
			return null;
		}
		return JSON.parseArray(jsonStr);
	}

	/**
	 * 将json字符串转换为对象
	 * 
	 * <pre>
	 * 使用方法：<br />
	 * User user = JsonUtil.parseObject(jsonStr, User.class);
	 * </pre>
	 * 
	 * @param jsonStr
	 *            json字符串
	 * @param clazz
	 *            对象类型
	 * @param <T>
	 *            返回对象类型
	 * @return 转换后的对象
	 */
	public static <T> T parseObject(String jsonStr, Class<T> clazz) {
		if (null == jsonStr) {
			return null;
		}
		return JSON.parseObject(jsonStr, clazz);
	}

	/**
	 * 将json字符串转换为JSONObject对象
	 * 
	 * @param jsonStr
	 *            json字符串
	 * @return 转换后的JSONObject
	 */
	public static JSONObject parseObject(String jsonStr) {
		if (null == jsonStr) {
			return null;
		}
		return JSON.parseObject(jsonStr);
	}

	/**
	 * 单位缩进字符串，4个空格
	 */
	private static String SPACE = "    ";

	/**
	 * 返回格式化JSON字符串。
	 * 
	 * @param json
	 *            未格式化的JSON字符串。
	 * @return 格式化的JSON字符串。
	 */
	public static String formatJson(String json) {
		StringBuffer result = new StringBuffer();

		int length = json.length();
		int number = 0;
		char key = 0;

		// 遍历输入字符串。
		for (int i = 0; i < length; i++) {
			// 1、获取当前字符。
			key = json.charAt(i);

			// 首个前花括号换号
			if (i == 0 && key == '{') {
				result.append('\n');
			}

			// 2、如果当前字符是前方括号、前花括号做如下处理：
			if ((key == '[') || (key == '{')) {
				// （1）如果前面还有字符，并且字符为“：”，打印：换行和缩进字符字符串。
				/*
				 * if ((i - 1 > 0) && (json.charAt(i - 1) == ':')) { result.append('\n'); result.append(" ");//前方括号、前花括号 空一格
				 * }
				 */

				// （2）打印：当前字符。
				result.append(key);

				// （3）前方括号、前花括号，的后面必须换行。打印：换行。
				// 如果后面后方括号、后花括号，则不换行
				if ((i + 1 < length) && (json.charAt(i + 1) == ']' || json.charAt(i + 1) == '}')) {
				} else {
					result.append('\n');
					// （4）每出现一次前方括号、前花括号；缩进次数增加一次。打印：新行缩进。
					number++;
					result.append(indent(number));
				}

				// （5）进行下一次循环。
				continue;
			}

			// 3、如果当前字符是后方括号、后花括号做如下处理：
			if ((key == ']') || (key == '}')) {
				// （1）后方括号、后花括号，的前面必须换行。打印：换行。
				// 如果前面前方括号、前花括号，则不换行
				if ((i - 1 > 0) && (json.charAt(i - 1) == '[' || json.charAt(i - 1) == '{')) {
				} else {
					result.append('\n');
					// （2）每出现一次后方括号、后花括号；缩进次数减少一次。打印：缩进。
					number--;
					result.append(indent(number));
				}

				// （3）打印：当前字符。
				result.append(key);

				// （4）如果当前字符后面还有字符，并且字符不为“，”，打印：换行。
				/*
				 * if (((i + 1) < length) && (json.charAt(i + 1) != ',')) { result.append('\n'); }
				 */

				// （5）继续下一次循环。
				continue;
			}

			// 4、如果当前字符是逗号。逗号后面换行，并缩进，不改变缩进次数。
			if ((key == ',')) {
				result.append(key);
				result.append('\n');
				result.append(indent(number));
				continue;
			}

			// 5、“：”,如果后面有引号都加一个空格。
			if ((key == ':')&&(((i + 1) < length)&&(json.charAt(i + 1) == '"'))) {
				result.append(key);
				result.append(' ');
				continue;
			}

			// 6、打印：当前字符。
			result.append(key);
		}

		return result.toString();
	}

	/**
	 * 返回指定次数的缩进字符串。每一次缩进三个空格，即SPACE。
	 * 
	 * @param number
	 *            缩进次数。
	 * @return 指定缩进次数的字符串。
	 */
	private static String indent(int number) {
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < number; i++) {
			result.append(SPACE);
		}
		return result.toString();
	}

}
