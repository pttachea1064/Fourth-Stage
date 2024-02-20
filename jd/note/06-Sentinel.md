# Sentinel

官网地址

https://sentinelguard.io/zh-cn/

下载地址

https://github.com/alibaba/Sentinel/releases

## 什么是Sentinel

Sentinel也是Spring Cloud Alibaba的组件

Sentinel英文翻译"哨兵\门卫"

随着微服务的流行，服务和服务之间的稳定性变得越来越重要。Sentinel 以流量为切入点，从流量控制、熔断降级、系统负载保护等多个维度保护服务的稳定性。

## 为什么需要Sentinel

- 丰富的应用场景

  双11,秒杀,12306抢火车票

- 完备的实时状态监控

  可以支持显示当前项目各个服务的运行和压力状态,分析出每台服务器处理的秒级别的数据

- 广泛的开源生态

  很多技术可以和Sentinel进行整合,SpringCloud,Dubbo,而且依赖少配置简单

- 完善的SPI扩展

  Sentinel支持程序设置各种自定义的规则


## 基本配置和限流效果

我们的限流针对的是 **控制器方法**

我们找一个简单的模块来测试和观察限流效果

在csmall-stock-webapi模块中
1.`pom.xml`添加依赖
```xml
  <!-- sentinel依赖： 限流降级-->
  <dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-sentinel</artifactId>
  </dependency>        
```

2.`application.yml`配置文件:
```yaml
spring: 
  cloud:
    sentinel:
      transport:
        # 配置sentinel的仪表台的位置
        dashboard: localhost:8080
        # 执行限流的端口，每个项目唯一（其他项目配置的话不能再使用8721）
        port: 8721
```

3.添加注解
```java
public class StockController {

    @Autowired
    private IStockService stockService;

    @PostMapping("/reduce/count")
    @ApiOperation("减少商品库存数")
    // @SentinelResource注解标记的控制器方法，会被Sentinel管理，
    // * 在这个方法第一次运行后，可以在Sentinel仪表台界面中设置限流规则
    // "减少库存的方法"设置当前方法在仪表台中显示的名称
    @SentinelResource("减少库存的方法")
    public JsonResult reduceCommodityCount(StockReduceCountDTO stockReduceCountDTO){
        // 调用业务逻辑层
        stockService.reduceCommodityCount(stockReduceCountDTO);
        return JsonResult.ok("商品库存减少完成!");
    }

}
```

4.sentinel启动(双击sentinel.bat)

 网页访问：http://localhost:8080/ -> 查看到sentinel界面(登录:用户名&密码均为 sentinel)

 **在这个方法第一次运行后**，才可以在Sentinel仪表台界面中设置限流规则 -> 对应服务 -> 簇点链路 -> 流控


5.自定义限流方法
```java
public class StockController {
  @Autowired
  private IStockService stockService;

  @PostMapping("/reduce/count")
  @ApiOperation("减少商品库存数")
  // blockHandler 指定请求被限流时运行的方法名称
  @SentinelResource(value = "减少库存的方法",blockHandler = "blockError")
  public JsonResult reduceCommodityCount(StockReduceCountDTO stockReduceCountDTO) throws InterruptedException {
    // ...  
  }

  /**
   *  Sentinel自定义限流方法的定义
   *  访问修饰符必须是public，返回值类型必须和限流的控制器方法一致
   *  方法名称必须是与限流注解定义的属性blockHandler指定的名称一致
   *  方法参数流列表必须与限流的控制器方法一致，而且还要添加一个BlockException类型的参数
   */
  public JsonResult blockError(StockReduceCountDTO stockReduceCountDTO, BlockException e){
    //限流方法 返回限流信息即可
    return JsonResult.failed(ResponseCode.BAD_REQUEST,"服务器忙，请稍后再试");
  }
}
```


6.QPS 与 并发线程数
 
QPS(Queries Per Second): 每秒请求数
  
  限制一秒内有多少个请求访问控制器方法 

并发线程数：是当前正在使用服务器资源请求线程的数量
  
  限制的是使用当前服务器的线程数


## 自定义降级方法

所谓降级就是正常运行控制器方法的过程中

控制器方法发生了异常,Sentinel支持我们运行别的方法来处理异常,或运行别的业务流程处理

我们也学习过处理控制器异常的统一异常处理类,和我们的降级处理有类似的地方

但是Sentinel降级方法优先级高,而且针对单一控制器方法编写

StockController类中@SentinelResource注解中,可以定义处理降级情况的方法

