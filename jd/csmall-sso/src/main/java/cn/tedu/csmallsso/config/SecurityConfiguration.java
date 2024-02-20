package cn.tedu.csmallsso.config;

import cn.tedu.csmallsso.filter.JwtAuthorizationFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
//SpringBoot中的配置類需要添加@Configuration
@Slf4j
@EnableGlobalMethodSecurity(prePostEnabled = true) //開啟全局授權訪問檢查
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtAuthorizationFilter jwtAuthorizationFilter;

    //準備BCryptPasswordEncoder實際例子
    @Bean
    public PasswordEncoder passwordEncoder (){
        log.info("創建密碼編輯工具:BCryptPasswordEncoder");
        return new BCryptPasswordEncoder();
    }

    //提供一個AuthenticationManager對象
    @Bean
    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }


    //重寫一個Configure() 用於配置HTTP請求的安全性
    //可以通過調用HTTP物件上的方法
    //定義HTTP請求授權之規則 與登入的配置 和 註銷配置 以及其他安全配置

    /*配置白名單*/
    @Override
    protected void configure(HttpSecurity http) throws Exception{
        /**設置白名單不需要登入即可訪問(沒有權限就返回403)*/
        String[] urls ={
                "/admins/login",
                "/doc.html",
                "/**/*.css",
                "/**/*.js",
                "/favicon.ico",
                "/v2/api-docs",
                "/swagger-resources"
        };
        http.csrf().disable();//禁止跨域使用 "csrf()"這個方法就是跨域的方法
        //假如不禁止跨域使用 白名單路徑的異步訪問會出現 error: 403 的錯誤
        http.cors(); //允許通過客戶端的複雜關係類型之請求
        http.authorizeRequests()//请求需要被授权才可以访问
                .antMatchers(urls)//匹配某些路径
                .permitAll()//允许直接访问(不需要经过认证和授权)
                .anyRequest() //除了以上配置过的任何请求
                .authenticated();//通过认证才可以访问，登录后才能访问

        //添加处理JWT的过滤器，执行的顺序必须在处理用户名和密码的过滤器(内置的UsernamePasswordAuthenticationFilter)之前
        http.addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);





        //在路徑當中可以使用*作為通配符號 一個*表示匹配單層級的路徑 兩個*可以匹配多個層級的路徑
//        http.authorizeRequests() //请求需要被授权才可以访问
//                .antMatchers("/**") //匹配某些路径
//                .permitAll(); //允许直接访问(不需要经过认证和授权)
    }


}
