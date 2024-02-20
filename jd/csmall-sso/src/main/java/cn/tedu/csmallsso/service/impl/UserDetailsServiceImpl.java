package cn.tedu.csmallsso.service.impl;

import cn.tedu.csmall.commons.pojo.admin.vo.AdminLoginVO;
import cn.tedu.csmallsso.utils.AdminDetails;
import cn.tedu.csmallsso.mapper.AdminMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private AdminMapper adminMapper;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        //输入数据库中的用户名，浏览器会返回UserDetailsService return null;
        log.info("Spring Security自动根据用户{},查询用户详情",userName);
        //只允许maxn用户的登录逻辑
//        if("maxn".equals(userName)){
//            UserDetails userDetails = User.builder()
//                    .username("maxn")
//                    .password("$2a$10$1TUjrx1j6gsQfEytHMdWMOlHE6U9dv4RITOE7WI2VV6QmzQCvCrOi")
//                    .disabled(false) //账号是否禁用
//                    .accountLocked(false) //账号是否锁定
//                    .accountExpired(false) //账号是否过期
//                    .credentialsExpired(false) //认证是否过期
//                    .authorities("p1")  //账号的权限信息
//                    .build(); //对象构建
//            return userDetails;
//        }

       /* if(adminLoginVO !=null){
            log.info("查询到匹配的管理员的信息：{}",adminLoginVO);
            UserDetails userDetails = User.builder()
                    .username(adminLoginVO.getUsername())
                    .password(adminLoginVO.getPassword())
                    .disabled(false) //账号是否禁用
                    .accountLocked(false) //账号是否锁定
                    .accountExpired(false) //账号是否过期
                    .credentialsExpired(false) //认证是否过期
                    .authorities("p1")  //账号的权限信息
                    .build();//对象构建
            return userDetails;
        }*/
        AdminLoginVO adminLoginVO = adminMapper.getUserByUserName(userName);
        //将从数据库中获取的权限赋值到AdminDetails中
        if(adminLoginVO != null){
            List<String> permissions = adminLoginVO.getPermissions();
            List<SimpleGrantedAuthority> authorities = new ArrayList<>();
            for (String permission : permissions) {
                authorities.add(new SimpleGrantedAuthority(permission));
            }

            AdminDetails adminDetails = new AdminDetails(
                    adminLoginVO.getUsername(),
                    adminLoginVO.getPassword(),
                    adminLoginVO.getEnable()==1,
                    authorities
            );
            adminDetails.setId(adminLoginVO.getId());
            return adminDetails;
        }
        return null;
    }
}
