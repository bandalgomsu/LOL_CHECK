package corp.lolcheck.app.subcribe.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("summoner_subscriber")
class SummonerSubscriber(
    @Id
    var id: Long? = null,
    @Column(value = "summoner_id")
    var summonerId: Long,
    @Column(value = "subscriber_id")
    var subscriberId: Long

) {
}