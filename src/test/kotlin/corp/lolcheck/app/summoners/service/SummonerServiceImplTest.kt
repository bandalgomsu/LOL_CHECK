package corp.lolcheck.app.summoners.service

import corp.lolcheck.app.summoners.SummonerTestConstant
import corp.lolcheck.app.summoners.domain.Summoner
import corp.lolcheck.app.summoners.dto.SummonerResponse
import corp.lolcheck.app.summoners.exception.SummonerErrorCode
import corp.lolcheck.app.summoners.repository.SummonerRepository
import corp.lolcheck.common.exception.BusinessException
import corp.lolcheck.infrastructure.riot.RiotClient
import corp.lolcheck.infrastructure.riot.RiotClientData
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime
import kotlin.test.assertEquals

class SummonerServiceImplTest {
    val summonerData = SummonerResponse()

    private val summonerRepository: SummonerRepository = mockk<SummonerRepository>()
    private val riotClient: RiotClient = mockk<RiotClient>()
    private val summonerService: SummonerServiceImpl = SummonerServiceImpl(summonerRepository, riotClient)

    val summoner = Summoner(
        id = SummonerTestConstant.SUMMONER_ID,
        puuid = SummonerTestConstant.PUUID,
        gameName = SummonerTestConstant.GAME_NAME,
        tagLine = SummonerTestConstant.TAG_LINE,
        introduce = SummonerTestConstant.INTRODUCE,
        recentGameId = SummonerTestConstant.RECENT_GAME_ID
    )

    val puuidGetResponse: RiotClientData.PuuidGetResponse = RiotClientData.PuuidGetResponse(
        puuid = SummonerTestConstant.PUUID,
        gameName = SummonerTestConstant.GAME_NAME,
        tagLine = SummonerTestConstant.TAG_LINE
    )

    val summonerInfo: SummonerResponse.SummonerInfo = SummonerResponse.SummonerInfo.from(summoner)

    val update = LocalDateTime.now()

    @BeforeEach
    fun BEFORE() {
        summoner.updatedAt = update
    }

    @Test
    @DisplayName("REGISTRY_SUMMONER_SUCCESS")
    fun REGISTRY_SUMMONER_SUCCESS() = runTest {

        coEvery {
            riotClient.getPuuid(
                SummonerTestConstant.GAME_NAME,
                SummonerTestConstant.TAG_LINE
            )
        } returns puuidGetResponse

        coEvery { summonerRepository.save(any()) } returns summoner

        val response =
            summonerService.registrySummoner(SummonerTestConstant.GAME_NAME, SummonerTestConstant.TAG_LINE)

        assertEquals(SummonerTestConstant.SUMMONER_ID, response.summonerId)
        assertEquals(SummonerTestConstant.GAME_NAME, response.gameName)
        assertEquals(SummonerTestConstant.TAG_LINE, response.tagLine)
        assertEquals(SummonerTestConstant.INTRODUCE, response.introduce)
        assertEquals(SummonerTestConstant.RECENT_GAME_ID, response.recentGameId)
        assertEquals(SummonerTestConstant.PUUID, response.puuid)
        assertEquals(update, response.updatedAt)
    }

    @Test
    @DisplayName("GET_SUMMONER_INFO_BY_GAME_NAME_AND_TAG_LINE_SUCCESS_BY_DB_HIT")
    fun GET_SUMMONER_INFO_BY_GAME_NAME_AND_TAG_LINE_SUCCESS_BY_DB_HIT() = runTest {

        coEvery {
            summonerRepository.findByGameNameAndTagLine(
                SummonerTestConstant.GAME_NAME,
                SummonerTestConstant.TAG_LINE
            )
        } returns summoner

        val response = summonerService.getSummonerInfoByGameNameAndTagLine(
            SummonerTestConstant.GAME_NAME,
            SummonerTestConstant.TAG_LINE
        )

        assertEquals(SummonerTestConstant.SUMMONER_ID, response.summonerId)
        assertEquals(SummonerTestConstant.GAME_NAME, response.gameName)
        assertEquals(SummonerTestConstant.TAG_LINE, response.tagLine)
        assertEquals(SummonerTestConstant.INTRODUCE, response.introduce)
        assertEquals(SummonerTestConstant.RECENT_GAME_ID, response.recentGameId)
        assertEquals(SummonerTestConstant.PUUID, response.puuid)
        assertEquals(update, response.updatedAt)
    }

