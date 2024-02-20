package cn.tedu.csmall.commons.pojo.admin.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;


/**
 * 管理員登入訊息數據
 * */
@Data
public class AdminLoginVO implements Serializable {
    private static final long serialVUID = 1L;

    //管理員id
    private Long id ;
    //管理員帳戶
    private String username;
    //管理員密碼
    private String password;
    //該帳號啟用狀態: 1 表示啟用 0 表示禁用
    private Integer enable;
    //權限列表
    private List<String> permissions;
}
