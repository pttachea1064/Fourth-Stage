# RPC:
是Remote Procedure Call的缩写 翻译为:远程过程调用

目标是为了实现两台(多台)计算机\服务器,相互调用方法\通信的解决方案

是一种计算机的通信协议(一套标准)，该标准主要规定了两部分内容

1.序列化协议
2.通信协议


# 什么是Dubbo
对RPC有基本认识之后,再学习Dubbo就简单了

Dubbo是一套RPC框架。既然是框架，我们可以在框架结构高度，定义Dubbo中使用的通信协议，使用的序列化框架技术，
而数据格式由Dubbo定义，我们负责配置之后直接通过客户端调用服务端代码。

可以说Dubbo就是RPC概念的实现，Dubbo是SpringCloudAlibaba提供的框架，能够实现微服务相互调用的功能!

# Dubbo的发展历程

2012年底dubbo停止更新后到2017年dubbo继续更新之前

2015SpringCloud开始兴起,当时没有阿里的框架

国内公司要从SpringCloud和Dubbo中抉择使用哪个微服务方案

在2012年dubbo停止更新后国内的当当网在dubbo的基础上开发了dubboX框架,并进行维护

2019年后,SpringCloud和Dubbo(2.7以上)才能共同使用

# Dubbo对协议的支持

RPC框架分通信协议和序列化协议

Dubbo框架支持多种通信协议和序列化协议,可以通过配置文件进行修改

Dubbo支持的通信协议

* dubbo协议(默认)
* rmi协议
* hessian协议
* http协议
* webservice
* .....

支持的序列化协议

* hessian2(默认)
* java序列化
* compactedjava
* nativejava
* fastjson
* dubbo
* fst
* kryo

Dubbo默认情况下,支持的协议有如下特征

* 采用NIO单一长连接
* 优秀的并发性能,但是处理大型文件的能力差


# Dubbo服务的注册与发现

在Dubbo的调用过程中,必须包含注册中心的支持

注册中心推荐阿里自己的Nacos,兼容性好,能够发挥最大性能

但是Dubbo也支持其它软件作为注册中心(例如Redis,zookeeper等)

**服务发现，即消费端自动发现服务地址列表的能力，是微服务框架需要具备的关键能力，借助于自动化的服务发现，
微服务之间可以在无需感知对端部署位置与 IP 地址的情况下实现通信。**


consumer服务的消费者,指服务的调用者(使用者)

provider服务的提供者,指服务的拥有者(生产者)

在Dubbo中,远程调用依据是服务的提供者在Nacos中注册的服务名称

一个服务名称,可能有多个运行的实例,任何一个空闲的实例都可以提供服务

> 常见面试题:Dubbo的注册发现流程

1.首先服务的提供者启动服务时,将自己的具备的服务注册到注册中心,启动包括当前提供者的ip地址和端口号等信息,Dubbo会同时注册该项目提供的远程调用的方法

2.消费者(使用者)启动项目,也注册到注册中心,同时从注册中心中获得当前项目具备的所有服务列表

3.当注册中心中有新的服务出现时,会通知已经订阅发现的消费者,消费者会更新所有服务列表

4.RPC调用,消费者需要调用远程方法时,根据注册中心服务列表的信息,只需服务名称,不需要ip地址和端口号等信息,就可以利用Dubbo调用远程方法了


# 代码实现

**如果当前项目会成为Duboo的生产者，需要将提供给别的服务调用的方法单独编写在一个项目中，这个项目中有的方法，别人才能调用**

## csmall-stock-webapi项目(生产者)：

1.pom.xml新增依赖项
```xml
        <!-- Dubbo依赖-->
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-dubbo</artifactId>
        </dependency>
```

2.application-dev.yml中添加dubbo的配置信息
```yaml
# dubbo的相关配置
dubbo:
  protocol:
    # 会自动动态生成可用的端口号，规则从20880开始，如果占用就递增使用(port:-1是dubbo框架支持的特殊写法)
    port: -1
    # 设置连接名称，一般固定就叫dubbo
    name: dubbo
  registry:
    # 声明注册中心的软件类型和ip端口号
    address: nacos://localhost:8848
  consumer:
    # 当前项目启动时，作为消费者，是否要检查所有远程服务可用，false表示不检查
    check: false
```

