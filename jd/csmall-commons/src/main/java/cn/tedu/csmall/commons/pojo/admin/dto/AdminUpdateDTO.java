package cn.tedu.csmall.commons.pojo.admin.dto;

import com.sun.istack.internal.NotNull;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@ApiModel(value ="後臺帳號更新DTO")
public class AdminUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;//序列化id是自己指定
    //設定這個id原因是為了方便反序列化,因為反序列化會需要根據序列化id進行轉化

    //帳號更新 必須指定id
    @ApiModelProperty(value = "管理員id",required = true)
    @NotNull
    private Long id;

    //    ------------------------------------------------------------------

    //用戶名
    @ApiModelProperty(value = "管理員用戶名")
    private String username;

    //密碼
    @ApiModelProperty(value = "管理員密碼")
    private String password;

    /*確認密碼*/
    @ApiModelProperty(value = "管理員確認密碼")
    private String passwordAct;

    //暱稱
    @ApiModelProperty(value = "管理員暱稱")
    private String nickname;

    //大頭照(頭部相片) url
    @ApiModelProperty(value = "管理員大頭照url")
    private String avatar;

    //手機號碼/聯繫方式
    @ApiModelProperty(value = "手機號碼")

    private String phone;

    //電子信箱/郵件
    @ApiModelProperty(value = "信箱")

    private String email;

    //描述
    @ApiModelProperty(value = "管理員描述")
    private String description;

    //是否啟用, 1=啟用狀態 , 0=禁止使用
    @ApiModelProperty(value = "是否啟用,1=啟用, 0=禁用")
    private Integer enable;


}
