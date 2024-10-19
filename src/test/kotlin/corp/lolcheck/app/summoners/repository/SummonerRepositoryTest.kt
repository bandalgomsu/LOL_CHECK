package corp.lolcheck.app.summoners.repository

import corp.lolcheck.app.summoners.domain.Summoner
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource


@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = ["classpath:application-test.yml"])
@DataR2dbcTest
class SummonerRepositoryTest(
    @Autowired private var summonerRepository: SummonerRepository
) {

    @Test
    @DisplayName("저장_테스트")
    fun 저장_테스트() = runTest {
        val summoner: Summoner = Summoner(
            puuid = "test",
            gameName = "test",
            tagLine = "test",
        )

        val save: Summoner = summonerRepository.save(summoner).awaitSingle()

        Assertions.assertEquals("test", save.puuid)
        Assertions.assertEquals("test", save.gameName)
        Assertions.assertEquals("test", save.tagLine)
    }
}