package corp.lolcheck.app.summoners.service

import corp.lolcheck.app.summoners.domain.Summoner
import corp.lolcheck.app.summoners.dto.SummonerResponse
import corp.lolcheck.app.summoners.repository.SummonerRepository
import corp.lolcheck.infrastructure.riot.RiotClient
import corp.lolcheck.infrastructure.riot.RiotClientData
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.spyk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.MockitoAnnotations
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@ExtendWith(MockKExtension::class)
class SummonerServiceImplTest(
) {

    var summonerRepository: SummonerRepository = mockk<SummonerRepository>()

    var riotClient: RiotClient = mockk<RiotClient>()

    var summonerService: SummonerServiceImpl = spyk(SummonerServiceImpl(summonerRepository, riotClient))

    val gameName: String = "test"
    val tagLine: String = "testLine"

    @BeforeEach
    fun beforeEach() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    @DisplayName("소환사_등록_성공_테스트")
    fun 소환사_등록_성공_테스트() = runTest {

        val mono: Mono<Summoner> = Summoner(
            id = 1L,
            puuid = "testPuuid",
            tagLine = tagLine,
            gameName = gameName
        ).toMono()

        every {
            riotClient.getPuuid(gameName, tagLine)
        } returns Mono.just(
            RiotClientData.GetPuuidResponse(
                puuid = "testPuuid",
                tagLine = tagLine,
                gameName = gameName
            )
        )

        every {
            summonerRepository.save(any())
        } returns mono

        val response: SummonerResponse.SummonerInfo = summonerService.registrySummoner(gameName, tagLine)

        Assertions.assertEquals(tagLine, response.tagLine)
        Assertions.assertEquals(gameName, response.gameName)
    }

}