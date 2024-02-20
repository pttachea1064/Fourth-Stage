package cn.tedu.csmall.commons.pojo.admin.model;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Role_Permission implements Serializable {
    //序列化id 反序列化時方便對應
    private static final long serialVersionUID = 1L;


    private Long id ;
    private Long roleId;
    private Long permissionId;

    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
}
