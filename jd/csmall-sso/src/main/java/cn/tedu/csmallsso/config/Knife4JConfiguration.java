package cn.tedu.csmallsso.config;

import com.github.xiaoymin.knife4j.spring.extension.OpenApiExtensionResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

/**
 * Knife4j（Swagger2）的配置
 */
@Configuration
@EnableSwagger2WebMvc
public class Knife4JConfiguration {

    /**
     * 【重要】指定Controller包路径
     */
    private String basePackage = "cn.tedu.csmallsso.controller";
    /**
     * 分组名称
     */
    private String groupName = "base-sso";
    /**
     * 主机名
     */
    private String host = "http://java.tedu.cn";
    /**
     * 标题
     */
    private String title = "酷鲨商城项目案例在线API文档--基础sso-web实例";
    /**
     * 简介
     */
    private String description = "构建基础sso-web项目,实现购买";
    /**
     * 服务条款URL
     */
    private String termsOfServiceUrl = "http://www.apache.org/licenses/LICENSE-2.0";
    /**
     * 联系人
     */
    private String contactName = "项目研发部";
    /**
     * 联系网址
     */
    private String contactUrl = "http://java.tedu.cn";
    /**
     * 联系邮箱
     */
    private String contactEmail = "java@tedu.cn";
    /**
     * 版本号
     */
    private String version = "1.0-SNAPSHOT";

    @Autowired
    private OpenApiExtensionResolver openApiExtensionResolver;

    @Bean
    public Docket docket() {
        String groupName = "1.0-SNAPSHOT";
        Docket docket = new Docket(DocumentationType.SWAGGER_2)
                .host(host)
                .apiInfo(apiInfo())
                .groupName(groupName)
                .select()
                .apis(RequestHandlerSelectors.basePackage(basePackage))
                .paths(PathSelectors.any())
                .build()
                .extensions(openApiExtensionResolver.buildExtensions(groupName));
        return docket;
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(title)
                .description(description)
                .termsOfServiceUrl(termsOfServiceUrl)
                .contact(new Contact(contactName, contactUrl, contactEmail))
                .version(version)
                .build();
    }
}
