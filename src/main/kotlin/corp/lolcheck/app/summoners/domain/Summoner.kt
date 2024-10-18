package corp.lolcheck.app.summoners.domain

import corp.lolcheck.common.entity.BaseEntity
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("summoner")
class Summoner(
    @Id
    var id : Long,
    var recentGame : LocalDateTime,
    var name : String
    ) : BaseEntity()
{}
