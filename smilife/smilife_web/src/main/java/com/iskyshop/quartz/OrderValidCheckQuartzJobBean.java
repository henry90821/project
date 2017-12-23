package com.iskyshop.quartz;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.ISysConfigService;

/**
 * 
 * @ClassName: OrderValidCheckQuartzJobBean
 * @Description: TODO(检查订单是否超过所设置的时效)
 * @author wangyun
 * @date 2015-10-9
 * 
 */
public class OrderValidCheckQuartzJobBean extends QuartzJobBean {
	private Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private IGoodsService goodsService;
	private static Map params = new HashMap();
	/**
	 * 重写定时方法
	 */
	@Override
	protected void executeInternal(JobExecutionContext arg0) throws JobExecutionException {
		logger.info("订单时效检查定时器执行......start");
		SysConfig config = this.configService.getSysConfig();
		if(!StringUtils.isNullOrEmpty(config)){
			int time_hour = config.getPayOrderTime(); //获取的单位为小时
			params.put("order_status", 10);//状态为10是未支付的订单
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.HOUR_OF_DAY,-time_hour);
//			params.put("addTime",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cal.getTime()));
			params.put("addTime",cal.getTime());
//			String query="SELECT * FROM ISKYSHOP_ORDERFORM T WHERE HOUR(TIMEDIFF(NOW(),T.`ADDTIME`)) >"+time_hour+" AND T.`ORDER_STATUS`='10'";
//			List<OrderForm> orderForms=this.orderFormService.query(query,null, -1, -1);
			String query="select obj from OrderForm obj where obj.order_status=:order_status and obj.addTime<=:addTime";
			List<OrderForm> orderForms=this.orderFormService.query(query,params, -1, -1);
			for (OrderForm orderForm : orderForms) {
				this.goodsService.order_cancel(orderForm);//修改订单状态
				this.goodsService.recover_goods_inventory(orderForm, null);//还原商品库存
			}
		}
		params.clear();
		logger.info("订单时效检查定时器执行......end");
	}
}
