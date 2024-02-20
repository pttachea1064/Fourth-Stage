package cn.tedu.csmall.commons.pojo.admin.model;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Permission implements Serializable {

    //序列化id 反序列化時方便對應
    private static final long serialVersionUID = 1L;


    private Long id ;
    private String name;
    private String value;
    private String description;
    private Integer sort;

    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
}
