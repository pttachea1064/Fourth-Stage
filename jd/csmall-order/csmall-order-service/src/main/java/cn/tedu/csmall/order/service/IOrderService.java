package cn.tedu.csmall.order.service;

import cn.tedu.csmall.commons.pojo.order.dto.OrderAddDTO;

public interface IOrderService {

    // 新增订单的业务逻辑层方法声明
    void orderAdd(OrderAddDTO orderAddDTO);


}