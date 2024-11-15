package corp.lolcheck.infrastructure.redis

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class RedisClient(
    private val redisTemplate: ReactiveRedisTemplate<String, Any>
) {

    suspend fun setData(key: String, data: Any): Boolean = coroutineScope {
        redisTemplate.opsForValue()
            .set(key, data)
            .awaitSingle()
    }

    suspend fun setData(key: String, data: Any, durationMinute: Long): Boolean = coroutineScope {
        redisTemplate.opsForValue()
            .set(key, data, Duration.ofMinutes(durationMinute))
            .awaitSingle()
    }

    suspend fun getData(key: String): Any = coroutineScope {
        redisTemplate.opsForValue()
            .get(key)
            .awaitSingle()
    }

    suspend fun deleteData(key: String): Boolean = coroutineScope {
        redisTemplate.opsForValue()
            .delete(key)
            .awaitSingle()
    }
}