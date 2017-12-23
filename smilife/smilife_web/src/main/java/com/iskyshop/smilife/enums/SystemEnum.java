package com.iskyshop.smilife.enums;

/**
 * 系统模块枚举
 * @author chuzhisheng
 * @version 1.0
 * @date 2016年4月6日 下午2:50:33
 */
public enum SystemEnum {
	SMI_MOVIE("SMI_MOVIE#NEW2016", "10003"),
	SMI_LIFE("SMI_LIFE#NEW2016", "10002"),
	SMI_MALL("SMI_MALL#NEW2016", "10004");
    // 成员变量  
    private String descr;  
    private String index;  
    // 构造方法  
    private SystemEnum(String descr, String index) {  
        this.descr = descr;  
        this.index = index;  
    }  
    //通过索引获取描述
    public static String getDescr(String index) {  
        for (SystemEnum c : SystemEnum.values()) {  
            if (index.equals(c.getIndex())) {  
                return c.descr;  
            }  
        }  
        return null;  
    } 
    //通过描述获取索引
    public static String getIndex(String descr) { 
    	if(descr==null)descr="";
        for (SystemEnum c : SystemEnum.values()) { 
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
