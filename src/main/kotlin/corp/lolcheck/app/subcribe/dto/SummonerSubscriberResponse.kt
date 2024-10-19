package corp.lolcheck.app.subcribe.dto

class SummonerSubscriberResponse {

    data class SummonerSubscriberInfo(
        val id: Long,
        val subscriberId: Long,
        val summonerId: Long
    )
}