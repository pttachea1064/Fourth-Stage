# SpringGateway - 网关

## 奈非框架简介

早期(2020年前)奈非提供的微服务组件和框架受到了很多开发者的欢迎

这些框架和SpringCloud Alibaba的对应关系我们要了解

Nacos对应Eureka都是注册中心

Dubbo对应Ribbon+feign都是实现微服务远程调用的组件

Sentinel对应Hystrix都是项目限流熔断降级的组件

Gateway对应Zuul都是网关项目

Gateway框架不是阿里写的,是Spring提供的


## Spring Gateway简介

我们使用Spring Gateway作为当前项目的网关框架

Spring Gateway是Spring自己编写的,也是SpringCloud中的组件

SpringGateway官网
    https://docs.spring.io/spring-cloud-gateway/docs/current/reference/html/

网关项目git地址
    https://gitee.com/ds-timer/jsdvn2303-gateway-demo.git


## 什么是网关

"网"指网络,"关"指关口或关卡

网关:就是指网络中的关口\关卡

网关就是当前微服务项目的"**统一入口**"

程序中的**网关就是当前微服务项目对外界开放的统一入口**

因为提供了统一入口之后,方便对所有请求进行统一的检查和管理（前端开发好的端口无需修改）


网关的主要功能有

* 将所有请求统一由经过网关
* 网关可以对这些请求进行检查
* 网关方便记录所有请求的日志
* 网关可以统一将所有请求路由（分配）到正确的模块\服务上


## 简单网关演示

1.gateway项目pom.xml文件
```xml
    <dependencies>
        <!-- gateway:网关处理-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-gateway</artifactId>
        </dependency>
        <!-- nacos:注册中心-->
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
        </dependency>
        <!-- loadbalancer:负载均衡器-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-loadbalancer</artifactId>
        </dependency>
    </dependencies>
```

2.gateway项目application-dev.yml
```yaml
server:
  port: 9000
spring:
  application:
    name: gateway
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
    gateway:
      # routes 是一个数组类型的变量(yml文件中"- ..."表示当前配置的是数组元素)
      routes:
        - id: gateway-shanghai
          uri: lb://shanghai
          predicates:
            - Path=/sh/**
        # 设置这个路由的名称（名称和项目没有任何关联，只是别和其他的路由名称重复）
        - id: gateway-beijing
          # uri设置路由的目标 lb就是loadBalance，beijing是beijing项目注册到nacos的名称
          uri: lb://beijing
          # predicates(断言)：判断条件是真时，要做的事情
          predicates:
            # Path会设置当访问9000端口是如果路径以bj开头，会路由到uri上面配置的路径
            # Path就是路径断言，判断路径是否为/bj/开头，如果路径满足就会执行路由
            # localhost:9000/bj/show 路由到 localhost:9001/bj/show
            - Path=/bj/**
```

上面的yml配置了beijing和shanghai项目的路由信息
 我们使用
    http://localhost:9000/sh/show 可以访问shanghai项目资源
    http://localhost:9000/bj/show 可以访问beijing项目资源
 以此类推，再有很多服务器时，我们都可以仅使用9000端口号来将请求路由到正确的服务器上，
 实现了gateway服务成为项目的**统一入口**的效果


## 动态路由

网关项目随着微服务数量的增多

gateway项目的yml文件配置会越来越多,维护的工作量也会越来越大

所以我们希望gateway能够设计一套默认情况下自动路由到每个模块的路由规则

这样的话,不管当前项目有多少个路由目标,都不需要维护yml文件了

这就是我们SpringGateway的动态路由功能

gateway项目中的配置文件中开启即可
```yaml
server:
  port: 9000
spring:
  application:
    name: gateway
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
    gateway:
      discovery:
        locator:
          # 默认情况下gateway不开启动态路由，通过enabled: true开启动态路由
          # 路由规则是在9000端口后先编写路由目标项目注册到nacos的名称，再编写具体路径
          enabled: true
```
路由规则是在9000端口后先编写路由目标项目注册到nacos的名称，再编写具体路径
 我们是用
    http://localhost:9000/shanghai/sh/show 可以访问shanghai项目资源
    http://localhost:9000/beijing/bj/show 可以访问beijing项目资源


## 内置断言

在网关配置中使用了predicates(断言)的配置，断言的意思就是判断某个条件是否满足

