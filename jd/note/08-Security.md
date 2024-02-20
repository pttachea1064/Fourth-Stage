## 练习(准备)：管理员模块

## 作业内容

创建新的项目，实现服务器端的：

- 添加管理员账号
- 显示管理员列表

## 作业步骤

**1. 创建子级项目**

项目名称：`csmall-sso`

Group Id：`cn.tedu`

Artifact Id：`csmall-sso`

Packages：`cn.tedu.csmall.sso`

Java版本：`8`

Spring Boot版本：`2.5.4`

创建过程中，可以不勾选任何依赖项。

创建完成后，从`csmall-stock`项目中，将所需依赖项复制到新项目中。



**2. 创建数据库与数据表**

创建`mall_ams`数据库

在新项目中配置Database面板，连接到的是`mall_ams`数据库

执行数据库脚本（`mall_ams.sql`）



**3. 配置application.yml、application.yml**

配置连接数据库的参数，注意：复制代码后需要修改数据库名称

配置服务器端端口号为`10002`

配置Mybatis的XML文件位置

配置日志的显示级别为`trace`

配置Knife4j的配置



**4. 添加必要的配置类**

`CommonsConfiguration`：扫描commons模块的统一异常处理类,使其在当前项目中生效

`MybatisConfiguration`：配置Mybatis接口所在的包

`Knife4jConfiguration`：配置Knife4j的API文档，注意，需要修改包名



**5. 开发持久层功能**

在commons下创建`pojo.admin.model.Admin`类，是与`ams_admin`表对应的实体类

在commons下创建`pojo.admin.vo.AdminListItemVO`类，此类中应该包含：`id`、`username`、`nickname`、`phone`、`email`、`enable`，另外，可自行决定添加其它属性

在根包下创建`mapper.AdminMapper`接口，并添加抽象方法：

```java
public interface AdminMapper {
    int insert(Admin admin);
    int countByUsername(String username); // 用于在添加业务中保障用户名唯一
    List<AdminListItemVO> list();
}
```

在`resources`下创建`mapper`文件夹（与`application.properties`中的配置对应），并复制得到`AdminMapper.xml`文件，在此文件中配置以上接口中各抽象方法映射的SQL，配置时：

- 插入数据时，必须获取自动编号的id
- 查询列表时，应该使用`<sql>`封装字段列表，使用`<resultMap>`配置结果集的处理



添加test测试依赖后，可以在`src/test/java`的根包下创建`mapper.AdminMapperTests`测试类，用于测试以上3个功能

```xml
<!-- Spring Boot测试 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```



**6. 开发业务逻辑层**



在commons下创建`pojo.admin.dto.AdminAddNewDTO`类，此类中应该包含：`username`、`password`、`nickname`、`phone`、`email`、`enable`、`avatar`、`description`

在根包下创建`service.IAdminService`接口，在接口中添加抽象方法：

```java
public interface IAdminService {
    void addNew(AdminAddDTO adminAddDTO);
    List<AdminListItemVO> list();
}
```

在根包下创建`service.impl.AdminServiceImpl`类，实现`IAdminService`接口，并在类上添加`@Service`注解，在类中自动装配`AdminMapper`对象，并实现以上抽象方法。注意：应该写日志！



添加test测试依赖后，可以在`src/test/java`的根包下创建`service.AdminServiceTests`测试类，用于测试以上2个功能



**7. 开发控制器层**

在根包下创建`controller.AdminController`类，在类上添加`@RestController`、`@RequestMapping("/admins")`注解，在类中自动装配`IAdminService`对象，然后，添加处理请求的方法，本次请求路径设计为：

- `/admins/add`
- `/admins`

完成后，在控制器类、处理请求的方法上添加Knife4j相关的注解，启动项目，通过Knife4j的调试功能检测以上2个功能是否可以正确运行


# Spring Security框架

Spring Security框架主要解决了**认证**与**授权**的相关问题。


## 密码加密(测试)

在开发实践中，所有的用户密码都**必须**加密之后，再存储到数据库中。

用户的原始密码（例如`1234`）通常称之为**原文**或**明文**，加密后得到的结果（例如`lkjfadshfdslafndshdsfaj`）通常称之为**密文**。

在处理加密时，通常应该选取**消息摘要算法**对用户的密码进行处理！

**注意：不可以使用加密算法对密码进行加密并存储，通常，加密算法是用于保障传输过程的安全的！**

消息摘要算法是**不可逆**的算法，是适合对密码进行加密的！

消息摘要算法的主要特点有：

- 同一种算法，无论消息长度多少，摘要的长度是固定的
- 当消息相同时，摘要必然相同
- 当消息不同时，摘要理论上不会相同（有概率是相同的）
    - 消息的长度是无限的，摘要的长度是有限且固定的

需要注意：理论上有n种不同的消息对应同一个摘要，但是，出现这样的现象的概率极低！

典型的消息摘要算法有：

- MD系列（Message Digest）：`MD2` / `MD4`  / `MD5`
    - MD系列的全部是128位算法
- SHA家族（Secure Hash Algorithm）：`SHA-1` / `SHA-256` / `SHA-384` / `SHA-512`
    - SHA-1是160位算法，其它则是与算法名称对应，例如`SHA-256`就是256位算法
- SM3（国家加密算法）
    - SM3是256位算法

在Spring Boot中，`spring-boot-starter`依赖项就包含`DigestUtils`工具类，可以简便的实现MD5算法的处理，例如：

