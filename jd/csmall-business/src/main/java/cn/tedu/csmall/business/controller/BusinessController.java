package cn.tedu.csmall.business.controller;

import cn.tedu.csmall.business.service.IBusinessService;
import cn.tedu.csmall.commons.restful.JsonResult;
import cn.tedu.csmall.commons.restful.ResponseCode;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/base/business")
// knife4j控制器介绍
@Api(tags = "购买业务开始")
public class BusinessController {

    @Autowired
    private IBusinessService businessService;

    @PostMapping("/buy")
    // localhost:20000/base/business/buy
    @ApiOperation("发起购买的方法")
    @SentinelResource(value = "business-sentinel",fallback = "fallbackError")
    public JsonResult buy() throws InterruptedException {
        // 调用业务逻辑层方法
        businessService.buy();
        return JsonResult.ok("购买完成!");
    }


    public JsonResult fallbackError(){
        return JsonResult.failed(ResponseCode.INTERNAL_SERVER_ERROR,"business降級提示");
    }

}
