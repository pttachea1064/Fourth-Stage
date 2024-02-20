package cn.tedu.springdataredis;

import cn.tedu.csmall.commons.pojo.cart.model.CartTb;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;

@SpringBootTest
class SpringDataRedisApplicationTests {

    //提供一个操作redis客户端的工具类
//    @Autowired
    @Resource
    private RedisTemplate redisTemplate;

    @Test
    void testString() {
        //写入一条String类型的数据
        redisTemplate.opsForValue().set("user:1","王五");
        redisTemplate.boundValueOps("user:2").set("李四");
        //获取数据
        String name = (String) redisTemplate.opsForValue().get("user:1");
        System.out.println("name="+name);
    }

    //存储对象
//    @Test
//    void testCart(){
//        CartTb cartTb = new CartTb().setId(1).setCommodityCode("PU200").setCount(2).setUserId("UU200").setPrice(99);
//        redisTemplate.boundValueOps("cart:1").set(cartTb);
//        CartTb cartTb1 = (CartTb) redisTemplate.boundValueOps("cart:1").get();
//        System.out.println("cart："+ cartTb1);
//
//    }
//
////    @Autowired
//    @Resource
//    private StringRedisTemplate stringRedisTemplate;
//    //JSON序列化工具
//    private static final ObjectMapper mapper = new ObjectMapper();
//    @Test
//    void testValueCart() throws JsonProcessingException {
//        CartTb cartTb = new CartTb().setId(1).setCommodityCode("PU200").setCount(2).setUserId("UU200").setPrice(99);
//        //手动序列化
//        String json = mapper.writeValueAsString(cartTb);
//        //写入数据
//        stringRedisTemplate.boundValueOps("cart:2").set(json);
//        //获取数据
//        String jsonCart = stringRedisTemplate.boundValueOps("cart:2").get();
//        //手动反序列化
//        CartTb cartTb2 =  mapper.readValue(jsonCart,CartTb.class);
//        System.out.println("cart:"+cartTb2);
//    }
}
