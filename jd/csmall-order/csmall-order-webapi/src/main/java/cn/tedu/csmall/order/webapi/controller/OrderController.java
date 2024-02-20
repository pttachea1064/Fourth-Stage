package cn.tedu.csmall.order.webapi.controller;

import cn.tedu.csmall.commons.pojo.order.dto.OrderAddDTO;
import cn.tedu.csmall.commons.restful.JsonResult;
import cn.tedu.csmall.order.service.IOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/base/order")
@Api(tags = "订单管理")
public class OrderController {
    @Autowired
    private IOrderService orderService;

    @ApiOperation("新增订单")
    @PostMapping("/add")
    public JsonResult orderAdd(OrderAddDTO orderAddDTO){
        // 调用业务逻辑层方法
        orderService.orderAdd(orderAddDTO);
        return JsonResult.ok("新增订单完成!");
    }

}