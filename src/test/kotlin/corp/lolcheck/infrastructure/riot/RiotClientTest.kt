package corp.lolcheck.infrastructure.riot

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import org.springframework.web.reactive.function.client.WebClientResponseException

@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = ["classpath:application-test.yml"])
@SpringBootTest
class RiotClientTest(
    @Autowired private var riotClient: RiotClient
) {

    @Test
    @DisplayName("PUUID_호출_성공_테스트")
    fun PUUID_호출_성공_테스트() = runTest {
        var gameName: String = "반달곰수"
        var tagLine: String = "KR1"

        val response: RiotClientData.GetPuuidResponse = riotClient.getPuuid(gameName, tagLine)

        println("PUUID = ${response.puuid}")
    }

    @Test
    @DisplayName("최근게임_호출_실패_테스트")
    fun 최근게임_호출_실패_테스트() = runTest {
        var gameName: String = "반달곰수"
        var tagLine: String = "KR1"

        val response: RiotClientData.GetPuuidResponse = riotClient.getPuuid(gameName, tagLine)

        val assertThrows: WebClientResponseException =
            assertThrows<WebClientResponseException> { riotClient.checkCurrentGameInfo(response.puuid) }

        Assertions.assertEquals(
            "404 Not Found from GET https://kr.api.riotgames.com/lol/spectator/v5/active-games/by-summoner/GREOkFV0W3fWqj3w6MczcF8G50fElnEjKBJ6t5n_NJWdH6C2azTXxYrJ8b0ZP_3jnjr95M0agpSBpQ",
            assertThrows.message
        )
    }
}