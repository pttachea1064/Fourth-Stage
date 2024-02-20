package cn.tedu.csmallsso.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Slf4j
public class WebMvcConfiguration implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry){
        log.info("使用跨區域配置類:WebMvcConfiguration");
        registry.addMapping("/**") //設置允許跨域的路徑
                .allowedHeaders("*")
                .allowedMethods("*")  //設置允許跨域的請求方式類型
                .allowedOriginPatterns("*")  //這是允許跨域的名稱地址
                .allowCredentials(true)//允許證書
                .maxAge(3600);//設置跨域允許時間
    }
}