我们之前使用了Path断言,判断请求的路径是不是满足条件,例如是不是/sh/**   /bj/**，如果路径满足这个条件,就路由到指定的服务器

但是Path实际上只是SpringGateway提供的多种内置断言中的一种

还有很多其它断言

- after
- before
- between
- cookie
- header
- host
- method
- path
- query
- remoteaddr

**时间相关**

before,after,between

判断当前时间在指定时间之前,之后或之间的操作

如果条件满足可以执行路由操作,否则拒绝访问

时间的格式：(ZonedDateTime.now())
```
2023-07-26T21:47:04.884+08:00[Asia/Shanghai]
```

使用before设置必须在指定时间之前访问
```yaml
    gateway:
      routes:
        - id: gateway-shanghai
          uri: lb://shanghai
          predicates:
            - Path=/sh/**
            - Before=2023-07-26T21:59:04.884+08:00[Asia/Shanghai]
```

使用after设置必须在指定时间之后访问
```yaml
gateway:
      routes:
        - id: gateway-shanghai
          uri: lb://shanghai
          predicates:
            - Path=/sh/**
            - After=2023-07-28T19:43:44.884+08:00[Asia/Shanghai]
```

使用between设置必须在指定时间之间访问
```yaml
gateway:
      routes:
        - id: gateway-shanghai
          uri: lb://shanghai
          predicates:
            - Path=/sh/**
            - Between=2023-07-28T19:43:44.884+08:00[Asia/Shanghai],2023-07-28T19:48:44.884+08:00[Asia/Shanghai]
```

使用Query断言，检查请求中是否包含name属性，包含才能路由
```yaml
gateway:
      routes:
        - id: gateway-shanghai
          uri: lb://shanghai
          predicates:
            - Path=/sh/**
            - Query=name
```

使用method断言，请求方式必须是GET,POST类型
```yaml
gateway:
      routes:
        - id: gateway-shanghai
          uri: lb://shanghai
          predicates:
            - Path=/sh/**
            - Method=GET,POST
```

使用cookie断言，cookie中必须有chocolate，值是ch.p
```yaml
gateway:
      routes:
        - id: gateway-shanghai
          uri: lb://shanghai
          predicates:
            - Path=/sh/**
            - Cookie=chocolate, ch.p
```

使用header断言，请求头中必须有X-Request-Id，值是 数字构成（正则表达式 \d 数字， + 至少出现一次）
```yaml
gateway:
      routes:
        - id: gateway-shanghai
          uri: lb://shanghai
          predicates:
            - Path=/sh/**
            - Header=X-Request-Id, \d+
```

## 自定义断言

SpringCloud Gateway包括许多内置的断言工厂，所有这些断言都与HTTP请求的不同属性匹配。 

一般情况下，内置的断言工厂基本能够满足开发需求，不过如果有些业务逻辑比较特殊，

那么我们也可以自定义路由断言工厂，在网关做统一判断。

创建 自定义路由断言工厂类
```java
package cn.tedu.gateway.predicate;

import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.gateway.handler.predicate.AbstractRoutePredicateFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 * 自定义的路由断言工厂类,要求有两个
 * 1 名字必须是 配置+RoutePredicateFactory
 * 2 内部类，用来接受配置文件中的参数
 * 3 必须继承AbstractRoutePredicateFactory<配置类>
 */
@Component
public class AgeRoutePredicateFactory extends AbstractRoutePredicateFactory<AgeRoutePredicateFactory.Config> {

    public AgeRoutePredicateFactory(){
        super(AgeRoutePredicateFactory.Config.class);
    }

    /**
     * 用于从配置文件中获取参数值赋值到 配置类中的属性上
     */
    @Override
    public List<String> shortcutFieldOrder() {
        // 顺序与配置文件中参数顺序一致
        return Arrays.asList("minAge","maxAge");
    }

    /**
     * 断言逻辑
     */
    @Override
    public Predicate<ServerWebExchange> apply(AgeRoutePredicateFactory.Config config) {
        return new Predicate<ServerWebExchange>(){
            @Override
            public boolean test(ServerWebExchange serverWebExchange) {
                // 1. 接受前端传入的age参数
                String ageStr = serverWebExchange.getRequest().getQueryParams().getFirst("age");
                // 2. 判断参数有效性
                if(StringUtils.isNotEmpty(ageStr)){ //age不为null && age不为空白
                    //3. 逻辑判断
                    int age = Integer.parseInt(ageStr);
                    if(age<config.getMaxAge() && age> config.getMinAge()){
                        return true;
                    }else {
                        return false;
                    }
                }
                return false;
            }
        };
    }

    /**
     * 内部配置类，用来接受配置文件中的参数
     */
    public static class Config{
        private int minAge;
        private int maxAge;

        public Config(){}

        public int getMinAge() {
            return minAge;
        }

