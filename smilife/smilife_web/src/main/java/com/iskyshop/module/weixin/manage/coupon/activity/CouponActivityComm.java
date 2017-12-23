package com.iskyshop.module.weixin.manage.coupon.activity;

import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.logging.SimpleFormatter;

import javax.net.ssl.HttpsURLConnection;




import org.apache.log4j.Logger;



public class CouponActivityComm {
	private static Logger logger = Logger.getLogger(CouponActivityComm.class);
	private static Map<String,String> weixiMap=new HashMap<String, String>();
	/***
	 * 根据key 获取value
	 * @param key
	 * @return
	 */
	public static String getPropertiesByKey(String key){
		InputStream is = null;
		Properties properties = null;
		String value="";
		try {
        	properties = new Properties();
        	is = CouponActivityComm.class.getResourceAsStream("/couponActivity.properties");
        	properties.load(is);
        	value=properties.getProperty(key);
        }
        catch (Exception e) {
        	logger.error("获取couponActivity.properties失败");
        }
        return value;
	}
	
	/***
	 * 获取当前日期
	 * @return
	 */
	public static String getCurDate(){
		Date date=new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(date);
	}
	
	/***
	 * 获取时间
	 * @param format
	 * @return
	 */
	public static String getCurTime(String format){
		Date date=new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}
	
	/***
	 * 获取随机数
	 * @param range
	 * @return
	 */
	public static int getRandomNum(int range){
		if(range==0){
			return range;
		}
		Random random = new Random();
		return random.nextInt(range);
	}
	
	/***
	 * 是否在活动时间
	 * @return
	 */
	public static boolean isCouponActivityTime(){
		boolean flag=false;
		String startTime=getPropertiesByKey("coupon.activity.time.start");
		String endTime=getPropertiesByKey("coupon.activity.time.end");
		String curTime=getCurTime("yyyy-MM-dd HH:mm:ss");
		if(startTime.compareTo(curTime)<=0 && endTime.compareTo(curTime)>=0){
			flag=true;
		}
		return flag;
	}
	
	/***
	 * 调用获取信息
	 * @param dataXml
	 * @param reqUrl
	 * @return
	 */
	public static String sendHttp(String dataXml,String reqUrl){
    	String retStr="";
    	BufferedOutputStream out=null;
    	HttpsURLConnection conn=null;
		try {
			URL url = new URL(reqUrl);
			conn = (HttpsURLConnection) url.openConnection();
			// 以post方式通信
			conn.setRequestMethod("GET");
	        //设置连接超时时间
			conn.setConnectTimeout(60000);
			//不使用缓存
			conn.setUseCaches(false);
		    //允许输入输出
			conn.setDoInput(true);
			conn.setDoOutput(true);
            
			conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
			conn.connect();  
			out = new BufferedOutputStream(conn.getOutputStream());
            out.write(dataXml.getBytes()); //写入请求的字符串  
	        out.flush();  
	        
	        int responseCode = conn.getResponseCode();
		    if(responseCode==200){
				InputStream inputStream = conn.getInputStream();
				byte[] retData = new byte[inputStream.available()];  
			    inputStream.read(retData);  
			    retStr = new String(retData);  
		    }
				
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				out.close(); 
				conn.disconnect();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return retStr;
    }
	
	
}
