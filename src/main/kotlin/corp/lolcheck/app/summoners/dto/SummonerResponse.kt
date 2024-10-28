package corp.lolcheck.app.summoners.dto

class SummonerResponse {
    data class SummonerInfo(
        val summonerId: Long? = null,
        val puuid: String,
        val gameName: String,
        val tagLine: String,
        val introduce: String? = null,
        val recentGameId: Long? = null,
    )
}
