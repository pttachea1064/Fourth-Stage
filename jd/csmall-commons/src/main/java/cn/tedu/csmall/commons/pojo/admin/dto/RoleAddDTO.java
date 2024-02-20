package cn.tedu.csmall.commons.pojo.admin.dto;

import com.sun.istack.internal.NotNull;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@ApiModel(value = "角色新增DTO")
public class RoleAddDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    //角色名稱
    @ApiModelProperty(value= "角色名稱",required = true)
    private String name;

    //角色描述
    @ApiModelProperty(value= "角色描述",required = true)
    private String description;
    //自訂排序序號
    @ApiModelProperty(value ="自訂排序序號",required = true)
    private Integer sort;
}
