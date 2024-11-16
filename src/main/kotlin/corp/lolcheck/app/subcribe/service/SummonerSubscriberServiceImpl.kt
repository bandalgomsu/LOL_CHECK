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
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private const val MAX_SUBSCRIBE_COUNT = 2

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
        if (summonerSubscriberRepository.findAllBySubscriberId(userId).toList().count() >= MAX_SUBSCRIBE_COUNT) {
            throw BusinessException(SummonerSubscriberErrorCode.MAX_COUNT_SUBSCRIBE)
        }

        val summonerDeferred = async {
            summonerService.getSummonerById(summonerId)
        }

        val duplicateDeferred = async {
            duplicateSummonerSubscriber(userId, summonerId)
        }

        val summoner: Summoner = awaitAll(summonerDeferred, duplicateDeferred)[0] as Summoner

        val subscriber: SummonerSubscriber = SummonerSubscriber(
            subscriberId = userId,
            summonerId = summoner.id!!,
            summonerGameName = summoner.gameName,
            summonerTagLine = summoner.tagLine,
        )

        val save: SummonerSubscriber = summonerSubscriberRepository.save(subscriber)

        SummonerSubscriberResponse.SummonerSubscriberInfo(
            id = save.id!!,
            subscriberId = save.subscriberId,
            summonerId = save.summonerId,
            summonerGameName = save.summonerGameName,
            summonerTagLine = save.summonerTagLine,
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

    override suspend fun getMySubscribes(userId: Long): Flow<SummonerSubscriberResponse.SummonerSubscriberInfo> =
        coroutineScope {
            summonerSubscriberRepository.findAllBySubscriberId(userId).map {
                SummonerSubscriberResponse.SummonerSubscriberInfo(
                    id = it.id!!,
                    subscriberId = it.subscriberId,
                    summonerId = it.summonerId,
                    summonerGameName = it.summonerGameName,
                    summonerTagLine = it.summonerTagLine,
                )
            }
        }

    override suspend fun getSubscriberIdsBySummonerIds(summonerIds: List<Long>): List<Long> = coroutineScope {
        summonerSubscriberRepository.findAllBySubscriberIdIn(summonerIds)
            .map { it.subscriberId }
            .toList()
    }

    override suspend fun getMySubscribe(
        userId: Long,
        summonerId: Long
    ): SummonerSubscriberResponse.SummonerSubscriberInfo = coroutineScope {
        val subscriber: SummonerSubscriber =
            summonerSubscriberRepository.findBySubscriberIdAndSummonerId(userId, summonerId)
                ?: throw BusinessException(SummonerSubscriberErrorCode.SUMMONER_SUBSCRIBER_NOT_FOUND)

        SummonerSubscriberResponse.SummonerSubscriberInfo(
            id = subscriber.id!!,
            subscriberId = subscriber.subscriberId,
            summonerId = subscriber.summonerId,
            summonerGameName = subscriber.summonerGameName,
            summonerTagLine = subscriber.summonerTagLine,
        )
    }

    override suspend fun getSubscriberBySummonerId(summonerId: Long): List<SummonerSubscriberResponse.SummonerSubscriberInfo> =
        coroutineScope {
            summonerSubscriberRepository.findAllBySummonerId(summonerId)
                .map {
                    SummonerSubscriberResponse.SummonerSubscriberInfo(
                        id = it.id!!,
                        subscriberId = it.subscriberId,
                        summonerId = it.summonerId,
                        summonerGameName = it.summonerGameName,
                        summonerTagLine = it.summonerTagLine,
                    )
                }.toList()
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