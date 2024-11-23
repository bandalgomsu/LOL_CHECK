package corp.lolcheck.app.subcribe.dto

import corp.lolcheck.app.subcribe.domain.SummonerSubscriber
import corp.lolcheck.app.summoners.domain.Summoner

class SummonerSubscriberResponse {

    data class SummonerSubscriberInfo(
        val id: Long,
        val subscriberId: Long,
        val summonerId: Long,
        val summonerGameName: String,
        val summonerTagLine: String,
        val summonerIntroduce: String? = null
    ) {
        companion object {
            fun of(subscriber: SummonerSubscriber, summoner: Summoner): SummonerSubscriberInfo {
                return SummonerSubscriberResponse.SummonerSubscriberInfo(
                    id = subscriber.id!!,
                    subscriberId = subscriber.subscriberId,
                    summonerId = subscriber.summonerId,
                    summonerGameName = summoner.gameName,
                    summonerTagLine = summoner.tagLine,
                    summonerIntroduce = summoner.introduce
                )
            }
        }
    }
}