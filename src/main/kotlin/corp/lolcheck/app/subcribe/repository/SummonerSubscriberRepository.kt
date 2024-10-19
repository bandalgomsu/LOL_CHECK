package corp.lolcheck.app.subcribe.repository

import corp.lolcheck.app.subcribe.domain.SummonerSubscriber
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface SummonerSubscriberRepository : ReactiveCrudRepository<SummonerSubscriber, Long> {

    fun findBySubscriberIdAndSummonerId(subscriberId: Long, summonerId: Long): Mono<SummonerSubscriber>
}