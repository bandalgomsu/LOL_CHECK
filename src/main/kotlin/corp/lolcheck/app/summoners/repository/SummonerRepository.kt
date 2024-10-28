package corp.lolcheck.app.summoners.repository

import corp.lolcheck.app.summoners.domain.Summoner
import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Pageable
import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface SummonerRepository : CoroutineCrudRepository<Summoner, Long> {
    suspend fun findByGameNameAndTagLine(gameName: String, tagLine: String): Summoner?

    suspend fun findAllByGameNameExists(pageable: Pageable): Flow<Summoner>
    suspend fun findTop49ByOrderByUpdatedAtAsc(): Flow<Summoner>

    @Modifying
    @Query("UPDATE summoner SET recent_game_id = 1 WHERE id IN (:summonerIds)")
    suspend fun updateAllByIdIn(@Param("summonerIds") summonerIds: List<Long>)
}