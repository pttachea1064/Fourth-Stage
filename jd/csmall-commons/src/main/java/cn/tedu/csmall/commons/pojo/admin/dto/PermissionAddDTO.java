package cn.tedu.csmall.commons.pojo.admin.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel(value = "權限新增DTO")
@Data
public class PermissionAddDTO implements Serializable {

    private static final long seriaVersionUID = 1L;

    //權限名稱
    @ApiModelProperty(value = "權限名稱",notes = "權限責任描述",required = true)
    private String name;
    //權限數值
    @ApiModelProperty(value = "權限數值",notes = "用來控制權限具體的數值",required = true)
    private String value;
    //權限描述
    @ApiModelProperty(value = "權限描述",required = true)
    private String description;

    //權限自訂排序
    @ApiModelProperty(value = "權限自訂排序",notes = "自訂排序",required = true)
    private String sort;
}
