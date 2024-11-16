package corp.lolcheck.infrastructure.redis

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Component

@Component
class RedisSetClient(
    private val redisTemplate: ReactiveRedisTemplate<String, Any>
) {

    suspend fun setData(key: String, data: Any) = coroutineScope {
        redisTemplate.opsForSet()
            .add(key, data)
            .awaitSingle()
    }

    suspend fun isMember(key: String, value: Any): Boolean = coroutineScope {
        redisTemplate.opsForSet()
            .isMember(key, value)
            .awaitSingle()
    }

    suspend fun removeData(key: String, data: Any) = coroutineScope {
        redisTemplate.opsForSet()
            .remove(key, data)
            .awaitSingle()
    }
}