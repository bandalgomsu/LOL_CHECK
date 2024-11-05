package corp.lolcheck.app.subcribe.service.interfaces

import corp.lolcheck.app.subcribe.dto.SummonerSubscriberResponse
import kotlinx.coroutines.flow.Flow

interface SummonerSubscriberService {

    suspend fun subscribeSummoner(userId: Long, summonerId: Long): SummonerSubscriberResponse.SummonerSubscriberInfo

    suspend fun unsubscribeSummoner(userId: Long, summonerId: Long): Unit

    suspend fun getMySubscribes(userId: Long): Flow<SummonerSubscriberResponse.SummonerSubscriberInfo>
    suspend fun getMySubscribe(userId: Long, summonerId: Long): SummonerSubscriberResponse.SummonerSubscriberInfo
    suspend fun getSubscriberIdsBySummonerIds(summonerIds: List<Long>): List<Long>
    suspend fun getSubscriberBySummonerId(summonerId: Long): List<SummonerSubscriberResponse.SummonerSubscriberInfo>

}