        public void setMinAge(int minAge) {
            this.minAge = minAge;
        }

        public int getMaxAge() {
            return maxAge;
        }

        public void setMaxAge(int maxAge) {
            this.maxAge = maxAge;
        }
    }
}
```

application-dev.yml配置 age断言 只有age在（18,60）的用户才能访问订单服务
```yaml
    gateway:
      routes:
        - id: gateway-shanghai
          uri: lb://shanghai
          predicates:
            - Path=/sh/**
            - Age=18,60 # 断言设置 只有age在（18,60）的用户才能访问订单服务
```

## 内置过滤器

Gateway还提供的内置过滤器，不要和我们学习的filter混淆

内置过滤器允许我们在路由请求到目标资源的同时,对这个请求进行一些加工或处理

演示AddRequestParameter过滤器，作用是在请求中添加参数

```yaml
    gateway:
      routes:
        - id: gateway-shanghai
          uri: lb://shanghai
          predicates:
            - Path=/sh/**
          filters:
            - AddRequestParameter=name,Tom #添加name参数,值为Tom
```
其他内置过滤器的使用，自行查阅相关文档了解。


过滤器用于对请求和响应进行修改和处理，可以实现各种功能。如身份验证，请求重写，请求转发等等。

断言用于对请求进行条件匹配，确定请求是否匹配某个路由规则。

## 自定义局部过滤器

在SpringCloud Gateway中也内置了很多不同类型的网关路由过滤器，

一般情况下，内置的网关路由过滤器基本能够满足开发需求，不过如果想要自定义网关路由过滤器，也是可以的。

找到一个内置的网关路由过滤器，如RewritePathGatewayFilterFactory，查看它的源码，

然后可以得出自定义网关路由过滤器，需要满足的2个前提条件，

1.自定义网关路由过滤器的名字必须是配置+GatewayFilterFactory，

2.自定义网关路由过滤器类必须继承AbstractGatewayFilterFactory<配置类>。

接下来要实现一个能够根据配置，来选择开启缓存日志或者是控制台日志的过滤器。

创建自定义局部过滤器
```java
package cn.tedu.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

/**
 * 自定义局部过滤器
 */
@Component
public class LogGatewayFilterFactory extends AbstractGatewayFilterFactory<LogGatewayFilterFactory.Config> {

    public LogGatewayFilterFactory(){
        super(LogGatewayFilterFactory.Config.class);
    }

    /**
     * 用于从配置文件中获取参数值赋值到 配置类中的属性上
     */
    @Override
    public List<String> shortcutFieldOrder() {
        // 顺序与配置文件中参数顺序一致
        return Arrays.asList("consoleLog","cacheLog");
    }

    /**
     * 过滤器逻辑
     */
    @Override
    public GatewayFilter apply(Config config) {
        return new GatewayFilter(){
            @Override
            public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

                if(config.isConsoleLog()){
                    System.out.println("consoleLog开启....");
                }

                if(config.isCacheLog()){
                    System.out.println("cacheLog开启....");
                }

                return chain.filter(exchange);
            }
        };
    }

    /**
     * 内部配置类，用来接受配置文件中的参数
     */
    public static class Config{
        private boolean consoleLog;
        private boolean cacheLog;

        public Config(){}

        public boolean isConsoleLog() {
            return consoleLog;
        }

        public void setConsoleLog(boolean consoleLog) {
            this.consoleLog = consoleLog;
        }

        public boolean isCacheLog() {
            return cacheLog;
        }

        public void setCacheLog(boolean cacheLog) {
            this.cacheLog = cacheLog;
        }
    }
}

```

配置文件 application.yml
```yaml
    gateway:
      routes:
        - id: gateway-shanghai
          uri: lb://shanghai
          predicates:
            - Path=/sh/**
          filters:
            - Log=true,false
```

## 自定义全局过滤器

**全局过滤器作用于所有路由, 无需配置**。通过全局过滤器可以实现对权限的统一校验，安全性验证等功能。

其实内置的全局过滤器已经可以完成大部分的功能，但是对于企业开发的一些业务功能处理，

比如说鉴权等，就还是需要开发自定义一个全局过滤器来实现的。

接下来简单实现一个全局过滤器，去校验所有请求的请求参数中是否包含“token”， 如果不包含请求参数“token”则不转发路由，否则执行正常的逻辑。

```java
package cn.tedu.gateway.filter;

import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 自定义全局过滤器
 * 所有请求的请求参数中是否包含“token”， 如果不包含请求参数“token”则不转发路由，否则执行正常的逻辑。
 */
