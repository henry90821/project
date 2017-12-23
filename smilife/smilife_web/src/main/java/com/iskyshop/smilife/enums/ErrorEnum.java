package com.iskyshop.smilife.enums;


/**
 * 接口错误码枚举
 * @author  chuzhisheng
 */
public enum ErrorEnum { 
    SUCCESS("成功", 1),
    SYSTEM_ERROR("服务器内部错误", 2),
    LOGIN_INVALID("用户未登录", 3),
    USER_INCOMPLETE("用户资料未补全", 4),
    REQUEST_ERROR("请求错误", 5);
	
	/**案例------------begin-----------------------------------------*/
	/*public static void main(String[] args) {
		System.out.println(ErrorEnum.getDescr(1));
		System.out.println(ErrorEnum.getIndex("用户资料未补全"));
		System.out.println(ErrorEnum.UNKONW_ERROR.name());
		System.out.println(ErrorEnum.UNKONW_ERROR.getDescr());
		System.out.println(ErrorEnum.UNKONW_ERROR.getIndex());
	}*/
	/**案例------------end------------------------------------------*/
	
	
    // 成员变量  
    private String descr;  
    private int index;  
    // 构造方法  
    private ErrorEnum(String descr, int index) {  
        this.descr = descr;  
        this.index = index;  
    }  
    //通过索引获取描述
    public static String getDescr(int index) {  
        for (ErrorEnum c : ErrorEnum.values()) {  
            if (index==c.getIndex()) {  
                return c.descr;  
            }  
        }  
        return null;  
    } 
    //通过描述获取索引
    public static int getIndex(String descr) { 
    	if(descr==null)descr="";
        for (ErrorEnum c : ErrorEnum.values()) { 
            if (descr.equals(c.descr)) {  
                return c.index;  
            }  
        }  
        return 0;  
    }
    // get set 方法  
    public String getDescr() {  
        return descr;  
    }  
    public void setDescr(String descr) {  
        this.descr = descr;  
    }  
    public int getIndex() {  
        return index;  
    }  
    public void setIndex(int index) {  
        this.index = index;  
    }  
}
