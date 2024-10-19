package corp.lolcheck.app.subcribe.service

import corp.lolcheck.app.subcribe.dto.SummonerSubscriberResponse
import corp.lolcheck.app.subcribe.repository.SummonerSubscriberRepository
import corp.lolcheck.app.subcribe.service.interfaces.SummonerSubscriberService
import corp.lolcheck.app.summoners.service.interfaces.SummonerService
import corp.lolcheck.infrastructure.riot.RiotClient
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
    ): SummonerSubscriberResponse.SummonerSubscriberInfo {

    }

    override suspend fun unsubscribeSummoner(userId: Long, summonerId: Long) {
        TODO("Not yet implemented")
    }
}