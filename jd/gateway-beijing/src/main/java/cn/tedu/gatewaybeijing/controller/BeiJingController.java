package cn.tedu.gatewaybeijing.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bj")
public class BeiJingController {

    @GetMapping("/show")
    public String show(/*@RequestParam("age") Integer age, @RequestHeader("request-color") String requestColor*/) throws InterruptedException {
        //請求500毫秒才能返回訊息(模擬處理耗時操作
        Thread.sleep(500);
        return "Welcome to Beijing"+"age"/*+age+", color="+requestColor*/;
    }
}
