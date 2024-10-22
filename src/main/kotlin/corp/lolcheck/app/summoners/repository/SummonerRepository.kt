package corp.lolcheck.app.summoners.repository

import corp.lolcheck.app.summoners.domain.Summoner
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface SummonerRepository : CoroutineCrudRepository<Summoner, Long> {
    suspend fun findByGameNameAndTagLine(gameName: String, tagLine: String): Summoner?

    @Query("SELECT * FROM summoner LIMIT 49 ORDER BY recent_game DESC")
    suspend fun findAllLimit49OrderByUpdatedAt(): Flow<Summoner>

    @Modifying
    @Query("UPDATE summoner SET recent_game = now()")
    suspend fun updateAllByIdIn(summonerIds: List<Long>)
}