```
    // fallback 指定控制器方法发生异常时，要执行的降级方法的名称
    @SentinelResource(value = "减少库存的方法",blockHandler = "blockError", fallback = "fallbackError")
    public JsonResult reduceCommodityCount(StockReduceCountDTO stockReduceCountDTO) throws InterruptedException {
        // 调用业务逻辑层
        stockService.reduceCommodityCount(stockReduceCountDTO);

        if(Math.random() < 0.5){ // 演示抛异常，服务降级
            throw new CoolSharkServiceException(ResponseCode.INTERNAL_SERVER_ERROR,"抛出随机异常！");
        }

        return JsonResult.ok("商品库存减少完成!");
    }

    /**
     * Sentinel自定义降级方法的定义
     * 当原定方法发生异常，Sentinel就会运行下面的方法
     * 定义的逻辑中，可以编写一些逻辑的补救措施，使用户收到的损失最少
     */
    public JsonResult fallbackError(StockReduceCountDTO stockReduceCountDTO){
        return JsonResult.failed(ResponseCode.BAD_REQUEST,"服务器运行发生异常，服务降级");
    }
```

**练习**

为business模块控制器的buy方法添加Sentinel流控和降级的功能

流控时输出"服务器忙",降级时输出"服务降级"

1.pom文件
```xml
    <!-- sentinel依赖： 限流降级-->
    <dependency>
        <groupId>com.alibaba.cloud</groupId>
        <artifactId>spring-cloud-starter-alibaba-sentinel</artifactId>
    </dependency>
```

2.yml(port属性不能和stock模块的相同8722)
```yaml
spring:
  cloud:
    sentinel:
      transport:
        dashboard: localhost:8080
        port: 8722
```

3.修改控制器代码(注解,流控和降级方法)
```
    @SentinelResource(value = "buy",blockHandler = "blockError",fallback = "fallbackError")
    public JsonResult buy(){
        // 调用业务逻辑层方法
        businessService.buy();
        return JsonResult.ok("购买完成!");
    }
    
    public JsonResult blockError(BlockException e){
        return JsonResult.failed(ResponseCode.BAD_REQUEST,"服务器忙");
    }
    
    public JsonResult fallbackError(){
        return JsonResult.failed(ResponseCode.BAD_REQUEST,"服务降级");
    }
```


## 异常处理

Sentinel针对各种异常情况，也提供了丰富的异常处理工具，以便开发人员及时捕捉异常和快速定位问题。

Sentinel主要提供以下几种异常处理方式：

快速失败（Fail Fast）：

当请求频率过高或当前系统负载超过预设的阈值时，Sentinel可以采用快速失败机制，
立即拒绝请求并返回错误信息，避免请求长时间等待而导致请求堆积。

限流降级（Circuit Breaking）：

当远端服务或依赖资源出现故障或请求超时时，Sentinel可以根据业务设定的降级策略，触发断路器开关，
降低调用链路中的请求并返回预先设定的备选响应， 从而保证调用方的正常使用和可靠性。

降级（Fallback）：

当系统出现异常情况或负载过高时，Sentinel可以根据事先设定的降级策略，继续处理对某个服务的请求或提供相应服务的资源，
同时返回默认的或设定的响应结果，保证系统的继续运行和稳定性。



## 名词解释：

  限流（Flow Control）
    指限制某个资源的请求或访问流量，以保证系统的稳定性和可用性。限流是企业应对高并发请求和突发流量的重要手段之一。

  降级（Degrade）   
    指在系统出现异常或负载过高等情况下，通过降低某些非核心功能或流量来保障核心业务的稳定运行。降级是Sentinel中另一个重要的功能之一。
  
  阈值（Threshold）
    指限流或其他规则中设置的一个阈值或门限值，用于控制流量或性能指标的波动范围。
    当监测到指定的流量或性能指标超过阈值时，Sentinel将会触发流控或降级等相应策略。

  QPS上线（QPS Limit）
    指每秒请求数上限，是一种常见的流量控制策略之一，
    用于控制针对指定资源的请求流量，以避免过高的系统负载和性能损失。

  簇点（Cluster）
    指Sentinel中表示具体资源或服务的抽象概念，可以是URL、方法或其他标识符。
    簇点是对资源进行流控或其他策略限制的基本单位，它可以定义流量、延迟、并发数等多种性能指标的限制规则。  

  流控指标（Flow Metric）
    指用于衡量流控效果和评估系统性能的各种指标和监控数据，比如流量、延迟、成功率、并发数等。

  









