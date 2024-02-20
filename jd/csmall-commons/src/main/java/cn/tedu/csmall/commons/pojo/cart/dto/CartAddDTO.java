package cn.tedu.csmall.commons.pojo.cart.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

// knife4j框架中解释实体类的注解
@ApiModel("新增购物车商品信息的DTO")
@Data
public class CartAddDTO implements Serializable {
    @ApiModelProperty(value = "商品编号",name="commodityCode" , example = "PC100")
    private String commodityCode; // 商品编号
    @ApiModelProperty(value = "商品单价",name="price" , example = "188")
    private Integer price;        // 商品单价
    @ApiModelProperty(value = "商品个数",name="count" , example = "5")
    private Integer count;        // 商品个数
    @ApiModelProperty(value = "用户ID",name="userId" , example = "UU100")
    private String userId;        // 用户ID




}
