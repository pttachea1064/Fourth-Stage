package cn.tedu.csmall.commons.pojo.admin.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel(value = "權限更新DTO")
@Data
public class PermissionUpdateDTO implements Serializable {

    private static final long seriaVersionUID = 1L;

    //權限id
    @ApiModelProperty(value = "權限id",required = true)
    private Long id ;

//    ---------------------------------------------------------------------------------------------------------
    //權限名稱
    @ApiModelProperty(value = "權限名稱")
    private String name;
    //權限數值
    @ApiModelProperty(value = "權限數值")
    private String value;
    //權限描述
    @ApiModelProperty(value = "權限描述")
    private String description;

    //權限自訂排序
    @ApiModelProperty(value = "權限自訂排序")
    private String sort;
}
