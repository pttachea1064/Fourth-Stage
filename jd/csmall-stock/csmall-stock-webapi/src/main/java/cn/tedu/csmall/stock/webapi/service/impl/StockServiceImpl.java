package cn.tedu.csmall.stock.webapi.service.impl;

import cn.tedu.csmall.commons.pojo.stock.dto.StockReduceCountDTO;
//import cn.tedu.csmall.stock.service.IStockService;
import cn.tedu.csmall.stock.service.IStockService;
import cn.tedu.csmall.stock.webapi.mapper.StockMapper;
import lombok.extern.slf4j.Slf4j;
//import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

// @DubboService注解标记的业务逻辑层实现类,其中所有方法都会注册到Nacos
// 其它服务在订阅时会发现当前项目提供的业务逻辑层方法,以备Dubbo调用
@DubboService
@Service
@Slf4j
public class StockServiceImpl implements IStockService {
    @Autowired
    private StockMapper stockMapper;

    @Value("${server.port}")
    private int port;

    @Override
    public void reduceCommodityCount(StockReduceCountDTO stockReduceCountDTO) {
        // 调用持久层减少库存的方法
        // 参数1:商品编号
        // 参数2:减少的库存数
        stockMapper.updateStockByCommodityCode(
                stockReduceCountDTO.getCommodityCode(),
                stockReduceCountDTO.getReduceCount());
        log.info("库存减少完成!"+port+"服務器");
    }
}