```java
package cn.tedu.csmall.passport;

import org.junit.jupiter.api.Test;
import org.springframework.util.DigestUtils;

public class MessageDigestTests {

    @Test
    public void testMd5() {
        String rawPassword = "123456";
        String encodedPassword = DigestUtils.md5DigestAsHex(rawPassword.getBytes());
        System.out.println("rawPassword = " + rawPassword);
        System.out.println("encodedPassword = " + encodedPassword);
        // 123456 >>> e10adc3949ba59abbe56e057f20f883e
    }

}
```

> 如果想要使用其它消息摘要算法，可以自行在项目中添加`commons-codec`依赖项，此依赖中也有一个名为`DigestUtils`的工具类，提供了多种算法的API。

由于消息算法的特点包括“消息相同，摘要必然相同”，所以，在互联网上有一些平台记录了消息与摘要的对应关系，记录在数据库，可以根据摘要进行反向查询，
从而得知摘要对应的消息！但是，由于这些平台能够记录的对应关系非常有限，可以使用更复杂的消息，大概率是没有被这些平台收录的，则不会被这些平台反向查询出原消息！

换言之，**只要原始密码足够复杂，则不会被这些平台“破解”。**

但是，某些场景中并不支持使用复杂的消息（密码），也有些用户不愿意使用复杂的原始密码，则很容易被穷举出消息与摘要的对应列表，为解决此问题，
应该在加密过程中使用“盐”，盐的本质就是一个字符串，其作用是使得被运算数据变得更加复杂，例如：

```
@Test
public void testMd5() {
    String salt = "kjkhglkjjg";
    String rawPassword = "123456";
    //                                                   123456kjkhglkjjg
    String encodedPassword = DigestUtils.md5DigestAsHex((rawPassword + salt).getBytes());
    System.out.println("rawPassword = " + rawPassword);
    System.out.println("encodedPassword = " + encodedPassword);
}
```

而盐值的具体值并没有明确的要求，包括其使用方式也没有明确的要求！

另外，还可以尝试多重加密，即循环调用以上算法。

**所以，为了提高密码的安全性：**

- **强制要求使用强度更高的密码**
- **加盐**
- **多重加密**
- **使用更安全的算法**
- **综合使用以上做法**

关于盐的补充：通常，可以使用随机的盐值，则即使完全相同的原始密码，得到的加密结果也完全不同，例如：

```
@Test
public void testMd5() {
    for (int i = 0; i < 5; i++) {
        String salt = UUID.randomUUID().toString();
        String rawPassword = "123456";
        String encodedPassword = DigestUtils.md5DigestAsHex((salt + rawPassword).getBytes());
        System.out.println("rawPassword = " + rawPassword);
        System.out.println("encodedPassword = " + encodedPassword);
        System.out.println();
    }
}
```

以上运行结果例如（每次都不同）：

```
rawPassword = 123456
encodedPassword = 678408c66bef83edf72b11ad5b505161

rawPassword = 123456
encodedPassword = 99c3da1ef1d1e9ea976c91a00af0b4c0

rawPassword = 123456
encodedPassword = 52c809ab1ef18607c0f357d1caa4082f

rawPassword = 123456
encodedPassword = faf506f5d7a8d5109fc24d4c700fb136

rawPassword = 123456
encodedPassword = e89b5401bfbd233e24cb3862425ccdb8
```

**需要注意的是，一旦使用随机的盐值，则必须将此随机的盐值记录下来**（可以在添加数据时，在数据表中使用专门的字段进行记录，或者，
将盐址和加密结果合并成1个字符串作为记录下来的密码），否则，在后续的验证密码时，将无法运算得到匹配的结果！

使用示例：

```
rawPassword = 123456
salt = 4da1ba18-e9c5-4adc-bc0e-3768aca841ad
encodedPassword = ef3bcab34967ab87d9a3002366439898

得到最终密码（盐值拼接密文）：
4da1ba18-e9c5-4adc-bc0e-3768aca841adef3bcab34967ab87d9a3002366439898
```

当使用了Spring Security框架后，此框架中还包含了`BCryptPasswordEncoder`类，此类可以使用BCrypt算法对密码进行处理，
调用此类对象的`encode()`方法即可实现加密，调用`matches()`方法就可以实现将原文和密文进行对比！
（这2个方法都是在`PasswordEncoder`接口中定义的）




## 添加依赖

在Spring Boot项目中，需要使用Spring Security时，需要添加`spring-boot-starter-security`依赖。
```xml
      <!-- Spring Security: 处理认证与授权相关问题-->
      <dependency>
          <groupId>org.springframework.boot</groupId>
          <artifactId>spring-boot-starter-security</artifactId>
      </dependency>
```

当项目中添加此依赖后，默认会执行一系列的自动配置，将：

- 当前项目中所有的访问，都是必须先登录才允许的
  - 未登录时，将自动重定向到 `/login`，当登录成功后，将自动重定向到此前访问的页面，或主页
  - 访问 `/logout` 可以退出登录
  - 如果希望某些路径不需要登录就可以访问，可以自定义Spring Security的配置类，将这些路径配置为“白名单”
- 启动项目的过程中，会生成随机的临时密码，用户名为`user`


## 在Spring Security中放行某些请求

默认情况下，Spring Security要求所有请求都是必须登录后才可以访问的，当需要放行某些请求时，可以将这些请求路径配置为“白名单”。

需要自定义配置类，继承自`WebSecurityConfigurerAdapter`类，重写其中的`configurer(HttpSecurity)`方法：

