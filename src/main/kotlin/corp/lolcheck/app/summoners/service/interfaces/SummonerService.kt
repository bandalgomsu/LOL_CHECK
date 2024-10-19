package corp.lolcheck.app.summoners.service.interfaces

import corp.lolcheck.app.summoners.dto.SummonerResponse

interface SummonerService {
    suspend fun registrySummoner(gameName: String, tagLine: String): SummonerResponse.SummonerInfo
    suspend fun getSummonerPuuid(gameName: String, tagLine: String): String
    suspend fun checkPlayingGame(puuid: String): Boolean

}