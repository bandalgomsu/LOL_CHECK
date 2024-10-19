package corp.lolcheck.app.subcribe.repository

import corp.lolcheck.app.subcribe.domain.SummonerSubscriber
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface SummonerSubscriberRepository : CoroutineCrudRepository<SummonerSubscriber, Long> {

    suspend fun findBySubscriberIdAndSummonerId(subscriberId: Long, summonerId: Long): SummonerSubscriber?
    fun findBySubscriberId(subscriberId: Long): Flow<SummonerSubscriber>
}