```java
package cn.tedu.csmall.passport.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Bean
    public PasswordEncoder passwordEncoder() {
        log.debug("创建密码编码器组件：BCryptPasswordEncoder");
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 在配置路径时，可以使用星号作为通配符
        // 使用 /* 只能匹配1层级路径，例如 /user 或 /brand，不可以匹配多层级，例如不可以匹配到 /user/list
        // 使用 /** 可以匹配若干层级路径

        http.csrf().disable(); // 禁用防止跨域访问，如果无此配置，白名单路径的异步访问也会出现403错误

        http.authorizeRequests() // 请求需要被授权才可以访问
            .antMatchers("/**") // 匹配某些路径
            .permitAll(); // 允许直接访问（不需要经过认证和授权）
    }
}
```

## 使用数据库中的账号实现登录

在Spring Security，使用默认的登录时（默认存在的`/login`页面），默认情况下使用`user`作为用户名，使用启动时生成的临时密码。
在处理过程中，也可以使其使用数据库中的账号进行登录，Spring Security会自动获取在输入框中输入的用户名、密码，然后，
会自动调用`UserDetailsService`接口类型对象的`UserDetails loadUserByUsername(String username)`方法，并获取返回的`UserDetails`对象，
此对象中应该包含密码的密文值，接下来，Spring Security会自动将登录界面中获取的密码原文进行加密，并与`UserDetails`中的密文进行对比，以判断是否可以成功登录。

在测试之前，应该先禁用（注释掉相关代码）Spring Security中的`configurer(HttpSecurity)`方法，否则将不会显示 `/login` 页面。

接下来，在项目的根包下自定义类`security.UserDetailsServiceImpl`类，实现`UserDetailsService`接口，并重写接口中的`loadUserByUsername()`方法：

```java
@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        log.debug("Spring Security自动根据用户名【{}】查询用户详情", s);
        return null;
    }
    
}
```

一旦编写了`UserDetailsService`接口的实现类，并将此类由Spring创建对象，则Spring Security会自动装配此类的对象，在后续启动项目时，将不再生成默认的随机密码，且默认的用户名`user`将不再可用。

可以在此方法中测试返回某个账号信息，例如：

```
@Override
public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
    log.debug("Spring Security自动根据用户名【{}】查询用户详情", s);

    // 以下是临时使用的代码
    if ("wangkejing".equals(s)) {
        UserDetails userDetails = User.builder()
                .username("wangkejing") // 用户名
                .password("$2a$10$XzUcx6Oag7n0tNKhBAGQEe5sFv9Jow9Fa0020UiWkfajUue7bmjz6") // 密码，此密文的原文是123456
                .disabled(false) // 账号是否禁用
                .accountLocked(false) // 账号是否锁定
                .accountExpired(false) // 账号是否过期
                .credentialsExpired(false) // 认证是否过期
                .authorities("临时给出任意字符串") // 【必须】此账号的权限信息
                .build();
        return userDetails;
    }

    return null;
}
```

如果此时重启项目，并通过 `/login` 测试登录：

- 当用户名不是`zhangsan`时，浏览器将提示`UserDetailsService returned null`，且IntelliJ IDEA控制台会有异常信息
- 当用户名是`zhangsan`但密码不是`123456`时，在浏览器将提示“用户名或密码错”
- 当用户名是`zhangsan`且密码是`123456`时，将登录成功（登录后的跳转可能404）

如果要改为通过数据库中的账号来实现登录，则在以上代码中，替换为“根据用户名查询管理员信息，如果存在此用户名对应的数据，则将查询到的管理员信息封装到`UserDetails`中并返回”即可。

所以，接下来需要执行的任务：

- 在commons下创建`pojo.admin.vo.AdminLoginVO`类，此类中至少包括：用户名、密码

- 在`AdminMapper`接口中添加抽象方法`AdminLoginVO getByUsername(String username);`

- 在`AdminMapper.xml`中配置以上抽象方法映射的SQL

  - ```mysql
    select username, password from ams_admin where username=?
    ```

- 在`AdminMapperTests`中进行测试

- 在`UserDetailsServiceImpl`中，自动装配`AdminMapper`对象，然后调用以上查询功能，
  并将查询到的结果中的信息封装到返回的`UserDetails`中去，如果未查询到有效记录，可以直接返回`null`




## 自定义处理登录的流程

默认情况下，Spring Security有默认的登录页，输入用户名、密码后，是由Spring Security自动接收登录请求，然后进行处理的，
如果登录成功，会自动跳转到此前访问的页面，如果登录失败，会将错误信息提示到默认的登录页上。

以上这套流程不适用于开发实践，因为：

- 这不是前后端分离的做法（服务器端处理了登录后，不响应JSON结果）
- 不便于处理细节，例如使用Validation框架验证请求参数的格式

要解决此问题，应该：像开发其它数据处理流程一样的做法，只不过，在自定义的Service实现过程中，通过Spring Security的机制来验证用户名和密码即可。

首先，需要使得控制器可以接收客户端提交的登录请求，需要：

- 在commons下创建`pojo.dto.AdminLoginDTO`类，在此类中封装登录请求的相关参数，例如：用户名，密码

- 在`AdminController`中添加处理登录请求的方法：

  - ```
    @PostMapping("/login")
    public JsonResult login(AdminLoginDTO adminLoginDTO) {
        // 通过日志简单的输出
        return JsonResult.ok();
    }
    ```

- 完成后，可以Knife4j中测试访问，且响应结果永远是成功（目前还没有真正的实现登录）

然后，需要在Service中准备处理登录，需要：

