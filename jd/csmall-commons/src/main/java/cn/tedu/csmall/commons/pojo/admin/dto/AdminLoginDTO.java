package cn.tedu.csmall.commons.pojo.admin.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 管理員登入數據
 */
@Data
public class AdminLoginDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    //序列化id沒有填寫的話會自動生成

    @ApiModelProperty(value="用戶名",required = true)
    private String username;

    @ApiModelProperty(value = "密碼",required = true)
    private String password;
}
