package corp.lolcheck.app.summoners.service.interfaces

import corp.lolcheck.app.summoners.domain.Summoner
import corp.lolcheck.app.summoners.dto.SummonerResponse
import kotlinx.coroutines.flow.Flow

interface SummonerService {
    suspend fun registrySummoner(gameName: String, tagLine: String): SummonerResponse.SummonerInfo
    suspend fun getSummonerInfoByGameNameAndTagLine(
        gameName: String,
        tagLine: String
    ): SummonerResponse.SummonerInfo

    suspend fun getSummonerById(summonerId: Long): Summoner
    suspend fun getSummonersLimit49(): Flow<Summoner>
    suspend fun updateSummonerRecentGameByIds(summonerIds: MutableList<Long>)
}