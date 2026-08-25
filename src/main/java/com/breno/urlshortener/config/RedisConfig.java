package com.breno.urlshortener.config;

import com.breno.urlshortener.url.dto.ShortUrlCacheDTO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import tools.jackson.databind.ObjectMapper;

@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<String, ShortUrlCacheDTO> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, ShortUrlCacheDTO> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new JacksonJsonRedisSerializer<>(ShortUrlCacheDTO.class));
        template.afterPropertiesSet();
        return template;
    }
}
