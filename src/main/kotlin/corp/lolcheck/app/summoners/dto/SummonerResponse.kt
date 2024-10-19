package corp.lolcheck.app.summoners.dto

import java.time.LocalDateTime

class SummonerResponse {
    data class SummonerInfo(
        var summonerId: Long? = null,
        var puuid: String,
        var gameName: String,
        var tagLine: String,
        var recentGame: LocalDateTime? = null
    )
}
