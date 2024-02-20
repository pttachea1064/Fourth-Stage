package cn.tedu.csmallsso.filter;


import cn.tedu.csmall.commons.restful.JsonResult;
import cn.tedu.csmall.commons.restful.ResponseCode;
import cn.tedu.csmallsso.utils.LoginPrincipal;
import cn.tedu.csmallsso.utils.JwtUtils;
import com.alibaba.fastjson.JSON;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

//处理JWT的过滤器
@Slf4j
@Component
//OncePerRequestFilter:确保在一个请求中只通过一次过滤器
/*
  1.此过滤器将会尝试获取请求中的JWT数据，如果存在有效数据，将尝试解析
  2.将解析后的结果村存入到Spring Security的上下文中
  3.后续就可以从Spring Security的上下文中获取用户信息
  4.最后可以完成授权的访问
 */
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        log.info("JwtAuthorizationFilter开始执行......");
        //清除Security上下文
        //如果不清除，只要存储过信息，即使后续不携带JWT，上下文的登录信息依然存在
        SecurityContextHolder.clearContext();

        //从请求对象中获取请求头(key-value)，JWT的值默认是Authorization中
        String jwt = httpServletRequest.getHeader("Authorization");
        log.info("从请求头中获取到jwt的数据：{}",jwt);
        //判断是否获取到有效的JWT数据：不为null并且不为空白并且包含文本---无JWT数据(直接放行--验证登录)
        if(!StringUtils.hasText(jwt)){
            log.info("请求头中的数据无效，直接放行");
            filterChain.doFilter(httpServletRequest,httpServletResponse);
            return;
        }
        //JWT有数据，解析数据
//        Claims claims = Jwts.parser().setSigningKey("ewtruewotewtwet").parseClaimsJws(jwt).getBody();
//        Claims claims = JwtUtils.parse(jwt);
          Claims claims = null;
        try{
            claims = JwtUtils.parse(jwt);
        }catch (MalformedJwtException e){
            log.info("當前JWT的組成結構出現異常:{}",e.getMessage());
            String message = "登入失敗!! 請重新登入! ";
            JsonResult jsonResult = JsonResult.failed(ResponseCode.ERR_JWT_INVALID, message);

            String jsonResultString = JSON.toJSONString(jsonResult);
            log.info("將向客戶端回應: {}",jsonResultString);

            //指定回應的類型
            httpServletResponse.setContentType("application/json;charset=UTF-8");
            httpServletResponse.getWriter().println(jsonResultString);
            return;
        }catch (SignatureException e){
            log.info("當前JWT的簽名出現異常:{}",e.getMessage());
            String message = "登入失敗!! 請重新登入! ";
            JsonResult jsonResult = JsonResult.failed(ResponseCode.ERR_JWT_INVALID, message);

            String jsonResultString = JSON.toJSONString(jsonResult);
            log.info("將向客戶端回應: {}",jsonResultString);

            //指定回應的類型
            httpServletResponse.setContentType("application/json;charset=UTF-8");
            httpServletResponse.getWriter().println(jsonResultString);
            return;
        }catch (ExpiredJwtException e){
            log.info("當前JWT出現過期異常:{}",e.getMessage());
            String message = "JWT登入訊息過期!! 請重新登入! ";
            JsonResult jsonResult = JsonResult.failed(ResponseCode.ERR_JWT_EXPIRED, message);

            String jsonResultString = JSON.toJSONString(jsonResult);
            log.info("將向客戶端回應: {}",jsonResultString);

            //指定回應的類型
            httpServletResponse.setContentType("application/json;charset=UTF-8");
            httpServletResponse.getWriter().println(jsonResultString);
            return;
        }catch (Throwable e ){
            log.info("當前JWT{}解析異常:{}",e.getMessage());
            String message = "服務器忙碌中!! 請稍後重試! ";
            JsonResult jsonResult = JsonResult.failed(ResponseCode.ERR_JWT_INVALID, message);

            String jsonResultString = JSON.toJSONString(jsonResult);
            log.info("將向客戶端回應: {}",jsonResultString);

            //指定回應的類型
            httpServletResponse.setContentType("application/json;charset=UTF-8");
            httpServletResponse.getWriter().println(jsonResultString);
            return;
        }

        //从claims中获取id
        Object id = claims.get("id");
        log.info("从JWT中解析出的id为：{}",id);
        //从claims中获取用户名
        Object username = claims.get("username");
        log.info("从JWT中解析出的username为：{}",username);

        //将当前登录人的id和name，赋值到LoginPrincipal
        LoginPrincipal loginPrincipal = new LoginPrincipal();
        loginPrincipal.setId(Long.parseLong(id.toString()));
        loginPrincipal.setUsername(username.toString());

        //从clamis中获取用户权限信息
        Object authoritiesJSON = claims.get("authorities");
        log.info("从JWT中解析出的authorities为：{}",authoritiesJSON);
        //将JSON格式转成java对象
        List<SimpleGrantedAuthority> authorities =
                JSON.parseArray(authoritiesJSON.toString(), SimpleGrantedAuthority.class);

        //解析成功后有，将相关数据存入到Security上下文对象中
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(loginPrincipal, null, authorities);
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);
        //放行
        filterChain.doFilter(httpServletRequest,httpServletResponse);
    }
}