@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    /**
     * 全局过滤器判断逻辑
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String thoken = exchange.getRequest().getQueryParams().getFirst("token");

        if(!StringUtils.equals(thoken,"admin")){
            System.out.println("签权失败");
            // 设置响应状态码 500
            exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
            // 完成响应
            return exchange.getResponse().setComplete();
        }

        return chain.filter(exchange);
    }

    /**
     * 返回的是过滤器的优先级顺序，数值越小，优先级越高。
     * 这里设为0，表示优先级最高
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
```
访问：http://localhost:9000/bj/show?token=admin 响应正常


## 跨域访问
指浏览器中，从当前页面所在的域名向其他域名发送请求，在默认情况下，这个请求遵循同源策略。
用于隔离不同来源的网页脚本，来阻止此类跨域请求。以防止恶意网站窃取数据或进行潜在的攻击。

同源策略：加载文档或脚本只能是同一来源的资源进行交互，即协议、域名、端口必须完全一致。
如果是跨域访问其他域名的资源，浏览器将对这些跨域请求进行限制并阻止他们的执行。

配置文件实现方式：
参考：https://docs.spring.io/spring-cloud-gateway/docs/current/reference/html/#global-cors-configuration

```yaml
spring:
  cloud:
    gateway:
      # 配置全局CORS
      globalcors:
        cors-configurations:
          # 指定所有路径
          '[/**]':
            # 只允许该来源【*-所有来源】的请求进行跨域访问
            allowedOrigins: "*"
            # 只允许GET、POST方法的请求进行跨域访问
            allowedMethods:
              - GET
              - POST
```

配置类实现方式：
```java
package cn.tedu.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsWebFilter(){

        CorsConfiguration corsConfiguration = new CorsConfiguration();
        // 只允许所有来源的请求进行跨域访问
        corsConfiguration.setAllowedOrigins(Arrays.asList("*"));
        // 只允许GET、POST方法的请求进行跨域访问
        corsConfiguration.setAllowedMethods(Arrays.asList("GET","POST"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 指定所有路径
        source.registerCorsConfiguration("/**",corsConfiguration);

        return new CorsWebFilter(source);
    }
    
}

```

## gateway与sentinel整合 - 限流

pom.xml添加依赖
```xml
<dependencies>
    
    <!-- 其他依赖项 ... -->
    
    <!-- sentinel核心依赖-->
    <dependency>
        <groupId>com.alibaba.cloud</groupId>
        <artifactId>spring-cloud-starter-alibaba-sentinel</artifactId>
    </dependency>

    <!-- sentinel整合gateway-->
    <dependency>
        <groupId>com.alibaba.cloud</groupId>
        <artifactId>spring-cloud-alibaba-sentinel-gateway</artifactId>
    </dependency>
    
</dependencies>

```

application.yml
```yaml
spring:
  cloud:
    # 配置sentinel
    sentinel:
      transport:
        dashboard: localhost:8080
```

sentinel仪表台操作 限流规则



# 项目整合网关
1.新建项目gateway

2.父子相认

gateway项目pom.xml
```xml
    <parent>
        <groupId>cn.tedu</groupId>
        <artifactId>csmall-teacher</artifactId>
        <version>0.0.1-SNAPSHOT</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
```
csmall项目pom.xml
```xml
    <modules>
        <module>gateway</module>
    </modules>
```

3.添加依赖：
gateway项目pom.xml
```xml
    <dependencies>
        <!-- gateway:网关处理-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-gateway</artifactId>
        </dependency>
        <!-- nacos:注册中心-->
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
        </dependency>
        <!-- loadbalancer:负载均衡器-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-loadbalancer</artifactId>
        </dependency>

        <!-- sentinel核心依赖-->
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-sentinel</artifactId>
        </dependency>

        <!-- sentinel整合gateway-->
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-alibaba-sentinel-gateway</artifactId>
        </dependency>

        <!-- 聚合网关 knife4j-->
        <dependency>
            <groupId>com.github.xiaoymin</groupId>
            <artifactId>knife4j-spring-boot-starter</artifactId>
        </dependency>
    </dependencies>

```

4. application.yml文件中配置
```yaml
server:
  port: 10000
spring:
  application:
    name: gateway-server
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
    gateway:
      discovery:
        locator:
          enabled: true # 开启动态路由
  main:
    web-application-type: reactive # 响应式应用程序
```


5.网关项目的knife4j配置

我们希望配置网关之后，在使用knife4j中进行测试。所以需要配置knife4j才能实现

5.1. config包下新建SwaggerProvider类
```java
package cn.tedu.gateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.stereotype.Component;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class SwaggerProvider implements SwaggerResourcesProvider {
    /**
     * 接口地址
     */
    public static final String API_URI = "/v2/api-docs";
    /**
     * 路由加载器
     */
    @Autowired
    private RouteLocator routeLocator;
    /**
     * 网关应用名称
     */
    @Value("${spring.application.name}")
    private String applicationName;

    @Override
    public List<SwaggerResource> get() {
        //接口资源列表
        List<SwaggerResource> resources = new ArrayList<>();
        //服务名称列表
        List<String> routeHosts = new ArrayList<>();
        // 获取所有可用的应用名称
        routeLocator.getRoutes().filter(route -> route.getUri().getHost() != null)
                .filter(route -> !applicationName.equals(route.getUri().getHost()))
                .subscribe(route -> routeHosts.add(route.getUri().getHost()));
        // 去重，多负载服务只添加一次
        Set<String> existsServer = new HashSet<>();
        routeHosts.forEach(host -> {
            // 拼接url
            String url = "/" + host + API_URI;
            //不存在则添加
            if (!existsServer.contains(url)) {
                existsServer.add(url);
                SwaggerResource swaggerResource = new SwaggerResource();
                swaggerResource.setUrl(url);
                swaggerResource.setName(host);
                resources.add(swaggerResource);
            }
        });
        return resources;
    }
}

