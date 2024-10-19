package corp.lolcheck.infrastructure.riot

class RiotClientData {
    data class GetPuuidResponse(
        var puuid: String,
        var gameName: String,
        var tagLine: String
    )
}

