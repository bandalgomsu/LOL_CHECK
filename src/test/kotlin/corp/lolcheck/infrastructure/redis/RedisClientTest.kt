package corp.lolcheck.infrastructure.redis

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import redis.embedded.RedisServer
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@SpringBootTest
class RedisClientTest {
    @Autowired
    private lateinit var redisClient: RedisClient

    private lateinit var redisServer: RedisServer

    @BeforeEach
    fun setup() {
        redisServer = RedisServer(6379)
        redisServer.start()
    }

    @AfterEach
    fun cleanup() {
        redisServer.stop()
    }

    @Test
    @DisplayName("SET_DATA_SUCCESS")
    fun SET_DATA_SUCCESS() = runTest {
        val key = "TEST_KEY"
        val value = "TEST_VALUE"

        val isSet = redisClient.setData(key, value)
        assertTrue(isSet)

        val response = redisClient.getData(key, String::class)
        assertEquals(value, response)
    }

    @Test
    @DisplayName("SET_DATA_DURATION_SUCCESS")
    fun SET_DATA_DURATION_SUCCESS() = runTest {
        val key = "TEST_KEY"
        val value = "TEST_VALUE"
        val duration = 1L

        val isSet = redisClient.setData(key, value, duration)
        assertTrue(isSet)

        Thread.sleep(1500)

        val response = redisClient.getData(key, String::class)
        assertNull(response)
    }

    @Test
    @DisplayName("GET_DATA_SUCCESS")
    fun GET_DATA_SUCCESS() = runTest {
        val key = "TEST_KEY"
        val value = "TEST_VALUE"

        val isSet = redisClient.setData(key, value)
        assertTrue(isSet)

        val response = redisClient.getData<String>(key, String::class)
        assertEquals(value, response)
    }

    @Test
    @DisplayName("DELETE_DATA_SUCCESS")
    fun DELETE_DATA_SUCCESS() = runTest {
        val key = "TEST_KEY"
        val value = "TEST_VALUE"

        val isSet = redisClient.setData(key, value)
        assertTrue(isSet)

        val isDelete = redisClient.deleteData(key)
        assertTrue(isDelete)

        val response = redisClient.getData(key, String::class)
        assertNull(response)
    }

}