- 在`IAdminService`中声明：`void login(AdminLoginDTO adminLoginDTO);`
- 在`AdminServiceImpl`中重写以上方法，实现细节可暂时留空
- 在`AdminController`处理登录时调用Service组件的此方法

关于在Service中处理登录的细节，应该使用Spring Security中的`AuthenticationManager`对象来执行Spring Security的认证过程 （后续保存用户信息、授权访问等都需要）。

如果需要得到`AuthenticationManager`，需要在Spring Security的配置类（自定义的`SecurityConfiguration`类）中
重写`authenticationManager()`方法，此方法可以返回`AuthenticationManager`对象，则在重写在方法上添加`@Bean`注解，可以使得Spring会自动调用此方法，并将返回结果保存在Spring容器中：

```
@Bean // 必须添加此注解
@Override
protected AuthenticationManager authenticationManager() throws Exception {
    return super.authenticationManager();
}
```

然后，回到业务实现类中，自动装配`AuthenticationManager`对象，在具体实现时，调用此对象的`authenticate()`方法，即可实现Spring Security的认证，此方法的参数可使用`UsernamePasswordAuthenticationToken`来封装用户名和密码：

```
@Override
public void login(AdminLoginDTO adminLoginDTO) {
    log.debug("开始处理管理员登录的业务，参数：{}", adminLoginDTO);

    // 调用AuthenticationManager执行Spring Security的认证
    Authentication authentication
            = new UsernamePasswordAuthenticationToken(
                    adminLoginDTO.getUsername(), adminLoginDTO.getPassword());
    authenticationManager.authenticate(authentication);
    log.debug("登录成功！");
}
```

以上代码的执行流程大致是：

```
请求 ==> Controller ==> Service ==> AuthenticationManager ==> UserDetailsServiceImpl ==> Mapper
```


## 关于Session

HTTP协议是**无状态**的协议，从协议本身来说，通信过程中并不会记录用户的相关信息，如果某用户第1次访问了服务器后，第2次再次访问时，
服务器并不会知道这是与第1次访问时是同一个用户！

在开发实践时，需要明确用户的身份，所以，各编程语言都提供了基于Session的处理机制，Session是服务器端程序维护的一个类似`Map`的数据，
每个客户端都有一个唯一的Key对应到此处的某个值！所以，各个客户端的访问时，都可以向自己对应的Session数据中存入数据，后续，也可以取出之前存入的数据，
例如，可以在登录成功后将用户的id存入到Session中，后续，就可以根据“Session中有没有此id”来判断用户是否登录了，并根据存入的用户id来识别用户的身份。

在Session的具体使用过程中，当某个客户端第1次向服务器端发出请求时，并没有所谓的`Map`的Key，则服务器会会自动生成一个Key响应到客户端去，
客户端会自动将此Key保存下来，并在后续每次发出请求时都自动携带这个Key！在此过程中，客户端还会使用Cookie技术将Key保存在客户端！

Session中的Key本身上都是UUID值，本身并没有具体的信息含义，只是具有唯一性，使得各客户端访问服务器端的Session时不会发生冲突。

目前，并不推荐使用Session技术来处理识别用户的身份，因为在集群架构中，同一个用户的多次请求可能是由集群中不同的服务器进行处理，
而Session是存储在服务器内存中的数据，直接使用的话，就会无法识别用户的身份！



## 关于Token

Token可以称之为“票据”、“令牌”，其最大的特点是类似于Session的Key这样的数据中是体现信息含义的！相当于“火车票”，票上是可以体现一些数据的，
服务器就相当于“车站”，不同的车站都有相同的验票机制，能够识别“火车票”的真伪，并从中获取某些信息。



## 关于JWT

JWT = Json Web Token，相比普通Token，它使用JSON封装更多的信息含义。

为了保证JWT数据在网络上传输时的安全，JWT本身是一组加密后的数据，通常，有相关的工具包来负责生成JWT并解析JWT中的数据。

可以选择使用`jjwt`工具包来实现，在项目中，添加依赖项：

```xml
<!-- JJWT（Java JWT） -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt</artifactId>
    <version>0.9.1</version>
</dependency>
```

可以创建测试类，测试**生成JWT**和**解析JWT**：

```java
public class JwtTests {

    // 密钥
    String secretKey = "jfdsakjdsfk%&JFDsfFDFADSFhj875421dsafhjafdsfdsalkjafdsafds";

    @Test
    public void testGenerateJwt() {
        // 准备Claims
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", 9527);
        claims.put("name", "刘老师");

        // 准备过期时间：1分钟
        Date expirationDate = new Date(System.currentTimeMillis() + 1 * 60 * 1000);

        // JWT的组成部分：Header（头）、Payload（载荷）、Signature（签名）
        String jwt = Jwts.builder()
                // Header：用于配置算法与此结果数据的类型
                // 通常配置2个属性：typ（类型）、alg（算法）
                .setHeaderParam("typ", "jwt")
                .setHeaderParam("alg", "HS256")
                // Payload：用于配置需要封装到JWT中的数据
                .setClaims(claims)
                .setExpiration(expirationDate)
                // Signature：用于指定算法与密钥（盐）
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
        System.out.println(jwt);
        // eyJ0eXAiOiJqd3QiLCJhbGciOiJIUzI1NiJ9
        // .
        // eyJuYW1lIjoi5YiY6ICB5biIIiwiaWQiOjk1MjcsImV4cCI6MTY1NzYxOTY2Nn0
        // .
        // kDW_hgQKbBb01WA5kQeMaxY8Fc_H2Yao2DdFABlbuiw
    }

    @Test
    public void testParseJwt() {
        String jwt = "eyJ0eXAiOiJqd3QiLCJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoi5YiY6ICB5biIIiwiaWQiOjk1MjcsImV4cCI6MTY1NzY3NTExNX0.yMG3xL4b2SCNjaqwrIHB3tfA9HHmkiiLzpuYzJCSlog";
        Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwt).getBody();
        Object id = claims.get("id");
        Object name = claims.get("name");
        System.out.println("id=" + id);
        System.out.println("name=" + name);
    }

}
```

