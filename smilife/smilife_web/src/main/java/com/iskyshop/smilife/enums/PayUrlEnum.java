package com.iskyshop.smilife.enums;
/**
 * 支付中心接口地址
 * @author chuzhisheng
 * @version 1.0
 * @date 2016年4月7日 下午6:31:07
 */
public enum PayUrlEnum {
	BILL_URL("支付中心下单接口地址", "payplatform/rest/bill.do"),
	REFUND_URL("支付中心退款接口地址", "payplatform/rest/refund.do"),
	QUERY_ORDER_URL("支付中心订单查询接口地址","payplatform/rest/querybills.do"),
	ORDER_COUNT_URL("支付中心订单总数查询接口地址","payplatform/rest/countbills.do"),
	QUERY_REFUND_URL("支付中心查询退款接口地址","payplatform/rest/queryrefunds.do"),
	REFUND_COUNT_URL("支付中心退款订单总数查询接口地址","payplatform/rest/countrefunds.do");
    // 成员变量  
    private String descr;  
    private String index;  
    // 构造方法  
    private PayUrlEnum(String descr, String index) {  
        this.descr = descr;  
        this.index = index;  
    }  
    //通过索引获取描述
    public static String getDescr(String index) {  
        for (PayUrlEnum c : PayUrlEnum.values()) {  
            if (index.equals(c.getIndex())) {  
                return c.descr;  
            }  
        }  
        return null;  
    } 
    //通过描述获取索引
    public static String getIndex(String descr) { 
    	if(descr==null)descr="";
        for (PayUrlEnum c : PayUrlEnum.values()) { 
            if (descr.equals(c.descr)) {  
                return c.index;  
            }  
        }  
        return "";  
    }
    // get set 方法  
    public String getDescr() {  
        return descr;  
    }  
    public void setDescr(String descr) {  
        this.descr = descr;  
    }  
    public String getIndex() {  
        return index;  
    }  
    public void setIndex(String index) {  
        this.index = index;  
    } 

}