3.StockServiceImpl实现类上添加注解
```java

// @DubboService注解标记的业务逻辑层实现类,其中所有方法都会注册到Nacos
// 其它服务在订阅时会发现当前项目提供的业务逻辑层方法,以备Dubbo调用
@DubboService
public class StockServiceImpl implements IStockService {
    // ...
}
```

4.CsmallStockWebapiApplication启动类上添加注解
```java
// 如果当前项目是Dubbo的生产者，需要添加这个注解，否则无法调用该项目的方法
@EnableDubbo
public class CsmallStockWebapiApplication {
    //...
}
```

## csmall-cart-webapi项目(生产者)：

与csmall-stock-webapi项目 步骤一致

## csmall-order-webapi项目(生产者&消费者)：
1.pom.xml新增依赖项
```xml
        <!-- Dubbo依赖-->
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-dubbo</artifactId>
        </dependency>
```

2.application-dev.yml中添加dubbo的配置信息
```yaml
# dubbo的相关配置
dubbo:
  protocol:
    # 会自动动态生成可用的端口号，规则从20880开始，如果占用就递增使用(port:-1是dubbo框架支持的特殊写法)
    port: -1
    # 设置连接名称，一般固定就叫dubbo
    name: dubbo
  registry:
    # 声明注册中心的软件类型和ip端口号
    address: nacos://localhost:8848
  consumer:
    # 当前项目启动时，作为消费者，是否要检查所有远程服务可用，false表示不检查
    check: false
```

3.
```java
// business模块会调用order模块的业务方法，所以order仍然需要编写生产者需要的注解
@DubboService
@Service
@Slf4j
public class OrderServiceImpl implements IOrderService {
    @Autowired
    private OrderMapper orderMapper;

    // 当前order模块消费stock模块的减少库存的方法
    // stock模块的减少库存的方法注册到nacos，所以当前order模块可以利用Dubbo调用
    // 想要调用就必须使用@DubboReference,才能获得业务逻辑层实现类对象(有的公司要求dubbo的业务层变量名用dubbo)
    @DubboReference
    private IStockService dubboStockService;

    @DubboReference
    private ICartService dubboCartService;

    @Override
    public void orderAdd(OrderAddDTO orderAddDTO) {

        // 1.减少订单商品的库存数(要调用stock模块的方法)
        StockReduceCountDTO stockReduceCountDTO = new StockReduceCountDTO();
        stockReduceCountDTO.setCommodityCode(orderAddDTO.getCommodityCode());
        stockReduceCountDTO.setReduceCount(orderAddDTO.getCount());
        // dubbo调用业务逻辑层的方法 - 减少库库存
        dubboStockService.reduceCommodityCount(stockReduceCountDTO);

        // 2.删除订单中勾选的购物车中的商品(要调用cart模块的方法)
        dubboCartService.deleteUserCart(orderAddDTO.getUserId(),orderAddDTO.getCommodityCode());

        // 3.新增订单
        OrderTb order=new OrderTb();
        BeanUtils.copyProperties(orderAddDTO,order);
        // 执行新增
        orderMapper.insertOrder(order);
        log.info("新增的订单信息:{}",order);
    }
}
```

4.启动类
```java
@SpringBootApplication
@EnableDubbo
public class CsmallOrderWebapiApplication {

    public static void main(String[] args) {
        SpringApplication.run(CsmallOrderWebapiApplication.class, args);
    }

}
```

## csmall-business-webapi项目(消费者)：
1.pom.xml新增依赖项
```xml
        <!-- Dubbo依赖-->
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-dubbo</artifactId>
        </dependency>
```

2.application-dev.yml中添加dubbo的配置信息
```yaml
# dubbo的相关配置
dubbo:
  protocol:
    # 会自动动态生成可用的端口号，规则从20880开始，如果占用就递增使用(port:-1是dubbo框架支持的特殊写法)
    port: -1
    # 设置连接名称，一般固定就叫dubbo
    name: dubbo
  registry:
    # 声明注册中心的软件类型和ip端口号
    address: nacos://localhost:8848
  consumer:
    # 当前项目启动时，作为消费者，是否要检查所有远程服务可用，false表示不检查
    check: false
```

