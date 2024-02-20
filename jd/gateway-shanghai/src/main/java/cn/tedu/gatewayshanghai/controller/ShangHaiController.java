package cn.tedu.gatewayshanghai.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sh")
public class ShangHaiController {

    @GetMapping("/show")
    public String show()    {
        return "Welcome to ShangHai";
    }
}
