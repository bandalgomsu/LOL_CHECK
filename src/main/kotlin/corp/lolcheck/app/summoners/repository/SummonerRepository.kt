package corp.lolcheck.app.summoners.repository

import corp.lolcheck.app.summoners.domain.Summoner
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface SummonerRepository : ReactiveCrudRepository<Summoner,Long> {

}