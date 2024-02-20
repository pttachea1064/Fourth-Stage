# 什么是Nacos

Nacos是Spring Cloud Alibaba提供的一个软件

这个软件主要具有注册中心和配置中心(课程最后讲解)的功能

我们先学习它注册中心的功能

微服务中所有项目都必须注册到注册中心才能成为微服务的一部分

注册中心和企业中的人力资源管理部门有相似

# Nacos的启动

因为Nacos是java开发的 , 我们要启动Nacos必须保证当前系统配置了java环境变量

简单来说就是要环境变量中,有JAVA_HOME的配置,指向安装jdk的路径,确定了支持java后,就可以启动Nacos了

将下载好的Nacos压缩包解压,将压缩包解压(注意不要有中文路径或空格)

打开解压得到的文件夹后打开bin目录

```
shutdown.cmd
shutdown.sh
startup.cmd
startup.sh
```
startup启动文件 shutdown停止文件
cmd结尾的文件是windows版本的，sh结尾的文件是linux(mac)版本的

**注：windows下启动Nacos不能直接双击cmd文件**

需要在终端窗口运行
```
D:\Java\nacos\bin>startup.cmd -m standalone
```

startup.cmd -m standalone 是启动nacos的指令
startup.cmd 指代文件
-m 设置启动参数
standalone 使用单机模式启动


如果启动报错:
```
Java不是内部或外部命令，也不是可运行的程序
```
表示当前电脑没有配置Java环境变量(主要没有JAVA_HOME)

浏览器：localhost://8848/nacos
第一次访问， 会出现登录页面：用户名、密码: nacos


# Nacos注册中心

## 注册到Nacos

我们已经讲过,一个项目要想称为微服务体系的一部分,必须将当前项目的信息注册到Nacos

我们要添加一些配置,目前先实现csmall-business模块启动时注册到Nacos的效果

## 首先camall-business模块

1. csmall-business模块pom文件中添加依赖

```xml
<!-- 注册到nacos的依赖-->
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
</dependency>
```

2. 在application-dev.yml中编写对nacos注册的配置信息
```yaml
spring:
  application:
    # 为当前项目命名，这个名称会在nacos注册时使用
    name: nacos-business
  cloud:
    nacos:
      discovery:
        # 定义nacos所在的位置，方便当前项目提交信息
        server-addr: localhost:8848
```

3. 先启动nacos，启动csmall-business模块，然后在nacos网站的**服务管理->服务列表**中看到nacos-business的名称


# Nacos心跳机制

> 常见面试题

Nacos内部注册的服务都会有一个心跳机制(注册到Nacos的微服务项目(模块)，都会遵循)

心跳机制：周期性操作，来表示自己是健康可用的机制

心跳机制的目的,是每个服务和Nacos保持沟通和交换信息的机制

- 默认情况下,服务启动后每隔5秒会向Nacos发送一个"心跳包",这个心跳包中包含了当前服务的基本信息（上课打卡）

- Nacos接收到这个心跳包,首先检查当前服务在不在注册列表中,如果不在按新服务的业务进行注册,如果在,表示当前这个服务是健康状态

- 如果一个服务连续3次心跳(默认15秒)没有和Nacos进行信息的交互,就会将当前服务标记为不健康的状态

- 如果一个服务连续6次心跳(默认30秒)没有和Nacos进行信息的交互,Nacos会将这个服务从注册列表中剔除


实际上Nacos的服务类型还有分类

* 临时实例(默认)
* 持久化实例(永久实例)

**默认每个服务都是临时实例**

如果想标记一个服务为永久实例

```yaml
cloud:
  nacos:
    discovery:
      # ephemeral设置当前项目启动时注册到nacos的类型 true(默认):临时实例 false:永久实例（不在项目中使用）
      ephemeral: false 
```

持久化实例启动时向nacos注册,nacos会对这个实例进行持久化处理

心跳包的规则和临时实例一致,只是不会将该服务从列表中剔除

一般情况下,我们创建的服务都是临时实例
只有项目的主干业务才会设置为永久实例



番外：
配置文件 - yml：
```yaml
# ...

# 当前Spring项目还需要读取额外的配置文件，当前读取到的文件名称application-dev.yml
spring:
  profiles:
    active: dev
```
后缀：
application-dev (development) - 开发环境
application-test (testing) - 测试环境
application-prod (production) - 生产环境


配置文件的区别：properties 和 yml

properties文件配置数据源：
```properties
spring.datasource.url=jdbc:mysql://localhost:3306
spring.datasource.username=root
spring.datasource.password=root
```
yml文件配置数据源：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306
    username: root
    password: root
```

bootstrap.yml, bootstrap.properties, application.yml, application.properties

同时存在 application.yml, application.properties yml文件会先加载，properties文件覆盖yml。

bootstrap: 系统级别的参数配置(一般不会变动)， 例如：连接数据库、加载配置中心
application: 应用级别的参数配置，例如：端口号，程序名称配置



nacos的更多配置：
    https://github.com/alibaba/spring-cloud-alibaba/wiki/Nacos-discovery



# 配置中心

所谓配置中心:将项目需要的配置信息保存在配置中心,需要读取时直接从配置中心读取,方便配置管理的微服务工具

我们可以将部分yml文件的内容保存在配置中心

一个微服务项目有很多子模块,这些子模块可能在不同的服务器上,如果有一些统一的修改,我们要逐一修改这些子模块的配置,
由于它们是不同的服务器,所以修改起来很麻烦，如果将这些子模块的配置集中在一个服务器上,我们修改这个服务器的配置信息,
就相当于修改了所有子模块的信息,这个服务器就是配置中心

**总结：高效的修改各模块配置的目的**


## 配置中心的使用

Nacos既可以做注册中心,也可以做配置中心

Nacos做配置中心,支持各种格式\类型的配置文件

properties\yaml(yml)\txt\json\xml等


## Nacos的数据结构

Namespace: 命名空间
    是nacos提供的最大的数据结构（一个nacos可以创建多个命名空间）

Group: 分组
    一个命名空间能够包含多个group

Service/DataId: 具体数据
    一个group中又可以包含多个配置信息

实现：
1.依赖项
```xml
    <!-- nacos的配置中心-->
    <dependency>
        <groupId>com.alibaba.cloud</groupId>
        <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
    </dependency>

    <!-- 支持SpringCloud加载系统配置的依赖-->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-bootstrap</artifactId>
    </dependency>
```

2.配置文件：bootstrap.yml
```yaml
spring:
  cloud:
    nacos:
      config:
        server-addr: localhost:8848 # 配置中心所在位置
        group: DEFAULT_GROUP  # 指定我们文件所在的组名(命名空间未指定，默认就是public)
        # 加载文件方式，默认加载的格式：[服务名称].[后缀名称]，目前服务器名称为nacos-stock，所以只需要指定后缀名即可
        file-extension: yaml # 指定文件后缀
        refresh-enabled: true
```

## 动态刷新

1.配置文件：bootstrap.yml
 可能存在框架版本兼容性问题，动态刷新需要借用共享配置列表，强制刷新配置信息。
```yaml
spring:
  cloud:
    nacos:
      config:
        #...
        shared-configs: # 共享配置列表
          - data-id: nacos-stock.yaml
            refresh: true # 让扩展配置刷新
```
2.代码实现 在需要动态刷新的类上添加`@RefreshScope`

```java
@RefreshScope // 用于启用配置的动态刷新功能。
public class StockController {

    // Spring框架提供的注入属性值的注解
    @Value("${stock.name}") // 从配置文件中注入嵌套属性值
    private String stockName;
}
```



