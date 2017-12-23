package com.iskyshop.smilife.enums;


/**
 * 支付中心，支付类型
 * @author chuzhisheng
 * @version 1.0
 * @date 2016年4月11日 下午1:52:03
 */
public enum PayTypeEnum {
	ALI("支付宝"),
	WX("微信"),
	YE("余额"),
	UN("银联"),
	YP("影票"),
	SBH("随变花");
   
    public String descr;
    
    private PayTypeEnum(String descr)
    {
        this.descr = descr;
    }
    
    /**
     * 获取中文名称.
     * 
     */
    public String getDescr()
    {
        return descr;
    }
    
    /**
     * 根据枚举名获取对应的枚举值。若失败，则返回null
     * @param value 枚举名
     * @return
     */
    public static final PayTypeEnum parse(String value)
    {
        try
        {
            return PayTypeEnum.valueOf(value);
        }
        catch (Throwable t)
        {
            return null;
        }
    }
    
}