解析JWT时，可能会出现一些异常，例如：

- 当JWT数据过期时：

  ```
  io.jsonwebtoken.ExpiredJwtException: 
  
  JWT expired at 2022-07-13T09:18:35Z. Current time: 2022-07-13T09:27:35Z, a difference of 540694 milliseconds.  Allowed clock skew: 0 milliseconds.
  ```

- 当生成和解析使用的密钥不一致时，或JWT数据的第二部分，最后一部分被恶意篡改时：

  ```
  io.jsonwebtoken.SignatureException: 
  
  JWT signature does not match locally computed signature. JWT validity cannot be asserted and should not be trusted.
  ```

- 当JWT数据的第1部分被恶意篡改时：

  ```
  io.jsonwebtoken.MalformedJwtException: 
  
  Unable to read JSON value: {"t{�:"jwt","alg":"HS256"}
  ```



## 在项目中使用JWT

在项目中使用JWT时，通常需要关注的问题：

- 什么时候生成JWT：通常是登录成功之后，将生成JWT，且会将JWT响应到客户端
- 客户端什么时候携带JWT来访问服务器端：服务器端不关心
- 什么时候检查JWT：?????



## 向客户端响应JWT

当需要向客户端响应JWT时，需要：

- 在`AdminServiceImpl`的`login()`中，获取`authenticate()`返回的结果，将此结果转换成`User`类型，
  即可从此`User`类型中获取当初在`UserDetailsService`中存入的数据，然后，将必要的部分取出（暂时为`username`），
  将其生成为JWT数据（参考测试类，暂不考虑封装工具类）
- 在`IAdminService` 接口中将`login()`的返回值改为`String`
- 在`AdminServiceImpl`类中也将`login()`的返回值改为`String`，并返回JWT数据
- 在`AdminController`处理登录的方法中，调用Service组件的方法时获取返回值，并将此返回值封装到响应结果中

关于`AdminServiceImpl`中的实现代码：

```
@Override
public String login(AdminLoginDTO adminLoginDTO) {
    log.debug("开始处理管理员登录的业务，参数：{}", adminLoginDTO);

    // 调用AuthenticationManager执行Spring Security的认证
    Authentication authentication
            = new UsernamePasswordAuthenticationToken(
                    adminLoginDTO.getUsername(), adminLoginDTO.getPassword());
    Authentication loginResult = authenticationManager.authenticate(authentication);

    // 以上调用的authenticate()方法是会抛出异常的方法，如果还能执行到此处，则表示用户名与密码是匹配的
    log.debug("登录成功！认证方法返回：{} >>> {}", loginResult.getClass().getName(), loginResult);
    // 从认证结果中获取Principal，本质上是User类型，且是UserDetailsService中loadUserByUsername()返回的结果
    log.debug("尝试获取Principal：{} >>> {}", loginResult.getPrincipal().getClass().getName(), loginResult.getPrincipal());
    User user = (User) loginResult.getPrincipal();
    String username = user.getUsername();
    log.debug("登录成功的用户名：{}", username);

    // 应该在此处生成JWT数据，向JWT中存入：id（暂无）, username, 权限（暂无）
    Map<String, Object> claims = new HashMap<>();
    claims.put("username", user.getUsername());
    Date expiration = new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000);
    String jwt = Jwts.builder()
            .setHeaderParam("typ", "jwt")
            .setHeaderParam("alg", "HS256")
            .setClaims(claims)
            .setExpiration(expiration)
            .signWith(SignatureAlgorithm.HS256, "lkjfdslkjafds8iufnmdsfadsa")
            .compact();
    log.debug("生成JWT数据：{}", jwt);
    return jwt;
}
```



## 关于客户端携带JWT数据

当客户端尝试访问需要认证才能请求的资源时，客户端应该携带JWT数据，而服务器端应该对JWT数据进行获取、检查、解析等处理。

当客户端携带JWT时，通常会将JWT数据放在请求头（Request Header）中的`Authorization`属性中，并且，通常，
服务器端的程序都会设计为从请求头中的`Authorization`属性中获取JWT数据。


## 服务器端检查JWT

由于许多不同的请求都需要检查JWT，所以，不会在控制器中处理JWT！

通常，应该在过滤器组件中检查JWT！

- 过滤器是Java服务器端程序（无论你使用什么框架）中最早接收到客户端请求的组件，且所有请求都会经过过滤器才会执行到控制器
- Xxxxx

需在自定义过滤器类：

