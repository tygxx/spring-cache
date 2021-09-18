package com.example.spring.cache.springcache.config;

import java.time.Duration;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.StringUtils;

// 开启缓存启动类的注解从启动类移到这里
@EnableCaching 
@Configuration
public class RedisConfig {

    @Value("${redisCahe.expireTime:3600}")
    private Long expireTime;

    @Value("${redisCahe.generatePre}")
    private String generatePre;

    @Bean
    public RedisSerializer<Object> redisSerializer() {
        // 创建JSON序列化器
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        //设置所有访问权限以及所有的实际类型都可序列化和反序列化
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 必须设置，否则无法将JSON转化为对象，会转化成Map类型
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);

        // 当实体类中关联了其他对象，在redis缓存中不能够懒加载到代理对象，导致序列化失败
		Hibernate5Module hibernate5Module = new Hibernate5Module();
		hibernate5Module.disable(Hibernate5Module.Feature.USE_TRANSIENT_ANNOTATION);
		objectMapper.registerModule(hibernate5Module);

        serializer.setObjectMapper(objectMapper);
        return serializer;
    }

    @Bean
    public RedisCacheConfiguration config() {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
        config = config.entryTtl(Duration.ofSeconds(expireTime)) // 设置默认缓存过期时长
            .disableCachingNullValues() // 不缓存空值，有这个设置当查不到结果来缓存时，会报错，需要借助unless
            .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer())) // key的序列化 string
            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer())) // value的序列化 json
            .computePrefixWith(name -> StringUtils.hasText(generatePre) ? (generatePre + ":" + name + ":") : name + ":"); // 使用spring-data-redis2.x版本时，@Cacheable缓存key值时默认会给vlue或cacheNames后加上双引号，变双冒号为单冒号
        return config;
    }

    @Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory factory) {
        // 在配置文件配置完redis的lettuce连接池相关参数后，在factory.clientConfiguration.poolConfig体现
        return RedisCacheManager.builder(factory).cacheDefaults(config()).build();
    }

}