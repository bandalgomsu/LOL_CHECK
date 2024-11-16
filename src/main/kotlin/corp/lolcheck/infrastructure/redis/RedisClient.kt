package corp.lolcheck.infrastructure.redis

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Component
import java.time.Duration
import kotlin.reflect.KClass

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

    suspend fun <T : Any> getData(key: String, type: KClass<T>): T? = coroutineScope {
        redisTemplate.opsForValue()
            .get(key)
            .awaitSingleOrNull() as T?
    }

    suspend fun deleteData(key: String): Boolean = coroutineScope {
        redisTemplate.opsForValue()
            .delete(key)
            .awaitSingle()
    }
}