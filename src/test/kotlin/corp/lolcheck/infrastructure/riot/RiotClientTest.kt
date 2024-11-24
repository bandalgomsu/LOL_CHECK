package corp.lolcheck.infrastructure.riot

import corp.lolcheck.common.exception.BusinessException
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import kotlin.test.assertEquals

@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = ["classpath:application-test.yml"])
@SpringBootTest
class RiotClientTest(
    @Autowired private var riotClient: RiotClient
) {

    val riotClientData = RiotClientData()

    @Test
    @DisplayName("GET_PUUID_SUCCESS")
    fun GET_PUUID_SUCCESS() = runTest {
        val gameName: String = "반달곰수"
        val tagLine: String = "KR1"

        val response: RiotClientData.PuuidGetResponse = riotClient.getPuuid(gameName, tagLine)
        assertEquals("GREOkFV0W3fWqj3w6MczcF8G50fElnEjKBJ6t5n_NJWdH6C2azTXxYrJ8b0ZP_3jnjr95M0agpSBpQ", response.puuid)
        assertEquals(gameName, response.gameName)
        assertEquals(tagLine, response.tagLine)
    }

    @Test
    @DisplayName("GET_PUUID_FAILURE_THROW_BY_SUMMONER_NOT_FOUND")
    fun GET_PUUID_FAILURE_THROW_BY_SUMMONER_NOT_FOUND() = runTest {
        val gameName: String = "반달곰수수"
        val tagLine: String = "K1"

        val exception = assertThrows<BusinessException> { riotClient.getPuuid(gameName, tagLine) }

        assertEquals(RiotClientErrorCode.SUMMONER_NOT_FOUND.code, exception.errorCode.getCodeValue())
        assertEquals(RiotClientErrorCode.SUMMONER_NOT_FOUND.message, exception.errorCode.getMessageValue())
        assertEquals(RiotClientErrorCode.SUMMONER_NOT_FOUND.status, exception.errorCode.getStatusValue())
    }

    @Test
    @DisplayName("CHECK_CURRENT_GAME_INFO")
    fun CHECK_CURRENT_GAME_INFO() = runTest {
        val gameName: String = "반달곰수"
        val tagLine: String = "KR1"

        val response: RiotClientData.PuuidGetResponse = riotClient.getPuuid(gameName, tagLine)

        val gameInfo: RiotClientData.CurrentGameResponse = riotClient.checkCurrentGameInfo(response.puuid)

        println(gameInfo.isCurrentPlayingGame)
    }
}