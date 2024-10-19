package corp.lolcheck.app.summoners.dto

import java.time.LocalDateTime

class SummonerResponse {
    data class SummonerInfo(
        val summonerId: Long? = null,
        val puuid: String,
        val gameName: String,
        val tagLine: String,
        val recentGame: LocalDateTime? = null
    )
}
