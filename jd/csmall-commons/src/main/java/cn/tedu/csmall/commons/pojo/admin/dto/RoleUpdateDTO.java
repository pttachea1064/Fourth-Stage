package cn.tedu.csmall.commons.pojo.admin.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@ApiModel(value = "角色更新DTO")
public class RoleUpdateDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    //角色id
    @ApiModelProperty(value = "角色id",required = true)
    private Long id ;
//    ------------------------------------------------------------------
    //角色名稱
    @ApiModelProperty(value= "角色名稱")
    private String name;

    //角色描述
    @ApiModelProperty(value= "角色描述")
    private String description;

    //自訂排序序號
    @ApiModelProperty(value ="自訂排序序號")
    private Integer sort;
}
