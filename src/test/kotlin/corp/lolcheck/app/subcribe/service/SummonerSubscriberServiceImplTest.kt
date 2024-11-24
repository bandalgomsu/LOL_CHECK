package corp.lolcheck.app.summoners.service

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
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class SummonerSubscriberServiceImplTest(
) {

    private var summonerSubscriberRepository: SummonerSubscriberRepository = mockk<SummonerSubscriberRepository>()

    private var summonerService: SummonerService = mockk<SummonerService>()

    private var summonerSubscriberService: SummonerSubscriberServiceImpl =
        SummonerSubscriberServiceImpl(
            summonerService = summonerService,
            summonerSubscriberRepository = summonerSubscriberRepository
        )

    @Test
    @DisplayName("SUBSCRIBE_SUMMONER_SUCCESS")
    fun SUBSCRIBE_SUMMONER_SUCCESS() = runTest {
        val subscriberId = 1L
        val summonerId = 1L

        val summoner = Summoner(
            id = summonerId,
            puuid = "TEST_PUUID",
            gameName = "TEST_GAME_NAME",
            tagLine = "TEST_TAG_LINE",
            introduce = "TEST_INTRODUCE",
            recentGameId = 2L
        )

        val summonerSubscriber = SummonerSubscriber(
            id = 1L,
            summonerId = summonerId,
            subscriberId = subscriberId
        )

        coEvery { summonerSubscriberRepository.findAllBySubscriberId(subscriberId) } returns flow {
            emit(
                summonerSubscriber
            )
        }

        coEvery { summonerService.getSummonerById(summonerId) } returns summoner

        coEvery { summonerSubscriberRepository.findBySubscriberIdAndSummonerId(subscriberId, summonerId) } returns null

        coEvery { summonerSubscriberRepository.save(any()) } returns summonerSubscriber

        val response: SummonerSubscriberResponse.SummonerSubscriberInfo =
            summonerSubscriberService.subscribeSummoner(userId, summonerId)

        assertEquals(1L, response.id)
        assertEquals(userId, response.subscriberId)
        assertEquals(summonerId, response.summonerId)
        assertEquals("TEST_GAME_NAME", response.summonerGameName)
        assertEquals("TEST_TAG_LINE", response.summonerTagLine)
        assertEquals("TEST_INTRODUCE", response.summonerIntroduce)
    }

    @Test
    @DisplayName("SUBSCRIBE_SUMMONER_FAILURE_THROW_BY_MAX_COUNT_SUBSCRIBE")
    fun SUBSCRIBE_SUMMONER_FAILURE_THROW_BY_MAX_COUNT_SUBSCRIBE() = runTest {
        val subscriberId = 1L
        val summonerId = 1L

        coEvery { summonerSubscriberRepository.findAllBySubscriberId(subscriberId) } returns flow {
            emit(
                SummonerSubscriber(
                    id = 1L,
                    summonerId = summonerId,
                    subscriberId = subscriberId
                )
            )
            emit(
                SummonerSubscriber(
                    id = 1L,
                    summonerId = summonerId,
                    subscriberId = subscriberId
                )
            )
            emit(
                SummonerSubscriber(
                    id = 1L,
                    summonerId = summonerId,
                    subscriberId = subscriberId
                )
            )
        }

        val exception = assertThrows<BusinessException> {
            summonerSubscriberService.subscribeSummoner(userId, summonerId)
        }

        assertEquals(SummonerSubscriberErrorCode.MAX_COUNT_SUBSCRIBE.code, exception.errorCode.getCodeValue())
        assertEquals(SummonerSubscriberErrorCode.MAX_COUNT_SUBSCRIBE.message, exception.errorCode.getMessageValue())
        assertEquals(SummonerSubscriberErrorCode.MAX_COUNT_SUBSCRIBE.status, exception.errorCode.getStatusValue())
    }

    @Test
    @DisplayName("SUBSCRIBE_SUMMONER_FAILURE_THROW_BY_DUPLICATE_SUMMONER_SUBSCRIBER")
    fun SUBSCRIBE_SUMMONER_FAILURE_THROW_BY_DUPLICATE_SUMMONER_SUBSCRIBER() = runTest {
        val subscriberId = 1L
        val summonerId = 1L

        val summoner = Summoner(
            id = summonerId,
            puuid = "TEST_PUUID",
            gameName = "TEST_GAME_NAME",
            tagLine = "TEST_TAG_LINE",
            introduce = "TEST_INTRODUCE",
            recentGameId = 2L
        )

        val summonerSubscriber = SummonerSubscriber(
            id = 1L,
            summonerId = summonerId,
            subscriberId = subscriberId
        )

        coEvery { summonerSubscriberRepository.findAllBySubscriberId(subscriberId) } returns flow {
            emit(
                SummonerSubscriber(
                    id = 1L,
                    summonerId = summonerId,
                    subscriberId = subscriberId
                )
            )
        }

        coEvery { summonerService.getSummonerById(summonerId) } returns summoner

        coEvery {
            summonerSubscriberRepository.findBySubscriberIdAndSummonerId(
                subscriberId,
                summonerId
            )
        } returns summonerSubscriber

        val exception = assertThrows<BusinessException> {
            summonerSubscriberService.subscribeSummoner(userId, summonerId)
        }

        assertEquals(
            SummonerSubscriberErrorCode.DUPLICATE_SUMMONER_SUBSCRIBER.code,
            exception.errorCode.getCodeValue()
        )
        assertEquals(
            SummonerSubscriberErrorCode.DUPLICATE_SUMMONER_SUBSCRIBER.message,
            exception.errorCode.getMessageValue()
        )
        assertEquals(
            SummonerSubscriberErrorCode.DUPLICATE_SUMMONER_SUBSCRIBER.status,
            exception.errorCode.getStatusValue()
        )
    }

    @Test
    @DisplayName("GET_MY_SUBSCRIBES_SUCCESS")
    fun GET_MY_SUBSCRIBES_SUCCESS() = runTest {
        val subscriberId = 1L
        val summonerId = 1L

        val summoner = Summoner(
            id = summonerId,
            puuid = "TEST_PUUID",
            gameName = "TEST_GAME_NAME",
            tagLine = "TEST_TAG_LINE",
            introduce = "TEST_INTRODUCE",
            recentGameId = 2L
        )

        val summonerSubscriber = SummonerSubscriber(
            id = 1L,
            summonerId = summonerId,
            subscriberId = subscriberId
        )

        coEvery { summonerSubscriberRepository.findAllBySubscriberId(userId) } returns flow { emit(summonerSubscriber) }
        coEvery { summonerService.getSummonerById(summonerId) } returns summoner

        val response = summonerSubscriberService.getMySubscribes(userId).toList()

        assertEquals(1, response.size)
        assertEquals(1L, response[0].id)
        assertEquals(subscriberId, response[0].subscriberId)
        assertEquals(summonerId, response[0].summonerId)
        assertEquals("TEST_GAME_NAME", response[0].summonerGameName)
        assertEquals("TEST_TAG_LINE", response[0].summonerTagLine)
        assertEquals("TEST_INTRODUCE", response[0].summonerIntroduce)
    }

    @Test
    @DisplayName("GET_SUBSCRIBER_IDS_BY_SUMMONER_IDS")
    fun GET_SUBSCRIBER_IDS_BY_SUMMONER_IDS() = runTest {
        val subscriberId = 1L
        val summonerId = 1L

        val summonerSubscriber = SummonerSubscriber(
            id = 1L,
            summonerId = summonerId,
            subscriberId = subscriberId
        )

        val summonerIds = listOf(summonerId)

        coEvery { summonerSubscriberRepository.findAllBySubscriberIdIn(summonerIds) } returns flow {
            emit(
                summonerSubscriber
            )
        }

        val response = summonerSubscriberService.getSubscriberIdsBySummonerIds(summonerIds)

        assertEquals(1, response.size)
        assertEquals(subscriberId, response[0])
    }

    @Test
    @DisplayName("GET_MY_SUBSCRIBE_SUCCESS")
    fun GET_MY_SUBSCRIBE_SUCCESS() = runTest {
        val subscriberId = 1L
        val summonerId = 1L

        val summoner = Summoner(
            id = summonerId,
            puuid = "TEST_PUUID",
            gameName = "TEST_GAME_NAME",
            tagLine = "TEST_TAG_LINE",
            introduce = "TEST_INTRODUCE",
            recentGameId = 2L
        )

        val summonerSubscriber = SummonerSubscriber(
            id = 1L,
            summonerId = summonerId,
            subscriberId = subscriberId
        )

        coEvery {
            summonerSubscriberRepository.findBySubscriberIdAndSummonerId(
                userId,
                summonerId
            )
        } returns summonerSubscriber
        coEvery { summonerService.getSummonerById(summonerId) } returns summoner

        val response = summonerSubscriberService.getMySubscribe(userId, summonerId)

        assertEquals(1L, response.id)
        assertEquals(subscriberId, response.subscriberId)
        assertEquals(summonerId, response.summonerId)
        assertEquals("TEST_GAME_NAME", response.summonerGameName)
        assertEquals("TEST_TAG_LINE", response.summonerTagLine)
        assertEquals("TEST_INTRODUCE", response.summonerIntroduce)
    }

    @Test
    @DisplayName("GET_MY_SUBSCRIBE_FAILURE_THROW_BY_SUMMONER_SUBSCRIBER_NOT_FOUND")
    fun GET_MY_SUBSCRIBE_FAILURE_THROW_BY_SUMMONER_SUBSCRIBER_NOT_FOUND() = runTest {
        val subscriberId = 1L
        val summonerId = 1L

        coEvery {
            summonerSubscriberRepository.findBySubscriberIdAndSummonerId(
                userId,
                summonerId
            )
        } returns null

        val exception =
            assertThrows<BusinessException> { summonerSubscriberService.getMySubscribe(userId, summonerId) }

        assertEquals(
            SummonerSubscriberErrorCode.SUMMONER_SUBSCRIBER_NOT_FOUND.code,
            exception.errorCode.getCodeValue()
        )
        assertEquals(
            SummonerSubscriberErrorCode.SUMMONER_SUBSCRIBER_NOT_FOUND.message,
            exception.errorCode.getMessageValue()
        )
        assertEquals(
            SummonerSubscriberErrorCode.SUMMONER_SUBSCRIBER_NOT_FOUND.status,
            exception.errorCode.getStatusValue()
        )
    }

    @Test
    @DisplayName("GET_SUBSCRIBER_BY_SUMMONER_ID")
    fun GET_SUBSCRIBER_BY_SUMMONER_ID() = runTest {
        val subscriberId = 1L
        val summonerId = 1L

        val summoner = Summoner(
            id = summonerId,
            puuid = "TEST_PUUID",
            gameName = "TEST_GAME_NAME",
            tagLine = "TEST_TAG_LINE",
            introduce = "TEST_INTRODUCE",
            recentGameId = 2L
        )

        val summonerSubscriber = SummonerSubscriber(
            id = 1L,
            summonerId = summonerId,
            subscriberId = subscriberId
        )

        coEvery { summonerSubscriberRepository.findAllBySummonerId(summonerId) } returns flow { emit(summonerSubscriber) }
        coEvery { summonerService.getSummonerById(summonerId) } returns summoner


        val response = summonerSubscriberService.getSubscriberBySummonerId(summonerId)

        assertEquals(1, response.size)
        assertEquals(1L, response[0].id)
        assertEquals(subscriberId, response[0].subscriberId)
        assertEquals(summonerId, response[0].summonerId)
        assertEquals("TEST_GAME_NAME", response[0].summonerGameName)
        assertEquals("TEST_TAG_LINE", response[0].summonerTagLine)
        assertEquals("TEST_INTRODUCE", response[0].summonerIntroduce)
    }

    @Test
    @DisplayName("UNSUBSCRIBE_SUCCESS")
    fun UNSUBSCRIBE_SUCCESS() = runTest {
        val subscriberId = 1L
        val summonerId = 1L

        val summonerSubscriber = SummonerSubscriber(
            id = 1L,
            summonerId = summonerId,
            subscriberId = subscriberId
        )

        coEvery {
            summonerSubscriberRepository.findBySubscriberIdAndSummonerId(
                subscriberId,
                summonerId
            )
        } returns summonerSubscriber
        coEvery { summonerSubscriberRepository.delete(summonerSubscriber) } returns Unit

        summonerSubscriberService.unsubscribeSummoner(subscriberId, summonerId)

        coVerify(exactly = 1) { summonerSubscriberRepository.delete(summonerSubscriber) }
    }

    @Test
    @DisplayName("UNSUBSCRIBE_FAILURE_THROW_BY_SUMMONER_SUBSCRIBER_NOT_FOUND")
    fun UNSUBSCRIBE_FAILURE_THROW_BY_SUMMONER_SUBSCRIBER_NOT_FOUND() = runTest {
        val subscriberId = 1L
        val summonerId = 1L

        coEvery {
            summonerSubscriberRepository.findBySubscriberIdAndSummonerId(
                subscriberId,
                summonerId
            )
        } returns null

        val exception =
            assertThrows<BusinessException> { summonerSubscriberService.unsubscribeSummoner(subscriberId, summonerId) }

        assertEquals(
            SummonerSubscriberErrorCode.SUMMONER_SUBSCRIBER_NOT_FOUND.code,
            exception.errorCode.getCodeValue()
        )
        assertEquals(
            SummonerSubscriberErrorCode.SUMMONER_SUBSCRIBER_NOT_FOUND.message,
            exception.errorCode.getMessageValue()
        )
        assertEquals(
            SummonerSubscriberErrorCode.SUMMONER_SUBSCRIBER_NOT_FOUND.status,
            exception.errorCode.getStatusValue()
        )
    }
}