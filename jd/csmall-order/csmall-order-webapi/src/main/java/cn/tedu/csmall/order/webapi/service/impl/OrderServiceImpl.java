package cn.tedu.csmall.order.webapi.service.impl;


import cn.tedu.csmall.cart.service.ICartService;
import cn.tedu.csmall.commons.pojo.order.dto.OrderAddDTO;
import cn.tedu.csmall.commons.pojo.order.model.OrderTb;
import cn.tedu.csmall.commons.pojo.stock.dto.StockReduceCountDTO;
import cn.tedu.csmall.order.service.IOrderService;
import cn.tedu.csmall.order.webapi.mapper.OrderMapper;
import cn.tedu.csmall.stock.service.IStockService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@DubboService
@Service
@Slf4j
public class OrderServiceImpl implements IOrderService {
    @Autowired
    private OrderMapper orderMapper;

//    @DubboReference表示這個註解是利用Dubbo來調用的
    @DubboReference
    private IStockService dubboStockService;
//    同上
    @DubboReference
    private ICartService dubboCartService;

    @Override
    public void orderAdd(OrderAddDTO orderAddDTO) {

        // 1.减少订单商品的库存数(要调用stock模块的方法)
        StockReduceCountDTO stockReduceCountDTO = new StockReduceCountDTO();
        stockReduceCountDTO.setCommodityCode(orderAddDTO.getCommodityCode());
        stockReduceCountDTO.setReduceCount(orderAddDTO.getCount());
        dubboStockService.reduceCommodityCount(stockReduceCountDTO);
        // 2.删除订单中勾选的购物车中的商品(要调用cart模块的方法)
        //故意拋出異常
        System.out.println(1/0);
        dubboCartService.deleteUserCart(orderAddDTO.getUserId(),orderAddDTO.getCommodityCode());

        // 3.新增订单
        OrderTb order=new OrderTb();
        BeanUtils.copyProperties(orderAddDTO,order);
        // 执行新增
        orderMapper.insertOrder(order);
        log.info("新增的订单信息:{}",order);
    }
}