```java
/**
 * <p>处理JWT的过滤器</p>
 *
 * <p>此过滤器将尝试获取请求中的JWT数据，如果存在有效数据，将尝试解析，</p>
 * <p>然后，将解析得到的结果存入到Spring Security的上下文中，</p>
 * <p>以至于Spring Security框架中的其它组件能够从上下文中获取到用户的信息，</p>
 * <p>从而完成后续的授权访问。</p>
 */
@Slf4j
@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    // 最终，过滤器可以选择“阻止”或“放行”
    // 如果选择“阻止”，则后续的所有组件都不会被执行
    // 如果选择“放行”，会执行“过滤器链”中剩余的部分，甚至继续向后执行到控制器等组件
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        // 此方法是任何请求都会执行的方法
        log.debug("执行JwtAuthorizationFilter");

        // 清除Security的上下文
        // 如果不清除，只要此前存入过信息，即使后续不携带JWT，上下文中的登录信息依然存在
        SecurityContextHolder.clearContext();

        // 从请求头中获取JWT
        String jwt = request.getHeader("Authorization");
        log.debug("从请求头中获取的JWT数据：{}", jwt);

        // 先判断是否获取到了有效的JWT数据，如果无JWT数据，直接放行
        if (!StringUtils.hasText(jwt)) {
            log.debug("请求头中的JWT数据是无效的，直接放行");
            filterChain.doFilter(request, response);
            return;
        }

        // 如果获取到了有效的JWT值，则尝试进行解析
        Claims claims = Jwts.parser().setSigningKey("lkjfdslkjafds8iufnmdsfadsa")
                .parseClaimsJws(jwt).getBody();
        Object username = claims.get("username");
        log.debug("从JWT中解析得到用户名：{}", username);

        // TODO 临时：准备用户权限
        GrantedAuthority authority = new SimpleGrantedAuthority("1");
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(authority);

        // 当解析成功后，应该将相关数据存入到Spring Security的上下文中
        Authentication authentication
                = new UsernamePasswordAuthenticationToken(username, null, authorities);
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);

        // 以下代码将执行“放行”
        filterChain.doFilter(request, response);
    }

}
```

并在配置类中添加配置：

```java
package cn.tedu.csmall.passport.config;

import cn.tedu.csmall.passport.filter.JwtAuthorizationFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtAuthorizationFilter jwtAuthorizationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        log.debug("创建密码编码器组件：BCryptPasswordEncoder");
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 在配置路径时，可以使用星号作为通配符
        // 使用 /* 只能匹配1层级路径，例如 /user 或 /brand，不可以匹配多层级，例如不可以匹配到 /user/list
        // 使用 /** 可以匹配若干层级路径

        // 白名单，不需要登录就可以访问
        String[] urls = {
                "/admins/login",
                "/doc.html",
                "/**/*.css",
                "/**/*.js",
                "/favicon.ico",
                "/v2/api-docs",
                "/swagger-resources"
        };

        http.csrf().disable(); // 禁用防止跨域访问，如果无此配置，白名单路径的异步访问也会出现403错误

        http.authorizeRequests() // 请求需要被授权才可以访问
                .antMatchers(urls) // 匹配某些路径
                .permitAll() // 允许直接访问（不需要经过认证和授权）
                .anyRequest() // 除了以上配置过的其它任何请求
                .authenticated(); // 已经通过认证，即已经登录过才可以访问

        // 添加处理JWT的过滤器，必须执行在处理用户名、密码的过滤器（Spring Security内置）之前
        http.addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);
    }

}
```

## 完整编码流程：基于Security+JWT的管理员登录

- 相关依赖：`spring-boot-starter-security`、`jjwt`
- 创建管理员登录的VO类，例如`AdminLoginVO`
- 在`AdminMapper`接口和`AdminMapper.xml`文件中实现：根据用户名查询管理员信息，应该至少包括：用户名、密码、权限
- 自定义类，实现`UserDetailsService`接口，重写`loadUserByUsername()`方法，在此类中通过`AdminMapper`的查询找到对应的管理员信息，
  并封装到`UserDetails`类型的对象中返回
- 创建Security配置类，继承自`WebSecurityConfigurerAdapter`类，在此类中使用`@Bean`方法得到`AuthenticationManager`对象，
  使用`@Bean`方法得到`BCryptPasswordEncoder`对象
- 在`IAdminService`接口中添加登录的抽象方法，并在`AdminServiceImpl`中重写此方法，在方法体中，调用`AuthenticationManager`
  的`authenticate()`执行认证，如果认证通过，应该生成JWT数据并返回，此JWT数据中应该包含用户名及必要信息
- 在`AdminController`中处理登录请求，并通过调用`IAdminService`类型的组件来实现，将调用得到的JWT响应到客户端去


## 完整编码流程：登录后的访问

- 在Security的配置类中，指定一些白名单，这些是不需要登录就可以直接访问的，其它请求路径都必须登录后才可以访问，
  需要注意：登录、注册等请求路径必须在白名单，否则不合理
- 创建JWT过滤器，在此过滤器中：
  - 清除Security的上下文
  - 从请求头中获取JWT
  - 对JWT数据进行基本判断（是否有值），如果没有有效值，直接放行
  - 如果获取到有效的JWT，则解析，得到用户信息，将用户信息存入到上下文中
- 在Security的配置类中，添加以上过滤器，将其添加在`UsernamePasswordAuthenticationFilter`之前


## 实现授权访问

实现授权访问的步骤：

- 当用户尝试登录时，应该根据用户名从数据库中查询出此管理员的权限信息

- 在`UserDetailsServiceImpl`中，（当登录认证时，Spring Security框架会自动调用此类中的`loadUserByUesrname()`方法），
  根据用户名查询到有效管理员信息后，向`UserDetails`中存入权限信息

  - 将`List<String>`格式的权限集合转换成`String...`格式即可，例如：

    ```
    admin.getPermissions().toArray(new String[] {})
    ```

- 在`AdminServiceImpl`的`login()`中，认证成功后，从返回的`Authentication`中取出权限信息，并其生成到JWT中

  - 为保证后续能从JWT中取出权限且还原成正常的格式，应该将权限列表（`Collection<? extend GrandtedAuthority>`）转换成JSON格式的字符串再写入

