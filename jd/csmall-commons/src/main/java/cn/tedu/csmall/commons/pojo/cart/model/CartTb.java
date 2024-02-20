package cn.tedu.csmall.commons.pojo.cart.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class CartTb implements Serializable {
    private Integer id;
    // 商品编号
    private String commodityCode;
    // 价格
    private Integer price;
    // 数量
    private Integer count;
    // 用户id
    private String userId;

}
