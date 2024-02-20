package cn.tedu.csmall.business.service.impl;

import cn.tedu.csmall.business.service.IBusinessService;
import cn.tedu.csmall.commons.exception.CoolSharkServiceException;
import cn.tedu.csmall.commons.pojo.order.dto.OrderAddDTO;
import cn.tedu.csmall.commons.restful.ResponseCode;
import cn.tedu.csmall.order.service.IOrderService;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BusinessServiceImpl implements IBusinessService {

    @DubboReference
    private IOrderService dubboOrderService;

    //添加了@GlobalTransactional註解 就意味者這方法被seata管理
    @GlobalTransactional
    @Override
    public void buy() throws InterruptedException {
        // 模拟购买业务
        // 创建一个OrderAddDTO类,并为它赋值
        OrderAddDTO orderAddDTO=new OrderAddDTO();
        orderAddDTO.setUserId("UU100");
        orderAddDTO.setCommodityCode("PC100");
        orderAddDTO.setCount(5);
        orderAddDTO.setMoney(500);
        // 模拟购买只是输出到控制台即可
        log.info("新增订单的信息为:{}",orderAddDTO);

        // 调用order业务
        dubboOrderService.orderAdd(orderAddDTO);
        log.info("远程调用order模块成功！！");
        //設置60%的概念進入延遲
        if(Math.random()<.6){
            Thread.sleep(1000);
        }

        //故意針對TM出異常做出測試 看事務是否rollback
        if(Math.random()<0.7){
            throw new CoolSharkServiceException(ResponseCode.NOT_FOUND,"找不到服務");
        }
    }
}