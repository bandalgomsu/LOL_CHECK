package corp.lolcheck.app.summoners.dto

import corp.lolcheck.app.summoners.domain.Summoner
import java.time.LocalDateTime

class SummonerResponse {
    data class SummonerInfo(
        val summonerId: Long? = null,
        val puuid: String,
        val gameName: String,
        val tagLine: String,
        val introduce: String? = null,
        val recentGameId: Long? = null,
        val updatedAt: LocalDateTime? = null,
    ) {
        companion object {
            fun from(summoner: Summoner): SummonerInfo {
                return SummonerInfo(
                    summonerId = summoner.id,
                    puuid = summoner.puuid,
                    gameName = summoner.gameName,
                    tagLine = summoner.tagLine,
                    introduce = summoner.introduce,
                    recentGameId = summoner.recentGameId,
                    updatedAt = summoner.updatedAt
                )
            }
        }
    }
}
