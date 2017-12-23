package com.iskyshop.smilife.enums;
/**
 * 订单状态枚举
 * @author chuzhisheng
 * @version 1.0
 * @date 2016年4月6日 下午2:34:29
 */
public enum OrderStatus {
	//订单状态，0为订单取消，10为已提交待付款，15为线下付款提交申请(已经取消该付款方式)，16为货到付款，20为已付款待发货，30为已发货待收货，35,自提点已经收货，40为已收货, 50买家评价完毕
    //65订单不可评价，到达设定时间，系统自动关闭订单相互评价功能，80为审核通过退款中,85退款已执行，87退款失败，90为退款完成
	CANCEL("已取消", 0),
    BE_PAYMENT("待付款", 10),
    PAY_APPLY("货到付款", 15),
    CASH_DELIVERY("待发货", 16),
    BE_SHIPPED("已付款", 20),
    INBOUND("已发货",30),
    POINT_BERECEIPTED("已收货",35),
    BERECEIPTED("待评价",40),
    EVALUATION_COMPLETED("已完成",50),
    CANNOT_ASSESSED("已结束",65),
    REFUNDING("退款中",80),
    REFUND_EXECUTION("退款中",85),
    REFUND_FAIL("退款失败",87),
    SUCCESS("退款完成",90);
    // 成员变量  
    private String descr;  
    private int index;  
    // 构造方法  
    private OrderStatus(String descr, int index) {  
        this.descr = descr;  
        this.index = index;  
    }  
    //通过索引获取描述
    public static String getDescr(int index) {  
        for (OrderStatus c : OrderStatus.values()) {  
            if (index==c.getIndex()) {  
                return c.descr;  
            }  
        }  
        return null;  
    } 
    //通过描述获取索引
    public static int getIndex(String descr) { 
    	if(descr==null)descr="";
        for (OrderStatus c : OrderStatus.values()) { 
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
