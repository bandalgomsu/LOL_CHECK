package corp.lolcheck.infrastructure.riot

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource

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

        val response: RiotClientData.PuuidGetResponse = riotClient.getPuuid(gameName, tagLine)

        println("PUUID = ${response.puuid}")
    }

    @Test
    @DisplayName("최근게임_호출_실패_테스트")
    fun 최근게임_호출_실패_테스트() = runTest {
        var gameName: String = "반달곰수"
        var tagLine: String = "KR1"

        val response: RiotClientData.PuuidGetResponse = riotClient.getPuuid(gameName, tagLine)

        val gameInfo: RiotClientData.CurrentGameResponse = riotClient.checkCurrentGameInfo(response.puuid)

        println(gameInfo.isCurrentPlayingGame)
    }
}