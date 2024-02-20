# 分布式事务 - Seata
## 下载Seata(1.4.2)

https://github.com/seata/seata/releases

## 什么是Seata

Seata 是一款开源的分布式事务解决方案，致力于在微服务架构下提供高性能和简单易用的分布式事务服务

也是Spring Cloud Alibaba提供的组件

Seata官方文档（更多信息可以通过官方文档获取）

https://seata.io/zh-cn/


Seata的目标是提供一个简单易用、高性能、稳定可靠的分布式事务解决方案，在保证强一致性的同时，提供高性能的事务处理，
并且兼容多种常见的分布式技术栈，包括Spring Cloud、Dubbo等


## 为什么需要Seata

我们之前学习了单体项目中的事务，使用的技术叫Spring声明式事务

能够保证一个业务中所有对数据库的写操作(增删改)要么都成功,要么都失败,来保证数据库的数据完整性

但是在微服务的项目中,业务逻辑层涉及远程调用,当前模块发生异常,无法操作远程服务器回滚

这时要想让远程调用也支持事务功能,就需要使用分布式事务组件Seata


> 事务的4个特性:ACID特性
- 原子性
- 一致性
- 隔离性
- 永久性

Seata保证微服务远程调用业务的原子性


Seata将为用户提供了 AT、TCC、SAGA 和 XA 事务模式,为用户打造一站式的分布式解决方案


## Seata的运行原理(AT模式)

Seata构成包含：
TC(Transaction Coordinator) 事务协调器：
    服务控制全局事务的整个流程，用于协调全局事务的提交和回滚，通过和RM进行交互，确保和RM进行交互，确保各个RM事务的一致性。

TM(Transaction Manager)事务管理器：
    是应用中发起分布式事务的一方，负责分解业务逻辑，协调各个RM。

RM(Resource Manager)资源管理器:
    负责管理各自服务的资源(数据库)，提供操作资源的接口，以便TC进行交互。
    当TC发送指令来协调全局事务时，RM需要协调数据的提交或回滚。



**我们项目使用AT(自动)模式完成分布式事务的解决**

AT模式运行过程

1.事务的发起方(TM)会向事务协调器(TC)申请一个全局事务id,并保存

2.Seata会管理事务中所有相关的参与方的数据源,将数据操作之前和之后的镜像都保存在undo_log表中,这个表是seata组件规定的表,
没有它就不能实现效果,依靠它来实现提交(commit)或回滚(roll back)的操作

3.事务的发起方(TM)会连同全局id一起通过远程调用运行资源管理器(RM)中的方法

4.RM接收到全局id,去运行指定方法,并将运行结果的状态发送给TC

5.如果所有分支运行都正常,事务管理器(TM)会通过事务协调器通知所有模块执行数据库操作,真正影响数据库内容,
反之如果有任何一个分支模块运行异常,都会通知TC,再由TC通知所有分支将数据库操作回滚,恢复成运行之前的样子


注意事项：

AT模式的运行有一个非常明显的前提条件,这个条件不满足,就无法使用AT模式

这个条件就是事务分支都必须是操作关系型数据库(mysql\MariaDB\Oracle)，需要一张表临时存储日志数据 - 表名：undo_log

但是如果我们在业务过程中有一个节点操作的是Redis或非关系型数据库时,就无法使用AT模式


## TCC模式
简单来说,TCC模式就是自己编写代码完成事务的提交和回滚

TCC模式要求我们在每个参与事务的业务中编写一组共3个方法

(prepare\commit\rollback)

prepare:准备   commit:提交   rollback:回滚

* prepare 方法是无论事务成功与否都会运行的代码

* commit 当整体事务运行成功时运行的方法

* rollback 当整体事务运行失败是运行的方法

优点:虽然代码是自己写的,但是事务整体提交或回滚的机制仍然可用(仍然由TC来调度)

缺点:每个业务都要编写3个方法来对应,代码冗余,而且业务入侵量大


## SAGA模式

SAGA模式的思想是对应每个业务逻辑层编写一个新的类,可以设置指定的业务逻辑层方法发生异常时,运行当新编写的类中的代码

这样编写代码不影响已经编写好的业务逻辑代码

一般用于修改已经编写完成的老代码

缺点是每个事务分支都要编写一个类来回滚业务,

会造成类的数量较多,开发量比较大


## XA模式(eXtended Architecture)
支持XA协议的数据库分布式事务,使用比较少



# 使用Seata
## 配置Seata
stock-webapi、cart-webapi、order-webapi项目都是具备数据库操作功能的模块
都需要添加seata依赖

1. pom.xml中添加
```xml
<dependencies>
        <!--seata整合springboot-->
        <dependency>
            <groupId>io.seata</groupId>
            <artifactId>seata-spring-boot-starter</artifactId>
        </dependency>
        <!-- seata完成分布式事务需要的两个相关依赖-->
        <dependency>
            <groupId>com.github.pagehelper</groupId>
            <artifactId>pagehelper-spring-boot-starter</artifactId>
        </dependency>
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>fastjson</artifactId>
        </dependency>
</dependencies>
```

2. application-dev.yml文件中配置

```yaml
seata:
  # 定义分组名称，一般去为了区分项目
  tx-service-group: csmall-group
  service:
    vgroup-mapping:
      # 定义分布式事务所涉及的服务组以及对应的seata配置
      csmall-group: default
    grouplist:
      # 设置seata所在的地址（默认端口号8091）
      default: localhost:8091
```

