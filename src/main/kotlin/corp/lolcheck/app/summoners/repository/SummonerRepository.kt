package corp.lolcheck.app.summoners.repository

import corp.lolcheck.app.summoners.domain.Summoner
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface SummonerRepository : ReactiveCrudRepository<Summoner, Long> {
    fun findByGameNameAndTagLine(gameName: String, tagLine: String): Mono<Summoner>
}