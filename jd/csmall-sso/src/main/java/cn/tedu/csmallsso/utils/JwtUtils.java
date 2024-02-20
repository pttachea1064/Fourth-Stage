package cn.tedu.csmallsso.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.Map;

@Slf4j
/**
 * 該工具類適用於生成與解析JWT
 *
 * 因為JWT使用的地方多 會需要一直重複寫 會太麻煩
 */
public class JwtUtils {
    //指定密鑰..至少要三位數以上
    private static final String SECRET_KEY = "jadsagflksdngs";
    //指定有效使用時效 以分鐘為單位
    private static final Long EXPIRED_IN_MINUTE = 7L*26*60;

    //私有化構造方法 避免外部隨便創建物件
    private JwtUtils(){ }

    //生成JWT
    public static String generate(Map<String , Object> claims){
        Date expirationDate = new Date(System.currentTimeMillis() + EXPIRED_IN_MINUTE * 60 * 1000);

        //JWT的組成
        String jwt = Jwts.builder()
                .setHeaderParam("typ","jwt") //指定類型
                .setHeaderParam("alg","HS256") //指定使用算法
                .setExpiration(expirationDate)  //指定時間限制
                .setClaims(claims)  //封裝到JWT的數據
                .signWith(SignatureAlgorithm.HS256,SECRET_KEY) //指定算法與秘鑰
                .compact(); //打包
        log.info(jwt);
        return jwt;

    }

    //解析JWT
    public static Claims parse(String jwt){
        Claims claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(jwt).getBody();
        return claims;
    }

}
