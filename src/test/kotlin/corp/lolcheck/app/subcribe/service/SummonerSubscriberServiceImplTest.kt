package corp.lolcheck.app.summoners.service

import corp.lolcheck.app.subcribe.SummonerSubscriberTestConst.gameName
import corp.lolcheck.app.subcribe.SummonerSubscriberTestConst.subscribeId
import corp.lolcheck.app.subcribe.SummonerSubscriberTestConst.summonerId
import corp.lolcheck.app.subcribe.SummonerSubscriberTestConst.tagLine
import corp.lolcheck.app.subcribe.SummonerSubscriberTestConst.userId
import corp.lolcheck.app.subcribe.domain.SummonerSubscriber
import corp.lolcheck.app.subcribe.dto.SummonerSubscriberResponse
import corp.lolcheck.app.subcribe.exception.SummonerSubscriberErrorCode
import corp.lolcheck.app.subcribe.repository.SummonerSubscriberRepository
import corp.lolcheck.app.subcribe.service.SummonerSubscriberServiceImpl
import corp.lolcheck.app.summoners.domain.Summoner
import corp.lolcheck.app.summoners.service.interfaces.SummonerService
import corp.lolcheck.common.exception.BusinessException
import io.mockk.coEvery
import io.mockk.coVerify
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
import kotlin.test.assertFailsWith

@ExtendWith(MockKExtension::class)
class SummonerSubscriberServiceImplTest(
) {

    var summonerSubscriberRepository: SummonerSubscriberRepository = mockk<SummonerSubscriberRepository>()

    var summonerService: SummonerService = mockk<SummonerService>()

    var summonerSubscriberService: SummonerSubscriberServiceImpl =
        spyk(SummonerSubscriberServiceImpl(summonerSubscriberRepository, summonerService))

    val summoner: Summoner = Summoner(
        id = summonerId,
        puuid = "test",
        gameName = gameName,
        tagLine = tagLine
    )

    val summonerSubscriber: SummonerSubscriber = SummonerSubscriber(
        id = subscribeId,
        summonerId = summonerId,
        subscriberId = userId,
    )

    @BeforeEach
    fun beforeEach() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    @DisplayName("소환사_구독_성공_테스트")
    fun 소환사_구독_성공_테스트() = runTest {
        coEvery { summonerService.getSummonerById(summonerId) } returns summoner

        coEvery { summonerSubscriberRepository.findBySubscriberIdAndSummonerId(userId, summonerId) } returns null

        coEvery { summonerSubscriberRepository.save(any()) } returns summonerSubscriber

        val response: SummonerSubscriberResponse.SummonerSubscriberInfo =
            summonerSubscriberService.subscribeSummoner(userId, summonerId)

        Assertions.assertEquals(userId, response.subscriberId)
        Assertions.assertEquals(summonerId, response.summonerId)
    }

    @Test
    @DisplayName("소환사_구독_실패_테스트_존재하지 않는 소환사")
    fun 소환사_구독_실패_테스트_중복_구독() = runTest {
        coEvery { summonerService.getSummonerById(summonerId) } returns summoner
        coEvery {
            summonerSubscriberRepository.findBySubscriberIdAndSummonerId(
                userId,
                summonerId
            )
        } returns summonerSubscriber

        assertFailsWith<BusinessException> {
            summonerSubscriberService.subscribeSummoner(
                userId,
                summonerId
            )
        }
    }

    @Test
    @DisplayName("소환사_구독취소_성공_테스트")
    fun 소환사_구독취소_성공_테스트() = runTest {
        coEvery {
            summonerSubscriberRepository.findBySubscriberIdAndSummonerId(
                userId,
                summonerId
            )
        } returns summonerSubscriber

        coEvery { summonerSubscriberRepository.delete(summonerSubscriber) } returns Unit

        summonerSubscriberService.unsubscribeSummoner(userId, summonerId)

        coVerify(exactly = 1) { summonerSubscriberRepository.delete(summonerSubscriber) }
    }

    @Test
    @DisplayName("소환사_구독취소_실패_테스트_존재하지_않는_구독")
    fun 소환사_구독취소_실패_테스트_존재하지_않는_구독() = runTest {
        coEvery {
            summonerSubscriberRepository.findBySubscriberIdAndSummonerId(
                userId,
                summonerId
            )
        } throws BusinessException(SummonerSubscriberErrorCode.SUMMONER_SUBSCRIBER_NOT_FOUND)

        assertFailsWith<BusinessException> {
            summonerSubscriberService.unsubscribeSummoner(
                userId,
                summonerId
            )
        }
    }
}