package corp.lolcheck.app.subcribe.repository

import corp.lolcheck.app.subcribe.domain.SummonerSubscriber
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface SummonerSubscriberRepository : CoroutineCrudRepository<SummonerSubscriber, Long> {

    suspend fun findBySubscriberIdAndSummonerId(subscriberId: Long, summonerId: Long): SummonerSubscriber?
    suspend fun findAllBySubscriberId(subscriberId: Long): Flow<SummonerSubscriber>
    suspend fun findAllBySubscriberIdIn(subscriberIds: List<Long>): Flow<SummonerSubscriber>
    suspend fun findAllBySummonerId(summonerId: Long): Flow<SummonerSubscriber>
}