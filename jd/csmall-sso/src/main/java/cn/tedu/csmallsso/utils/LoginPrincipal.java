package cn.tedu.csmallsso.utils;

import lombok.Data;

import java.io.Serializable;

/**
 * 当前登录人的id和用户名
 */
@Data
public class LoginPrincipal implements Serializable {
    //当前登录用户的用户id
    private Long id;
    //当前登录用户的用户名称
    private String username;
}