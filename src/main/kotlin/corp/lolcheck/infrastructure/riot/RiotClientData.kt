package corp.lolcheck.infrastructure.riot

class RiotClientData {
    data class PuuidGetResponse(
        val puuid: String,
        val gameName: String,
        val tagLine: String
    )

    data class CurrentGameResponse(
        val gameId: Long? = null,
        val gameType: String? = null,
        val gameMode: String? = null,
        val isCurrentPlayingGame: Boolean = true
    )
}

