package webengdus.shopservice

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class RedisConfiguration {

    @Bean
    @Primary
    fun redisTemplate(rcf: RedisConnectionFactory, redisProductSerializer: RedisProductSerializer): RedisTemplate<String, String> {
        val rt = RedisTemplate<String, String>()
        rt.connectionFactory = rcf
        rt.keySerializer = StringRedisSerializer()
        rt.valueSerializer = StringRedisSerializer()
        rt.hashKeySerializer = StringRedisSerializer()
        rt.hashValueSerializer = redisProductSerializer
        rt.afterPropertiesSet()
        return rt
    }
}


