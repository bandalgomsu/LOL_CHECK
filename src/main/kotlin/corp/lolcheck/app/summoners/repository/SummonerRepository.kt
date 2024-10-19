package corp.lolcheck.app.summoners.repository

import corp.lolcheck.app.summoners.domain.Summoner
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface SummonerRepository : CoroutineCrudRepository<Summoner, Long> {
    suspend fun findByGameNameAndTagLine(gameName: String, tagLine: String): Summoner?
}