package cn.tedu.csmall.order.webapi.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

// 所有Spring相关的配置类都需要添加下面注解
@Configuration
// 扫描commons模块的统一异常处理类,使其在当前项目中生效
@ComponentScan("cn.tedu.csmall.commons.exception")
public class CommonsConfiguration {
}