3.BusinessServiceImpl类中
```java
@Service
@Slf4j
public class BusinessServiceImpl implements IBusinessService {

    @DubboReference
    private IOrderService dubboOrderService;

    @Override
    public void buy() {
        // 模拟购买业务
        // 创建一个OrderAddDTO类,并为它赋值
        OrderAddDTO orderAddDTO=new OrderAddDTO();
        orderAddDTO.setUserId("UU100");
        orderAddDTO.setCommodityCode("PC100");
        orderAddDTO.setCount(5);
        orderAddDTO.setMoney(500);
        // 模拟购买只是输出到控制台即可
        log.info("新增订单的信息为:{}",orderAddDTO);

        // 调用order业务
        dubboOrderService.orderAdd(orderAddDTO);
    }
}
```

# 负载均衡(loadBalance)

## 什么是负载均衡

负载均衡（Load Balancing）是指在多台服务器或计算机集群中，通过某种算法将要处理的请求（包括网络流量、网络请求或数据流等）
平均分配到每一台服务器中去，以达到优化总体系统性能、提高吞吐量、降低响应时间等目的的一种技术方案。


在实际开发中,一个服务基本都是集群模式的,也就是多个功能相同的项目在运行,这样才能承受更高的并发

这时一个请求到这个服务,就需要确定访问哪一个服务器

Dubbo框架内部支持负载均衡算法,能够尽可能的让请求在相对空闲的服务器上运行

Dubbo内部默认支持负载均衡算法

在不同的项目中,可能选用不同的负载均衡策略,以达到最好效果


## Dubbo内置负载均衡策略算法

Dubbo内置4种负载均衡算法

- **random loadbalance:随机分配策略(默认)**
- round Robin Loadbalance:权重平均分配
- leastactive Loadbalance:活跃度自动感知分配
- consistanthash Loadbalance:一致性hash算法分配


### 随机分配策略(默认)
随机选择一台可用的服务器，将请求转发到该服务器上。
假设我们当前3台服务器,经过测试它们的性能权重比值为5:3:1
随机生成随机数，在哪个范围内让哪个服务器运行

优点:
算法简单,效率高,长时间运行下,任务分配比例准确

缺点:
偶然性高,如果连续的几个随机请求发送到性能弱的服务器,会导致异常甚至宕机

### 权重平均分配(SWRR)
如果几个服务器权重一致,那么就是依次运行,但是服务器的性能权重一致的可能性很小,所以我们需要权重平滑分配。

一个优秀的权重分配算法,应该是让每个服务器都有机会运行的，如果一个集群服务器性能比为5:3:1

1>A 2>A 3>A 4>A 5>A  6>B  7>B  8>B 9>C

10>A ...

上面的安排中,连续请求一个服务器肯定是不好的,我们希望所有的服务器都能够穿插在一起运行

Dubbo2.7之后更新了这个算法使用"**平滑加权算法**"优化权重平均分配策略

### 活跃度自动感知分配

记录每个服务器处理一次请求的时间

按照时间比例来分配任务数,运行一次需要时间多的分配的请求数较少


### 一致性hash算法分配

根据请求的参数进行hash运算,以后每次相同参数的请求都会访问固定服务器

因为根据参数选择服务器,不能平均分配到每台服务器上,使用的也不多

配置（yml文件）
```yaml
dubbo:
  protocol:
  registry:
    # 声明注册中心的软件类型和ip端口号
    address: nacos://localhost:8848
  consumer:
    # random、roundrobin、leastactive、consistanthash
    loadbalance: random
```

# Dubbo生产者消费者配置小结

Dubbo生产者消费者相同的配置

pom文件添加dubbo依赖,yml文件配置dubbo信息

**生产者**

* 要有service接口项目

* 提供服务的业务逻辑层实现类要添加@DubboService注解
* SpringBoot启动类要添加@EnableDubbo注解

**消费者**

* pom文件添加消费模块的service依赖
* 业务逻辑层远程调用前,模块使用@DubboReference注解获取业务逻辑层实现类对象









