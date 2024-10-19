package corp.lolcheck.app.subcribe.service

import corp.lolcheck.app.subcribe.domain.SummonerSubscriber
import corp.lolcheck.app.subcribe.dto.SummonerSubscriberResponse
import corp.lolcheck.app.subcribe.exception.SummonerSubscriberErrorCode
import corp.lolcheck.app.subcribe.repository.SummonerSubscriberRepository
import corp.lolcheck.app.subcribe.service.interfaces.SummonerSubscriberService
import corp.lolcheck.app.summoners.domain.Summoner
import corp.lolcheck.app.summoners.service.interfaces.SummonerService
import corp.lolcheck.common.exception.BusinessException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SummonerSubscriberServiceImpl(
    private val summonerSubscriberRepository: SummonerSubscriberRepository,
    private val summonerService: SummonerService
) : SummonerSubscriberService {

    @Transactional
    override suspend fun subscribeSummoner(
        userId: Long,
        summonerId: Long
    ): SummonerSubscriberResponse.SummonerSubscriberInfo = coroutineScope {
        val summonerDeferred = async {
            summonerService.getSummonerById(summonerId)
        }

        val duplicateDeferred = async {
            duplicateSummonerSubscriber(userId, summonerId)
        }

        val summoner: Summoner = awaitAll(summonerDeferred, duplicateDeferred)[0] as Summoner

        val subscriber: SummonerSubscriber = SummonerSubscriber(
            subscriberId = userId,
            summonerId = summoner.id!!
        )

        val save: SummonerSubscriber = summonerSubscriberRepository.save(subscriber)

        SummonerSubscriberResponse.SummonerSubscriberInfo(
            id = save.id!!,
            subscriberId = save.subscriberId,
            summonerId = save.summonerId
        )
    }

    private suspend fun duplicateSummonerSubscriber(
        userId: Long,
        summonerId: Long
    ) = coroutineScope {
        if (summonerSubscriberRepository.findBySubscriberIdAndSummonerId(userId, summonerId) != null) {
            throw BusinessException(SummonerSubscriberErrorCode.DUPLICATE_SUMMONER_SUBSCRIBER)
        }
    }

    @Transactional
    override suspend fun getMySubscriberSummoner(userId: Long): Flow<SummonerSubscriberResponse.SummonerSubscriberInfo> =
        coroutineScope {
            summonerSubscriberRepository.findBySubscriberId(userId).map {
                SummonerSubscriberResponse.SummonerSubscriberInfo(
                    id = it.id!!,
                    subscriberId = it.subscriberId,
                    summonerId = it.summonerId
                )
            }
        }

    @Transactional
    override suspend fun unsubscribeSummoner(userId: Long, summonerId: Long): Unit = coroutineScope {
        val subscriber =
            summonerSubscriberRepository.findBySubscriberIdAndSummonerId(userId, summonerId) ?: throw BusinessException(
                SummonerSubscriberErrorCode.SUMMONER_SUBSCRIBER_NOT_FOUND
            )

        summonerSubscriberRepository.delete(subscriber)
    }
}