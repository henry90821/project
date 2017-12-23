package com.iskyshop.smilife.common;

import java.util.HashMap;

import com.iskyshop.smilife.enums.ErrorEnum;

/**
 * 接口返回结果实体类
 * 使用案例：Result r=new Result();
		   r.set(OrderStatus.BE_PAYMENT);
		   r.setData("测试数据"); 
      1、 实体set方法现已支持添加其他业务枚举，但其他枚举必需和ErrorEnum一样包含
         getIndex和getDescr这两个方法，以便该实体可以通过反射获取对应code和msg
      2、实体中重载的set方法可以动态传入对应的方法
 * @author hanhua
 * @version 1.0
 * @date 2016年3月21日 下午2:11:09
 */
public class Result extends HashMap<String, Object> {
    private static final long serialVersionUID = 2546281331124443628L;
    
	/**案例------------begin-----------------------------------------*/
/*	public static void main(String[] args) {
		Result r1=new Result();
		r1.set(OrderStatus.BE_PAYMENT);
		r1.setData("测试数据");
		System.out.println(r1.getMsg()+"|----|"+r1.getCode()+"|-----|"+r1.getData());
		
		Result r2=new Result();
		r2.set(OrderStatus.BE_PAYMENT,"getIndex","getDescr");
		r2.setData("测试数据");
		System.out.println(r2.getMsg()+"|----|"+r2.getCode()+"|-----|"+r2.getData());
	}*/
	/**案例------------end------------------------------------------*/
	
    public Result() {
        this.setData(null);
        this.set(ErrorEnum.SUCCESS);
    }

    /**
     * 传人枚举对象，通过反射默认获取getIndex和getDescr方法设置对应参数
     * 如果使用的枚举方法名不是这个两个，可以使用重载的set的方法将对应方法名传入
     * @author chuzhisheng
     * @version 1.0
     * @date 2016年3月31日 下午2:17:24
     * @param enumObj
     */
    public Result set(Object enumObj) {
    	try{
          this.set(enumObj, "getIndex", "getDescr");
    	}catch(Exception e){
    	  this.put("code", ErrorEnum.SYSTEM_ERROR.getIndex());
          this.put("msg", ErrorEnum.SYSTEM_ERROR.getDescr());
    	}
    	return this;
    }
    /**
     * 添加set重载方法，兼容枚举结构和ErrorEnum不同的枚举，
     * 传入的getIndex为获取索引的方法名，
     * getDescr为获取描述的方法名
     * @author chuzhisheng
     * @version 1.0
     * @date 2016年3月31日 下午2:13:05
     * @param enumObj
     * @param getIndex
     * @param getDescr
     */
    public Result set(Object enumObj,String getIndex,String getDescr) {
    	try{
          this.put("code", enumObj.getClass().getMethod(getIndex).invoke(enumObj));
          this.put("msg", enumObj.getClass().getMethod(getDescr).invoke(enumObj));
    	}catch(Exception e){
    	  this.put("code", ErrorEnum.SYSTEM_ERROR.getIndex());
          this.put("msg", ErrorEnum.SYSTEM_ERROR.getDescr());
    	}
    	return this;
    }

    public Result setCode(String code) {
          this.put("code", code);
          return this;
    }
    
    public Result setMsg(String msg) {
          this.put("msg",msg);
          return this;
    }
    
    public String getCode() {
        return this.get("code").toString();
    }

    public String getMsg() {
        return this.get("msg").toString();
    }

    public Object getData() {
        return this.get("entity");
    }

    public Result setData(Object data) {
        this.put("entity", data);
        return this;
    }
}
