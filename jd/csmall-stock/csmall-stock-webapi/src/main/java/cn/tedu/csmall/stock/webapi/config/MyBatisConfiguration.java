package cn.tedu.csmall.stock.webapi.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
// MyBatis框架扫描mapper接口包的注解
@MapperScan("cn.tedu.csmall.stock.webapi.mapper")
public class MyBatisConfiguration {
}
