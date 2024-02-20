package cn.tedu.csmallsso;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SpringBootTest
class CsmallSsoApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void testMD5(){
        for(int i=1;i<10;i++){
            String salt = UUID.randomUUID().toString();
//            String salt = "abcdefg";
            String passwd = "123456";
            String md5DigestAsHex = DigestUtils.md5DigestAsHex((passwd+salt).getBytes());
            System.out.println("md5DigestAsHex:"+md5DigestAsHex);

        }
    }

    @Test
    public void testBCrypt(){
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String rawPassword="123456";
        String encodePassword= bCryptPasswordEncoder.encode(rawPassword);
        System.out.println("rP:"+rawPassword+",eP:"+encodePassword);

    }
    //要記住生成的密碼內容
    //寫一個匹配的測試
    @Test
    public void testMatch(){
        String rawPassword = "123456";
        String encodePassword="/*上面生成的密碼*/";
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        boolean matches = bCryptPasswordEncoder.matches(rawPassword,encodePassword);
        System.out.println("匹配結果"+matches);
    }

    //生成JWT
    //自訂一個密鑰
    String secretKey = "seniorhighschool";

    @Test
    public void testGenerateJWT(){
        //準備要封裝的數據內容Payload存放,利用Map來實現
        Map<String ,Object> readyData = new HashMap<>();
        readyData.put("id",6666);
        readyData.put("name","Lili");
        readyData.put("age",18);
        //準備JWT的有效期限(有效使用時間) ex.2分鐘的測試時間
        Date expirationDate = new Date(System.currentTimeMillis() + 2 * 60 * 1000);

        //JWT組成分為三部分 Head(頭) , Payload(載荷) ,Signature(簽名)
        String jwt = Jwts.builder()
                .setHeaderParam("type","jwt") //配置類型為JWT
                .setHeaderParam("alg","hs256") //配置算法類型為HS256
                .setClaims(readyData) //需要封裝JWT的數據內容
                .setExpiration(expirationDate) //設置JWT有效時限
                .signWith(SignatureAlgorithm.HS256,secretKey) //指定算法和密鑰(包含鹽)
                .compact(); //打包
        System.out.println(jwt);
//        eyJ0eXBlIjoiand0IiwiYWxnIjoiSFMyNTYifQ.eyJuYW1lIjoiTGlsaSIsImlkIjo2NjY2LCJleHAiOjE2OTgxNDg5ODksImFnZSI6MTh9.lSoLuATF0HSdMGHg8wV7iI88b1S-bd0u5xkJZEFiuvc
//        eyJ0eXBlIjoiand0IiwiYWxnIjoiSFMyNTYifQ.eyJuYW1lIjoiTGlsaSIsImlkIjo2NjY2LCJleHAiOjE2OTgxNDkxMTgsImFnZSI6MTh9.xt4MG4Cg7ba7VW_fq7WMlrPyFqQYvwGCfJZyBPIRHys

    }

    //解析JWT
    @Test
    public void testParseJWT(){
        //要注意密鑰的有效期限 失效時需要再次產生
        String jwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6Imp3dCJ9.eyJleHAiOjE2OTgzMjg5NjksInVzZXJuYW1lIjoibGluMjMifQ.Xee-elWyZH0RXbFc8Z8e3n7j43q8TCLmmCVTcOrczFM";
        Claims claims = Jwts.parser().setSigningKey("ewtruewotewtwet").parseClaimsJws(jwt).getBody();
        Object username = claims.get("username");
//        Object password = claims.get("password");
//        Object age = claims.get("age");
        System.out.println("username:"+username);
//        System.out.println("password:"+password);
//        System.out.println("age:"+age);
    }



}
