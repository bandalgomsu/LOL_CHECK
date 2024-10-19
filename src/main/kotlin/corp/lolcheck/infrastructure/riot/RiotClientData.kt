package corp.lolcheck.infrastructure.riot

class RiotClientData {
    data class GetPuuidResponse(
        val puuid: String,
        val gameName: String,
        val tagLine: String
    )
}

