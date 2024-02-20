package cn.tedu.csmall.cart.webapi;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//如果當前項目是Dubbo的提供者 需要添加這個註解 否則當前項目無法被發現
@EnableDubbo
@SpringBootApplication
public class CsmallCartWebapiApplication {

    public static void main(String[] args) {
        SpringApplication.run(CsmallCartWebapiApplication.class, args);
    }

}
