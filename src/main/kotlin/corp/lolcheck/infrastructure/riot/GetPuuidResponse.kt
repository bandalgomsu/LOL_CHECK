package corp.lolcheck.infrastructure.riot

data class GetPuuidResponse(
    var puuid: String,
    var gameName: String,
    var tagLine: String
)
