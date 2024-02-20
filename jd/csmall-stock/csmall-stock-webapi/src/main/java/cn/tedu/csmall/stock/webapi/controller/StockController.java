package cn.tedu.csmall.stock.webapi.controller;

import cn.tedu.csmall.commons.pojo.stock.dto.StockReduceCountDTO;
import cn.tedu.csmall.commons.restful.JsonResult;
import cn.tedu.csmall.commons.restful.ResponseCode;
import cn.tedu.csmall.stock.service.IStockService;
import cn.tedu.csmall.stock.webapi.exception.BlockErrorHandler;
import cn.tedu.csmall.stock.webapi.exception.FallbackErrorHandler;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/base/stock")
@Api(tags = "库存管理")
public class StockController {

    @Autowired
    private IStockService stockService;

    @PostMapping("/reduce/count")
    @ApiOperation("减少商品库存数")
    //@SentinelResource注解描述的方法，会被sentinel进行管理
    //stock-reduce是仪表盘中资源名称
    @SentinelResource(value = "stock-reduce",
            blockHandlerClass = BlockErrorHandler.class,blockHandler = "blockError",
            fallbackClass = FallbackErrorHandler.class,fallback = "fallbackError"
    )
    public JsonResult reduceCommodityCount(StockReduceCountDTO stockReduceCountDTO){
        //模拟百分之五十的概率抛出异常
        if(Math.random()<0.5){
            System.out.println(1/0);
        }
        // 调用业务逻辑层
        stockService.reduceCommodityCount(stockReduceCountDTO);
        return JsonResult.ok("商品库存减少完成!");
    }

    //自定义限流方法
    /*
    访问修饰符：public
    返回值类型：需要和被限流的方法的返回值类型一致
    方法名称：和blockHandler的属性值一致
    参数列表：需要被限流的方法的参数列表一致、额外添加BlockException异常
     */
//    public JsonResult blockError(StockReduceCountDTO stockReduceCountDTO,
//                                 BlockException e){
//        //限流方法一般直接返回限流信息就可以了
//        return JsonResult.failed(ResponseCode.BAD_REQUEST,"服务器繁忙，请稍后重试~");
//    }
    //自定义降级方法
    /*
     访问修饰符：public
     返回值类型：需要和被降级的方法的返回值类型一致
     方法名称：和fallback的属性值一致
     参数列表：需要被降级的方法的参数列表一致，还可以添加Throwable异常
     */
//    public JsonResult fallbackError(StockReduceCountDTO stockReduceCountDTO){
//        //降级方法中，可以指定执行其他的操作，目前为止直接返回降级提示信息
//        System.out.println("模拟其他操作");
//        return JsonResult.failed(ResponseCode.INTERNAL_SERVER_ERROR,"方法被降级了");
//    }
}
