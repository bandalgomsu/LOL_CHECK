package corp.lolcheck.app.subcribe.service

import corp.lolcheck.app.subcribe.domain.SummonerSubscriber
import corp.lolcheck.app.subcribe.dto.SummonerSubscriberResponse
import corp.lolcheck.app.subcribe.repository.SummonerSubscriberRepository
import corp.lolcheck.app.subcribe.service.interfaces.SummonerSubscriberService
import corp.lolcheck.app.summoners.service.interfaces.SummonerService
import corp.lolcheck.infrastructure.riot.RiotClient
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.stereotype.Service

@Service
class SummonerSubscriberServiceImpl(
    private val summonerSubscriberRepository: SummonerSubscriberRepository,
    private val riotClient: RiotClient,
    private val summonerService: SummonerService
) : SummonerSubscriberService {
    override suspend fun subscribeSummoner(
        userId: Long,
        summonerId: Long
    ): SummonerSubscriberResponse.SummonerSubscriberInfo = coroutineScope {
        val subscriber: SummonerSubscriber = SummonerSubscriber(
            subscriberId = userId,
            summonerId = summonerId,
        )

        val save: SummonerSubscriber = summonerSubscriberRepository.save(subscriber).awaitSingle()

        SummonerSubscriberResponse.SummonerSubscriberInfo(
            id = save.id!!,
            subscriberId = save.subscriberId,
            summonerId = save.summonerId
        )
    }

    override suspend fun unsubscribeSummoner(userId: Long, summonerId: Long): Unit = coroutineScope {
        val subscriber = summonerSubscriberRepository.findBySubscriberIdAndSummonerId(userId, summonerId).awaitSingle()

        summonerSubscriberRepository.delete(subscriber).awaitSingle()
    }
}