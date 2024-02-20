package cn.tedu.csmall.commons.pojo.admin.model;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 管理員的表
 * */
@Data
public class Admin implements Serializable {

    //序列化id 反序列化時方便對應
    private static final long serialVersionUID = 1L;

    private Long id ;
    private String username;
    private String password;
    private String nickname;
    private String avatar;
    private String phone;
    private String email;
    private String description;
    private Integer enable;
    private String lastLoginIp;
    private Integer loginCount;
    private LocalDateTime gmtLastLogin;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
}