注意事项：
 同一个事务必须在同一个tx-service-group(分组)中
 通知指定项目的seata地址和端口(localhost:8091)

business模块配置：
1. pom.xml文件添加依赖
```xml
<dependencies>
    <!--seata整合springboot-->
    <dependency>
        <groupId>io.seata</groupId>
        <artifactId>seata-spring-boot-starter</artifactId>
    </dependency>
</dependencies>
```

2. application-dev.yml文件中配置

```yaml
seata:
  # 定义分组名称，一般去为了区分项目
  tx-service-group: csmall-group
  service:
    vgroup-mapping:
      # 定义分布式事务所涉及的服务组以及对应的seata配置
      csmall-group: default
    grouplist:
      # 设置seata所在的地址（默认端口号8091）
      default: localhost:8091
```

3.BusinessServiceImpl类中的buy添加注解 `@GlobalTransactional`
```java
@Service
@Slf4j
public class BusinessServiceImpl implements IBusinessService {

    @DubboReference
    private IOrderService dubboOrderService;

    // GlobalTransactional注解标记的方法，即分布式事务起点
    // 最终的效果就是由当前方法引发的所有远程调用对数据库的操作，要么全部成功，要么全部失败
    @GlobalTransactional
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

        // 为了验证seata实现了分布式事务效果（随机抛出异常，观察是否能够回滚）
//        if(Math.random()<0.5){
//            throw new CoolSharkServiceException(ResponseCode.INTERNAL_SERVER_ERROR,"发生随机异常，本次操作回滚！");
//        }

    }
}
```

## 启动Seata
seata也是java开发的，启动方式和nacos很像，只是启动命令不同
 注意：要求配置环境变量中Path数据值有java的bin目录路径
解压后路径不要用中文，不要用空格，也是解压之后在bin目录下路径上输入cmd进入终端(dos)窗口

```
D:\Java\seata\seata-server-1.4.2\bin>seata-server.bat -h 127.0.0.1 -m file
```

-h 127.0.0.1 ：指定seata Server监听的主机地址为127.0.0.1
-m file : 指定seata Server的模式为file，file模式表示使用文件模式的存储方法来存储事务数据。

启动组件：nacos，seata
在启动4个服务：cart、stock、order、business

访问business模块：http://localhost:20000/doc.html 中的`buy()`方法








随笔:
## 什么是事务

一般我们提到的"事务"指**数据库事务**

所谓数据库事务:是数据库管理系统执行数据库操作的一个逻辑单位,由一个有限的数据库操作序列构成

开发的 新增管理员 功能就是一个数据库事务

但是如果我们在程序运行过程中发生了异常,可能出现admin新增到数据库,但是关系没有完全新增完毕的状况,我们将这种状态称之为"数据库完整性缺失"

数据库完整性缺失会为程序运行出现各种bug埋下隐患,是我们要防止的现象

我们如果要想防止这样的情况发生就需要将这个功能的所有sql操作保存在同一个事务进行提交

这样就能实现这些sql操作要么都执行,要么都不执行的效果

一个数据库事务中的所有sql操作只有都顺利完成才能最终影响数据库数据,如果这个过程中发生了意外而终止,那么就不会提交这个操作,而执行"回滚/roll back",
让数据库还原为执行事务之前的状态


## 基于Spring JDBC的事务管理

事务（Transaction）是数据库中一种能够使得多个写操作（增删改）要么全部成功、要么全部失败的一种机制。

Spring JDBC是Spring对数据库编程的支持的框架，当在Spring Boot项目中添加了`mybatis-spring-boot-starter`
时，就会内置依赖`spring-boot-starter-jdbc`，而`spring-boot-starter-jdbc`是依赖了`spring-jdbc`的。

当项目中使用了Spring JDBC时，当需要使用事务来保障某个业务时，只需要在业务方法上添加`@Transactional`注解即可。

```
// 添加事务注解
// 实现效果:当前方法中所有sql操作要么都执行,要么都不执行
// 只要方法运行过程中发生异常,那么已经执行的sql语句都会"回滚"
@Transactional
public void addNew(AdminAddNewDTO adminAddNewDTO) {
     //.......
}
```

使用事务的原则:

**一个业务逻辑层方法中包含两个以及两个以上的增删改操作时**

为了保证数据库完整性,应该在方法前添加事务注解

所以,我们之前编写的注册功能的方法也应该添加事务注解

**【相关概念】**

- 自动提交：在默认的情况下，对数据库的数据的写操作默认都是自动提交的

    - 正在执行的程序和数据都是在内存中的，而数据库的数据是保存在永久存储的介质（例如硬盘）上的，所以，
      任何写操作都是先在内存中执行，再写入到硬盘上，自动提交指的就是自动写入到硬盘

- 开启事务（begin）：准备开始执行事务，本质上，将临时关闭默认的自动提交

- 提交（commit）：将内存中的写操作（对数据的改动）的结果写入到数据库中

    - 在执行结束后，默认会继续还原成自动提交的状态

- 回滚（rollback）：放弃在内存中的写操作，本质上，是不将数据写入到数据库

    - 在执行结束后，默认会继续还原成自动提交的状态

**【Spring JDBC的事务机制】**

- 关于回滚：在默认情况下，如果执行的操作抛出`RuntimeException`或其子孙类异常，Spring JDBC就会执行回滚

面试题：ACID、事务的传播、事务的隔离级别