- 在`JwtAuthorizationFilter`中，从JWT中解析出权限，并存入到Security的上下文中

  - 从JWT中解析出的权限是JSON格式的字符串，需还原成`Collection<? extend GrandtedAuthority>`类型才可以存入到Security的上下文中，
    可以还原成`List<SimpleGrantedAuthority>`

- 在Security的配置类`SecurityConfiguration`上添加注解`@EnableGlobalMethodSecurity(prePostEnabled = true)`以开启全局的授权访问检查

  - 此配置是一次性的配置

- 在控制器中，在处理请求的方法上，使用`@PreAuthorize`注解，配置其中的`hasAuthority`属性，即可要求此请求必须具有某种权限

  - 例如：`@PreAuthorize("hasAuthority('/ams/admin/read')")`



## 根据用户名查询管理员的权限

首先，在`AdminLoginVO`中添加必要的属性：

```java
package cn.tedu.csmall.passport.pojo.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class AdminLoginVO implements Serializable {

    /**
     * 管理员的id
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码（密文）
     */
    private String password;

    /**
     * 账号是否启用，0=禁用，1=启用
     */
    private Integer enable;

    /**
     * 此账号的权限列表
     */
    private List<String> permissions;

}
```

然后，在`AdminMapper.xml`中配置查询：

```
<!-- AdminLoginVO getByUsername(String username); -->
<select id="getByUsername" resultMap="LoginResultMap">
    SELECT
        ams_admin.id,
        ams_admin.username,
        ams_admin.password,
        ams_admin.enable,
        ams_permission.value
    FROM ams_admin
    LEFT JOIN ams_admin_role ON ams_admin.id=ams_admin_role.admin_id
    LEFT JOIN ams_role_permission ON ams_admin_role.role_id=ams_role_permission.role_id
    LEFT JOIN ams_permission ON ams_role_permission.permission_id=ams_permission.id
    WHERE
        ams_admin.username=#{username}
</select>

<resultMap id="LoginResultMap" type="cn.tedu.csmall.passport.pojo.vo.AdminLoginVO">
    <id column="id" property="id" />
    <result column="username" property="username" />
    <result column="password" property="password" />
    <result column="enable" property="enable" />
    <collection property="permissions" ofType="java.lang.String">
        <constructor>
            <arg column="value" />
        </constructor>
    </collection>
</resultMap>
```



## 关于JWT过滤器的处理细节

解析JWT是可能失败的，例如JWT数据过期、签名错误、数据非法等，这些错误都应该被处理，否则，就会存在异常未处理的情况，最终将导致500错误！

关于以上可能的错误，应该大致分为3类，一类是JWT数据过期，一类是JWT数据被恶意篡改，再另外还有可能是其它的错误。

首先，先在`ServiceCode`中添加新的业务状态码，对应一些错误：

```
/**
 * 错误：JWT数据错误，可能被恶意篡改
 */
public static final int ERR_JWT_INVALID = 40001;
/**
 * 错误：JWT过期
 */
public static final int ERR_JWT_EXPIRED = 40300;
```

然后，需要在JWT过滤器中，自行使用`try...catch`来捕获并处理异常！



## 在登录的用户身份标识中添加自定义信息

Spring Security框架中并没有使用、封装用户的ID等相关信息，如果使用过程中，需要自行封装更多的信息，并添加到用户身份标识中，则需要：

- 自定义类实现`UserDetails`接口
- 或，自定义类继承`User`类

并且，在自定义类中添加所需的属性，例如ID，然后，在`UserDetailsService`的实现类中，在`loadUserByUsername()`方法返回自定义类的对象。

所以，创建`AdminDetails`类：

```java
@Setter
@Getter
@EqualsAndHashCode
@ToString(callSuper = true)
public class AdminDetails extends User {

    private Long id;

    public AdminDetails(String username, String password, boolean enabled,
                        Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled,
                true, true, true,
                authorities);
    }

}
```

在`UserDetailsServiceImpl`中，需要返回时：

```
List<String> permissions = admin.getPermissions();
List<SimpleGrantedAuthority> authorities = new ArrayList<>();
for (String permission : permissions) {
    authorities.add(new SimpleGrantedAuthority(permission));
}

AdminDetails adminDetails = new AdminDetails(
        admin.getUsername(),
        admin.getPassword(),
        admin.getEnable() == 1,
        authorities
);
adminDetails.setId(admin.getId());
log.debug("即将向Spring Security返回AdminDetails：{}", adminDetails);
```

接下来，在`AdminServiceImpl`的`login()`方法中，通过`AuthenticationManager`的`authenticate()`执行认证且通过认证的返回结果就是以上`AdminDetails`对象，所以，可以从中获取管理员的id，并用于生成JWT数据，则用户登录成功后得到的JWT数据中将包含Id信息。

后续，客户端提交请求时，携带的JWT也是包含Id信息的，可以在`JwtAuthenticationFilter`中解析得到此Id，最终，此Id值应该封装到Security的上下文中，则可以利用`UsernamePasswordAuthenticationToken`类的`principal`属性（`Object`类型），所以，自定义类，用于封装后续可能需要使用到的管理员信息：

```java
package cn.tedu.csmall.passport.security;

import lombok.Data;

import java.io.Serializable;

/**
 * 当前登录的当事人
 *
 * @author java@tedu.cn
 * @version 0.0.1
 */
@Data
public class LoginPrincipal implements Serializable {

    /**
     * 当前登录的用户id
     */
    private Long id;

    /**
     * 当前登录的用户名
     */
    private String username;

}
```

