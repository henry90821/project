package com.iskyshop.foundation.events.handler;

import com.iskyshop.core.beans.exception.CallException;
import com.iskyshop.core.tools.SpringUtils;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.foundation.domain.GoodsConfig;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.domain.OrderSyn;
import com.iskyshop.foundation.domain.ShipAddress;
import com.iskyshop.foundation.events.SynchronizeOrderEvent;
import com.iskyshop.foundation.events.utils.ThreadUtil;
import com.iskyshop.foundation.service.IGoodsConfigService;
import com.iskyshop.foundation.service.IOrderDistributeService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.IOrderSynService;
import com.iskyshop.foundation.service.IShipAddressService;
import com.tydic.framework.util.PropertyUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 同步订单事件处理类<br/>
 * Created by 亚翔 on 2015/11/13.
 */
@Component
public class SynchronizeOrderHandler implements ApplicationListener<SynchronizeOrderEvent> {
    private final Logger LOGGER = Logger.getLogger(this.getClass());

    @Autowired
    private IOrderFormService orderFormService;
    @Autowired
    private IOrderSynService orderSynService;
    @Autowired
    private IShipAddressService shipAddressService;
    @Autowired
    private IGoodsConfigService goodsConfigService;

    @Async
    @Override
    public void onApplicationEvent(SynchronizeOrderEvent synchronizeOrderEvent) {
        // 获取当前线程名
        String threadName = Thread.currentThread().getName();
        // 获取当前线程信息
        String threadStr = Thread.currentThread().toString();
        LOGGER.info("===================================开始——处理待同步订单业务" + threadStr + "===================================");
        if (ThreadUtil.isSyncOrderExecutor(threadName)) {
            // 从触发的事件中获得订单ID值
            Long id = synchronizeOrderEvent.getOrderId();
            LOGGER.info("获得到的订单主键ID值为：" + id);
            // 获得待同步订单数据集合
            List<OrderForm> orderFormList = this.orderFormService.getUnSynchronizeOrders(id);
            // 处理需要同步的订单数据
            if (!StringUtils.isNullOrEmpty(orderFormList)) {
                this.addToSyncList(orderFormList);
            } else {
                LOGGER.warn("没有需要同步的订单数据！");
            }
        } else {
            LOGGER.warn("当前线程不是合法的指定执行器线程，不允许进行业务逻辑处理！");
        }
        LOGGER.info("===================================结束——处理待同步订单业务" + threadStr + "===================================");
    }

    /**
     * 同步处理需要同步的订单数据
     *
     * @param orderFormList 需要同步的订单列表数据
     */
    private synchronized void addToSyncList(List<OrderForm> orderFormList) {
        Boolean result = false;
        List<OrderSyn> orderSynList = null;
        List<String> syncOrderIdList = null;

        if (!StringUtils.isNullOrEmpty(orderFormList)) {
            orderSynList = new ArrayList<OrderSyn>();
            syncOrderIdList = new ArrayList<String>();
            // 将参数列表中的集合数据添加到OrderSyn表中。
            for (OrderForm orderForm : orderFormList) {
                // 验证是否已经存在相同数据
                Map<String, Object> existParams = new HashMap<>();
                existParams.put("deleteStatus", 0);
                existParams.put("orderId", orderForm.getId());
                List<OrderSyn> existOrderSynList = this.orderSynService.query(
                        "FROM OrderSyn WHERE order_id=:orderId and deleteStatus=:deleteStatus", existParams, -1, -1);
                if (StringUtils.isNullOrEmpty(existOrderSynList)) {
                    OrderSyn orderSyn = this.getOrderSyn(orderForm, null);
                    orderSynList.add(orderSyn);
                    syncOrderIdList.add(orderForm.getOrder_id());
                    // 判断如果是海信商品则需要增加一条订单APP同步的记录
                    if (GoodsConfig.CODE_HX.equals(orderSyn.getGoodsConfig().getConfigCode())) {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("configCode", GoodsConfig.CODE_DDAPP);
                        List<GoodsConfig> goodsConfigList = this.goodsConfigService.query(
                                "FROM GoodsConfig WHERE configCode=:configCode", params, -1, -1);
                        if (!StringUtils.isNullOrEmpty(goodsConfigList) || goodsConfigList.size() > 1) {
                            GoodsConfig goodsConfig = goodsConfigList.get(0);
                            OrderSyn appOrderSyn = this.getOrderSyn(orderForm, goodsConfig);
                            orderSynList.add(appOrderSyn);
                        } else {
                            LOGGER.error("因为查询到的GoodsConfig（DataSource=" + ArrayUtils.toString(goodsConfigList.toArray())
                                    + "）数据异常而无法创建订单APP同步OrderSyn数据！");
                        }
                    }
                } else {
                    LOGGER.warn("已经存在主键值为" + orderForm.getId() + "的待同步订单，订单号为" + orderForm.getOrder_id());
                }
            }
            result = this.orderSynService.batchSave(orderSynList);
            if (result) {
                LOGGER.info(ArrayUtils.toString(syncOrderIdList) + "订单已经成功加入待同步列表！");
            } else {
                LOGGER.error(ArrayUtils.toString(syncOrderIdList) + "订单加入待同步列表失败！");
            }
        }
    }

    /**
     * 通过{@link OrderForm}对象获得{@link OrderSyn}对象
     *
     * @param orderForm
     * @param goodsConfig
     * @return
     */
    private OrderSyn getOrderSyn(OrderForm orderForm, GoodsConfig goodsConfig) {
        OrderSyn orderSyn = null;
        String orderPushMsg = null;
        String aliasName = null;

        try {
            orderSyn = new OrderSyn();
            // 关联订单对象
            orderSyn.setOrderForm(orderForm);
            // 关联商品配置对象
            orderSyn.setGoodsConfig(StringUtils.isNullOrEmpty(goodsConfig) ? orderForm.getGoodsConfig() : goodsConfig);
            // 设置数据添加时间
            orderSyn.setAddTime(new Date());
            // 查询获得订单所属店铺信息
            ShipAddress shipAddress = this.shipAddressService.getObjById(orderForm.getShip_addr_id());
            // 从配置文件中获取需要实例化的接口实现类别名
            aliasName = StringUtils.isNullOrEmpty(goodsConfig) ? PropertyUtil.getProperty(orderForm.getGoodsConfig()
                    .getConfigCode()) : PropertyUtil.getProperty(goodsConfig.getConfigCode());
            // 根据每个OrderSyn中对应的GoodsConfig进行动态实例化接口来调用
            IOrderDistributeService orderDistributeService = (IOrderDistributeService) SpringUtils.getContext().getBean(
                    aliasName);
            try {
                // 调用借口获得对应同步系统的报文
                orderPushMsg = orderDistributeService.createPushMessage(orderForm, StringUtils.isNullOrEmpty(shipAddress) ? null
                        : shipAddress.getSa_code());
            } catch (CallException e) {
                LOGGER.error("调用" + IOrderDistributeService.class.toString() + "接口的实现类" + aliasName + "创建分发报文时出错！", e);
            }
            orderSyn.setOrderInfo(orderPushMsg);
        } catch (BeansException e) {
            LOGGER.error("创建" + IOrderDistributeService.class.toString() + "接口的实现类出错！从配置中读取出来需要实现的子类别名为：" + aliasName, e);
        } catch (Exception e) {
            LOGGER.error("根据OrderForm对象创建OrderSyn对象时发生未知异常！", e);
        }
        return orderSyn;
    }
}
