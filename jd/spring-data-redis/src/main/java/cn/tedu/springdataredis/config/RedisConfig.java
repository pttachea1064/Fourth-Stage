package cn.tedu.springdataredis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class RedisConfig {
    //提供RedisTemplate. 自動注入到Srping中

    @Bean
    public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory rdc){
        //創建RedisTemplate物件
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        //設置連接工廠
        template.setConnectionFactory(rdc);
        //創建JSON序列化工具
        GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer();
        //設置key的序列化
        template.setKeySerializer(RedisSerializer.string());
        template.setHashKeySerializer(RedisSerializer.string());
        //設置value的序列化
        template.setValueSerializer(genericJackson2JsonRedisSerializer);
        template.setHashValueSerializer(genericJackson2JsonRedisSerializer);
        //返回
        return template;
    }
}