然后，在过滤器，将其存入：

```
// 准备当前登录用户的当事人信息
LoginPrincipal loginPrincipal = new LoginPrincipal();
loginPrincipal.setId(Long.parseLong(id.toString()));
loginPrincipal.setUsername(username.toString());

// 当解析成功后，应该将Authentication存入到Spring Security的上下文中
Authentication authentication
        = new UsernamePasswordAuthenticationToken(loginPrincipal, null, authorities);
//                                             == 以上封装了当事人信息 ==
SecurityContext securityContext = SecurityContextHolder.getContext();
securityContext.setAuthentication(authentication);
log.debug("已经向Security的上下文中写入：{}", authentication);
```

至此，当客户端携带JWT访问服务器端时，服务器端的Security的上下文中就包含了管理员的id、用户名、权限，其中，权限不需要自行使用，都是Security框架自动判断（你只需要在控制器处理请求的方法上配置`@PreAuthorize`注解即可），当需要获取管理员的id、用户名时，可以在控制器处理请求的方法的参数列表中添加`Authentication`即可，此参数就是Security上下文中的认证信息（过滤器中存入的对象），例如：

```
// http://localhost:9081/admins
@ApiOperation("查询管理员列表")
@ApiOperationSupport(order = 401)
@PreAuthorize("hasAuthority('/ams/admin/read')")
@GetMapping("")
public JsonResult list(Authentication authentication) {
    log.debug("接收到查询管理员列表的请求");
    log.debug("当前认证信息：{}", authentication);
    LoginPrincipal principal = (LoginPrincipal) authentication.getPrincipal();
    Long id = principal.getId();
    log.debug("从认证信息中获取当前登录的管理员的id：{}", id);
    String username = principal.getUsername();
    log.debug("从认证信息中获取当前登录的管理员的用户名：{}", username);
    List<AdminListItemVO> admins = adminService.list();
    return JsonResult.ok(admins);
}
```

从`Authentication`中获取`LoginPrincipal`比较麻烦，还需要自行获取、转换类型，可以改为声明`LoginPricipal`参数（在过滤器中封装到`UsernamePasswordAuthenticationToken`的`pricipal`属性中的对象），然后，在此参数前添加`@AuthenticationPrincipal`注解，即可直接使用：

```
// http://localhost:9081/admins
@ApiOperation("查询管理员列表")
@ApiOperationSupport(order = 401)
@PreAuthorize("hasAuthority('/ams/admin/read')")
@GetMapping("")
public JsonResult list(@AuthenticationPrincipal LoginPrincipal loginPrincipal) {
    log.debug("接收到查询管理员列表的请求");
    log.debug("当前认证信息中的当事人信息：{}", loginPrincipal);
    Long id = loginPrincipal.getId();
    log.debug("从认证信息中获取当前登录的管理员的id：{}", id);
    String username = loginPrincipal.getUsername();
    log.debug("从认证信息中获取当前登录的管理员的用户名：{}", username);
    List<AdminListItemVO> admins = adminService.list();
    return JsonResult.ok(admins);
}
```


## SSO：Single Sign On，即：单点登录

单点登录表现为：在集群或分布式系统中，客户端在其中的某1个服务器登录，后续的请求被分配到其它服务器处理时，其它服务器也能识别用户的身份。

单点登录的实现方案有：

- 共享Session
  - 把所有客户端的Session数据存储到专门的服务器上，其它任何服务器需要识别客户端身份时，都从这个专门的服务器上去查找、读取Session数据
  - 缺点：Session的有效期不宜过长
  - 优点：编码简单，读取Session数据基本上没有额外牺牲性能
  
- Token
  - 当某客户端登录成功，服务器端将响应Token到客户端，在后续的访问中，客户端自行携带Token数据来访问任何服务器，且任何服务器都具备
    解析此Token的功能，即可识别客户端的身份
  - JWT（JSON Web Token）也是Token的一种
  - 缺点：编写代码略难，需要频繁解析JWT，需要牺牲一部分性能来进行解析
  - 优点：可以长时间有效

## 作业：实现`csmall-business`中的认证访问

目标：原`csmall-business`项目中的所有功能都是必须登录以后才可以访问的。

开发步骤：

- 在`csmall-business`项目中添加相关依赖项

- 在项目的启动类的`@SpringBootApplication`注解中排除`UserDetailsServiceAutoConfiguration`

  - ```java
    @SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
    ```

  - 不再使用临时的`user`用户名和随机密码

- 创建`LoginPrincipal`类

- 【手写】使用`JwtAuthenticationFilter`获取、解析JWT，并将当事人信息封装到`Authentication`中，并存入到Spring Security的上下文中

- 创建`SecurityConfigration`类（无需：`PasswordEncoder`、`AuthenticationManager`对象）

  - 需要`http.cors()` // 允许通过客户端的OPTIONS类型(预检机制)的请求
  - 需要`http.csrf().disable()`
  - 所有请求都必须登录才可以访问

注意：原有的所有客户端请求都必须携带JWT。










随笔：
10进制   2进制    16进制
0       0000      0
1       0001      1
2       0010      2
3       0011      3
4       0100      4
5       0101      5
6       0110      6
7       0111      7
8       1000      8
9       1001      9
10      1010      A
11      1011      B
12      1100      C
13      1101      D
14      1110      E
15      1111      F

消息摘要算法（哈希算法-散列算法）：主要 用于 检验数据的完整性










validation框架 未讲