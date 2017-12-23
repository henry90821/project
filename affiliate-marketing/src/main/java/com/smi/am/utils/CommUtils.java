package com.smi.am.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;

import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;

/**
 * 
 * @author dengyuqi
 *
 */
public class CommUtils {

	/**
	 * 
	 * @param code 响应码
	 * @param msg 响应码描述
	 * @param data 响应数据
	 * @return
	 */
	public static Map<String,Object> buidResult(Integer code,String msg,Object data){
		Map<String,Object> result =new HashMap<String,Object>();
		result.put("code", code);
		result.put("msg", msg);
		result.put("entity", data);
		return result;
	}
	/**
	 * 通过properties文件名和对应的Key，获取值
	 * @author herendian
	 * @version 1.0
	 * @date 2016年3月21日 上午11:11:59
	 * @param propertiesFile   文件名称
	 * @param key   需要获取的值 key
	 * @return
	 * @throws Exception
	 */
	public static String getProperties(String propertiesFile,String key)throws Exception{
		InputStream is = null;
		Properties properties = null;
		String value="";
		try {
        	properties = new Properties();
        	is = CommUtils.class.getResourceAsStream("/"+propertiesFile+".properties");
        	properties.load(is);
        	value=(String)properties.getProperty(key);
        }catch (Exception e) {
        	throw e;
        }finally {
			is.close();
		}
		return value;
	}
	/**
	 * 获取properties对象
	 * @author herendian
	 * @version 1.0
	 * @date 2016年3月21日 上午11:15:15
	 * @param propertiesFile
	 * @return
	 * @throws Exception
	 */
	public static Properties getProperties(String propertiesFile)throws Exception{
		InputStream is = null;
		Properties properties = null;
		try {
        	properties = new Properties();
        	is = CommUtils.class.getResourceAsStream("/"+propertiesFile+".properties");
        	properties.load(is);
        }catch (Exception e) {
        	throw e;
        }finally {
			is.close();
		}
		return properties;
	}
	
	
	/**
	 * 把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
	 * 
	 * @param params
	 *            需要排序并参与字符拼接的参数组
	 * @return 拼接后字符串
	 */
	public static String createLinkString(Map<String, Object> params) {
		List<String> keys = new ArrayList<String>(params.keySet());
		Collections.sort(keys);
		StringBuffer prestr=new StringBuffer();
		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			String value = params.get(key)+"";
			if (i == keys.size() - 1) {// 拼接时，不包括最后一个&字符
				prestr.append(key + "=" + value);
			} else {
				prestr.append(key + "=" + value+"&");
			}
		}
		return prestr.toString();
	}
	
	/**
     * 向指定 URL 发送POST方法的请求
     * @author chuzhisheng
	 * @version 1.0
     * @param url 发送请求的 URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendPostUrl(String url, String param)
    {
        PrintWriter out = null;
        BufferedReader in = null;
        StringBuffer result = new StringBuffer();
        try
        {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null)
            {
                result.append(line);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally
        {
            try
            {
                if (out != null)
                {
                    out.close();
                }
                if (in != null)
                {
                    in.close();
                }
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
            }
        }
        return result.toString();
    }

    /**  
     * POST请求，Map形式数据  
     * @author chuzhisheng
	 * @version 1.0
     * @param url 请求地址  
     * @param param 请求数据  
     * @param charset 编码方式  
     * @throws IOException 
     * @throws NoSuchAlgorithmException 
     * @throws KeyManagementException 
     */  
	public static String sendPost(String url, Map<String, Object> param,  
            String charset) throws IOException, NoSuchAlgorithmException, KeyManagementException {  
			String result = "";  
			HttpURLConnection conn = null;
	        if(!StringUtils.isEmpty(url)&&url.startsWith("https")){
	        	URL urlStr = new URL(url);
	            HttpsURLConnection connect = (HttpsURLConnection) urlStr.openConnection();
	            connect.setHostnameVerifier(new IgnoreHostnameVerifier());
	            TrustManager[] tm = { new IgnoreCertificationTrustManger() };
	            SSLContext sslContext = SSLContext.getInstance("TLS");
	            sslContext.init(null, tm, null);
	            SSLSocketFactory ssf = sslContext.getSocketFactory();
	            connect.setSSLSocketFactory(ssf);
	            conn=connect;
	        }else{ 
	        	 URL realUrl = new URL(url); 
	        	 // 打开和URL之间的连接  
		         conn = (HttpURLConnection) realUrl.openConnection();  
	        }
	        PrintWriter out = null;  
	        BufferedReader in = null;  
	        try {  
	            // 设置通用的请求属性  
	            conn.setRequestProperty("accept", "*/*"); 
	            conn.setRequestProperty("Content-Type", "application/json"); 
	            conn.setRequestProperty("connection", "Keep-Alive");  
	            conn.setRequestProperty("user-agent",  
	                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");  
	            // 发送POST请求必须设置如下两行  
	            conn.setDoOutput(true);  
	            conn.setDoInput(true);  
	            // 获取URLConnection对象对应的输出流  
	            out = new PrintWriter(conn.getOutputStream());  
	            // 发送请求参数  
	            out.print(JSON.toJSONString(param));  
	            // flush输出流的缓冲  
	            out.flush();  
	            // 定义BufferedReader输入流来读取URL的响应  
	            in = new BufferedReader(new InputStreamReader(  
	                    conn.getInputStream(), charset));  
	            String line;  
	            while ((line = in.readLine()) != null) {  
	                result += line;  
	            }  
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        }  
	        // 使用finally块来关闭输出流、输入流  
	        finally {  
	            try {  
	                if (out != null) {  
	                    out.close();  
	                }  
	                if (in != null) {  
	                    in.close();  
	                }  
	            } catch (IOException ex) {  
	                ex.printStackTrace();  
	            }  
	        }  
        return result;  
    } 
    public static class IgnoreHostnameVerifier implements HostnameVerifier{
    	public boolean verify(String arg0, SSLSession arg1) {
    	                return true;
    	}
    }
    public static class IgnoreCertificationTrustManger implements X509TrustManager {
	    	private X509Certificate[] certificates;
	    	public void checkClientTrusted(X509Certificate certificates[],
	    	                        String authType) throws CertificateException {
	    	                if (this.certificates == null) {
	    	                        this.certificates = certificates;
	    	                }
	    	}
	    	public void checkServerTrusted(X509Certificate[] ax509certificate, String s)
	    	                        throws CertificateException {
	    	                if (this.certificates == null) {
	    	                        this.certificates = ax509certificate;
	    	                }
	    	        }
			@Override
			public void checkClientTrusted(
					java.security.cert.X509Certificate[] chain, String authType)
					throws java.security.cert.CertificateException {
			}
			@Override
			public void checkServerTrusted(
					java.security.cert.X509Certificate[] chain, String authType)
					throws java.security.cert.CertificateException {
			}
			@Override
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}
    }
}
