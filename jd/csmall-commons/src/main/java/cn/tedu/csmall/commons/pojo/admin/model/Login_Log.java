package cn.tedu.csmall.commons.pojo.admin.model;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Login_Log implements Serializable {

    //序列化id 反序列化時方便對應
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long adminId;
    private String username;
    private String nickname;
    private String ip;
    private String userAgent;

    private LocalDateTime gmtLastLogin;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
}
