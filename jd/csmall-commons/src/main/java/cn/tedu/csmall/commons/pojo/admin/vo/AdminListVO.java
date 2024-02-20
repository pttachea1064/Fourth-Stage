package cn.tedu.csmall.commons.pojo.admin.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 管理員列表數據(列表項目)
 */
@Data
public class AdminListVO implements Serializable {
    private static final long serialVersionUID = 1L;


    @ApiModelProperty(value = "管理员id")
    private Long id; //管理员id
    @ApiModelProperty(value = "管理员名称")
    private String username; //管理员名称
    @ApiModelProperty(value = "管理员昵称")
    private String nickname; //管理员昵称
    @ApiModelProperty(value = "管理员手机号")
    private String phone; //管理员手机号
    @ApiModelProperty(value = "电子邮箱")
    private String email; //电子邮箱
    @ApiModelProperty(value = "是否启用,1=启用，0=禁用")
    private Integer enable; //是否启用

}
