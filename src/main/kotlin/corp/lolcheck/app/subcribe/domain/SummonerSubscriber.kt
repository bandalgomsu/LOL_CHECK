package corp.lolcheck.app.subcribe.domain

import corp.lolcheck.common.entity.BaseEntity
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
    var subscriberId: Long,
    @Column(value = "summoner_game_name")
    var summonerGameName: String,
    @Column(value = "summoner_tag_line")
    var summonerTagLine: String,
) : BaseEntity() {
}