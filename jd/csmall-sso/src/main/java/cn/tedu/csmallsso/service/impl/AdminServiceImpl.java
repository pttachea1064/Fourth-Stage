package cn.tedu.csmallsso.service.impl;

import cn.tedu.csmall.commons.exception.CoolSharkServiceException;
import cn.tedu.csmall.commons.pojo.admin.dto.AdminAddDTO;
import cn.tedu.csmall.commons.pojo.admin.dto.AdminLoginDTO;
import cn.tedu.csmall.commons.pojo.admin.model.Admin;
import cn.tedu.csmall.commons.pojo.admin.model.Admin_Role;
import cn.tedu.csmall.commons.pojo.admin.vo.AdminListVO;
import cn.tedu.csmall.commons.restful.ResponseCode;
import cn.tedu.csmallsso.mapper.AdminRoleMapper;
import cn.tedu.csmallsso.utils.AdminDetails;
import cn.tedu.csmallsso.utils.JwtUtils;
import cn.tedu.csmallsso.mapper.AdminMapper;
import cn.tedu.csmallsso.service.IAdminService;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.JmxException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j //提供log对象
public class AdminServiceImpl implements IAdminService {
    @Autowired
    private AdminMapper adminMapper;

    public AdminServiceImpl(){
        log.info("创建admin业务逻辑对象");
    }

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AdminRoleMapper adminRoleMapper;


    @Override
    public void addAdmin(AdminAddDTO adminAddDTO) {
        log.info("开始执行添加admin的业务");

        //判断用户名和密码是否为null或者空串
        if(adminAddDTO.getUsername()==null || adminAddDTO.getUsername()==""
                || adminAddDTO.getPassword()==null || adminAddDTO.getPassword()==""){
            //直接抛出异常
            throw new CoolSharkServiceException(ResponseCode.INTERNAL_SERVER_ERROR,"用户名或密码不能为空");
        }
        //判断用户名是否存在
        int count = adminMapper.selectAdminByUserName(adminAddDTO.getUsername());
        //count大于0，就是存在该用户
        if(count>0){
            String message = "添加管理员失败，用户名【"+adminAddDTO.getUsername()+"】已经被占用";
            log.error(message);
            throw new CoolSharkServiceException(ResponseCode.CONFLICT,message);
        }
        //以上代码都没有问题，执行新增admin的操作
        //需要将AdminAddDTO转成Admin对象
        Admin admin = new Admin();
        //利用BeanUtils工具，进行同名属性赋值
        BeanUtils.copyProperties(adminAddDTO,admin);

        /**
         * 我們將原始的密碼從Admin Object中取出 加密後再次存入Admin Object當中
         */
        String rawPassword = admin.getPassword();
        String encodePassword = passwordEncoder.encode(rawPassword);
        admin.setPassword(encodePassword);
        /**
         * 我們將原始的密碼從Admin Object中取出 加密後再次存入Admin Object當中
         */

        //补全adminAddDTO中没有的属性
        admin.setLoginCount(0);
        admin.setLastLoginIp(null);
        admin.setGmtCreate(LocalDateTime.now());
        admin.setGmtModified(LocalDateTime.now());
        //将要添加的管理员插入到数据库中
        int rows = adminMapper.insert(admin);
        //如果rows==0，表示插入失败
        if(rows==0){
            throw new CoolSharkServiceException(ResponseCode.INTERNAL_SERVER_ERROR,"添加管理员失败！！！");
        }

        /**
         * 插入管理員與角色關聯的數據 讓以上添加的管理是被分配角色的
         */
        Admin_Role adminRole = new Admin_Role();
        adminRole.setAdminId(admin.getId());
        adminRole.setRoleId(2L); //暂时将管理员指定为2的角色
        log.info("管理员与角色的关联表添加关联数据，{}",adminRole);
        int adminRoleRows = adminRoleMapper.insert(adminRole);
        //如果影响行数不是1，就说明添加管理员失败
        if(adminRoleRows != 1){
            String message = "添加管理员失败，服务器繁忙，请稍后重试！！！";
            log.info(message);
            throw new CoolSharkServiceException(ResponseCode.INTERNAL_SERVER_ERROR,"添加失败");
        }



    }
    @Override
    public List<AdminListVO> listAdmins() {
        log.info("开始处理查询管理员列表的业务");
        List<AdminListVO> adminListVOS = adminMapper.listAdmins();
        return adminListVOS;
    }

    @Override  //重写
    public String login(AdminLoginDTO adminLoginDTO) {
        log.info("开始处理业务逻辑层");
        //利用Security进行认证，通过AuthenticationManager对象来执行认证过程(保存用户信息?、授权访问等需要)
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(adminLoginDTO.getUsername(),adminLoginDTO.getPassword());
        //查看返回结果相关信息，类名和数据(数据被Principal包含)
        Authentication loginResult = authenticationManager.authenticate(authentication);
        //以上调用的authenticate方法会抛出异常(抛出异常疑问程序的终止)，如果还能执行到以下代码，表示用户名与密码是匹配的
        log.info("登录成功!!!认证方法返回：类名为{},结果是：{}",loginResult.getClass().getName(),loginResult);
        //从结果数据中获取Principal
        log.info("尝试获取Principal：{}",loginResult.getPrincipal());
//        org.springframework.security.core.userdetails.User user = (User) loginResult.getPrincipal();
//        String username = user.getUsername(); //获取用户名
//        log.info("登录用户为{}",username);


        /**從AdminDetails中獲取id
         * */
        AdminDetails adminDetails = (AdminDetails) loginResult.getPrincipal();

        Long id = adminDetails.getId();
        log.info("登陆成功用户的用户id为：{}",id);

        //获取用户名
        String username = adminDetails.getUsername();
        log.info("登录用户的用户名为:{}",username);

        //获取权限列表
        Collection<GrantedAuthority> authorities = adminDetails.getAuthorities();
        log.info("登录成功用户的权限列表为：{}",authorities);

        //将权限列表对象转成JSON格式
        String authoritiesJSON = JSON.toJSONString(authorities);
        log.info("权限转成JSON格式，数据为：{}",authoritiesJSON);

        //利用JwtUtils工具生成JWT
        Map<String,Object> claims = new HashMap<>();
        claims.put("id",id);
        claims.put("username",username);
        claims.put("authorities",authoritiesJSON);
//        Date expiration = new Date(System.currentTimeMillis()+6*60*60*1000);
//        String jwt = Jwts.builder().setHeaderParam("alg", "HS256").setHeaderParam("typ", "jwt").setClaims(claims)
//                .setExpiration(expiration).signWith(SignatureAlgorithm.HS256, "ewtruewotewtwet").compact();
        String jwt = JwtUtils.generate(claims);
        log.info("生成JWT数据：{}",jwt);
        return jwt;
    }
}