    @Test
    @DisplayName("GET_SUMMONER_INFO_BY_GAME_NAME_AND_TAG_LINE_SUCCESS_BY_DB_MISS_API_HIT")
    fun GET_SUMMONER_INFO_BY_GAME_NAME_AND_TAG_LINE_SUCCESS_BY_DB_MISS_API_HIT() = runTest {

        coEvery {
            summonerRepository.findByGameNameAndTagLine(
                SummonerTestConstant.GAME_NAME,
                SummonerTestConstant.TAG_LINE
            )
        } returns null

        coEvery {
            riotClient.getPuuid(
                SummonerTestConstant.GAME_NAME,
                SummonerTestConstant.TAG_LINE
            )
        } returns puuidGetResponse

        coEvery { summonerRepository.save(any()) } returns summoner

        val response = summonerService.getSummonerInfoByGameNameAndTagLine(
            SummonerTestConstant.GAME_NAME,
            SummonerTestConstant.TAG_LINE
        )

        assertEquals(SummonerTestConstant.SUMMONER_ID, response.summonerId)
        assertEquals(SummonerTestConstant.GAME_NAME, response.gameName)
        assertEquals(SummonerTestConstant.TAG_LINE, response.tagLine)
        assertEquals(SummonerTestConstant.INTRODUCE, response.introduce)
        assertEquals(SummonerTestConstant.RECENT_GAME_ID, response.recentGameId)
        assertEquals(SummonerTestConstant.PUUID, response.puuid)
        assertEquals(update, response.updatedAt)
    }

    @Test
    @DisplayName("GET_SUMMONER_BY_ID_SUCCESS")
    fun GET_SUMMONER_BY_ID_SUCCESS() = runTest {
        coEvery { summonerRepository.findById(SummonerTestConstant.SUMMONER_ID) } returns summoner

        val response = summonerService.getSummonerById(SummonerTestConstant.SUMMONER_ID)

        assertEquals(SummonerTestConstant.SUMMONER_ID, response.id)
        assertEquals(SummonerTestConstant.GAME_NAME, response.gameName)
        assertEquals(SummonerTestConstant.TAG_LINE, response.tagLine)
        assertEquals(SummonerTestConstant.INTRODUCE, response.introduce)
        assertEquals(SummonerTestConstant.RECENT_GAME_ID, response.recentGameId)
        assertEquals(SummonerTestConstant.PUUID, response.puuid)
        assertEquals(update, response.updatedAt)
    }

    @Test
    @DisplayName("GET_SUMMONER_BY_ID_FAILURE_THROW_BY_SUMMONER_NOT_FOUND")
    fun GET_SUMMONER_BY_ID_FAILURE_THROW_BY_SUMMONER_NOT_FOUND() = runTest {
        coEvery { summonerRepository.findById(SummonerTestConstant.SUMMONER_ID) } returns null

        val exception =
            assertThrows<BusinessException> { summonerService.getSummonerById(SummonerTestConstant.SUMMONER_ID) }

        assertEquals(SummonerErrorCode.SUMMONER_NOT_FOUND.code, exception.errorCode.getCodeValue())
        assertEquals(SummonerErrorCode.SUMMONER_NOT_FOUND.message, exception.errorCode.getMessageValue())
        assertEquals(SummonerErrorCode.SUMMONER_NOT_FOUND.status, exception.errorCode.getStatusValue())
    }
}