```

5.2. controller包下新建SwaggerController类
```java
package cn.tedu.gateway.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import springfox.documentation.swagger.web.*;

import java.util.Optional;

@RestController
@RequestMapping("/swagger-resources")
public class SwaggerController {
    @Autowired(required = false)
    private SecurityConfiguration securityConfiguration;
    @Autowired(required = false)
    private UiConfiguration uiConfiguration;
    private final SwaggerResourcesProvider swaggerResources;
    @Autowired
    public SwaggerController(SwaggerResourcesProvider swaggerResources) {
        this.swaggerResources = swaggerResources;
    }
    @GetMapping("/configuration/security")
    public Mono<ResponseEntity<SecurityConfiguration>> securityConfiguration() {
        return Mono.just(new ResponseEntity<>(
                Optional.ofNullable(securityConfiguration).orElse(SecurityConfigurationBuilder.builder().build()), HttpStatus.OK));
    }
    @GetMapping("/configuration/ui")
    public Mono<ResponseEntity<UiConfiguration>> uiConfiguration() {
        return Mono.just(new ResponseEntity<>(
                Optional.ofNullable(uiConfiguration).orElse(UiConfigurationBuilder.builder().build()), HttpStatus.OK));
    }
    @GetMapping("")
    public Mono<ResponseEntity> swaggerResources() {
        return Mono.just((new ResponseEntity<>(swaggerResources.get(), HttpStatus.OK)));
    }
}
```

5.3. filter包下新建SwaggerHeaderFilter类
```java
package cn.tedu.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;

@Component
public class SwaggerHeaderFilter extends AbstractGatewayFilterFactory {
    private static final String HEADER_NAME = "X-Forwarded-Prefix";

    private static final String URI = "/v2/api-docs";

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getURI().getPath();
            if (!StringUtils.endsWithIgnoreCase(path,URI )) {
                return chain.filter(exchange);
            }
            String basePath = path.substring(0, path.lastIndexOf(URI));
            ServerHttpRequest newRequest = request.mutate().header(HEADER_NAME, basePath).build();
            ServerWebExchange newExchange = exchange.mutate().request(newRequest).build();
            return chain.filter(newExchange);
        };
    }
}
```

启动组件：nacos、seata、sentinel
启动项目：cart、stock、order、business、gateway

通过10000端口号测试以下业务模块的功能：
    http://localhost:10000/nacos-stock/doc.html
    http://localhost:10000/nacos-cart/doc.html
    http://localhost:10000/nacos-order/doc.html
    http://localhost:10000/nacos-business/doc.html


6. gateway与springMVC依赖冲突问题和解决

网关依赖
```xml
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-gateway</artifactId>
        </dependency>
```

springMVC依赖项
```xml
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
```

以上两个依赖项在同一个项目中，默认情况下启动会报错。

SpringMVC框架中 自带服务器 Tomcat
Gateway框架中 自带服务器 Netty

在启动项目是，两个框架中包含的服务器都想占用相同端口，争夺端口号和主动权而发生冲突，导致报错。

要想正常启动项目在yml文件中配置,启动Netty服务器

```yaml
spring:
  main:
    web-application-type: reactive # 响应式应用程序
```





