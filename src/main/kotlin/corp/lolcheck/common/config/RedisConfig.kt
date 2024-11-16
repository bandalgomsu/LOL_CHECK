package corp.lolcheck.common.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration
import org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
@EnableAutoConfiguration(exclude = [RedisAutoConfiguration::class, RedisReactiveAutoConfiguration::class])
class RedisConfig {
    @Value("\${spring.data.redis.host}")
    private val host: String? = null

    @Value("\${spring.data.redis.port}")
    private val port = 0

    @Bean
    fun redisConnectionFactory(): ReactiveRedisConnectionFactory {
        return LettuceConnectionFactory(host!!, port)
    }

    @Bean
    fun reactiveRedisTemplate(): ReactiveRedisTemplate<String, Any> {
        val factory = redisConnectionFactory()
        val serializer = Jackson2JsonRedisSerializer(Any::class.java)
        val builder = RedisSerializationContext
            .newSerializationContext<String, Any>(StringRedisSerializer())
        val context = builder.value(serializer).hashValue(serializer)
            .hashKey(serializer).build()
        return ReactiveRedisTemplate(factory, context)
    }
}