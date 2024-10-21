package corp.lolcheck.app.summoners.domain

import corp.lolcheck.common.entity.BaseEntity
import kotlinx.coroutines.coroutineScope
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("summoner")
class Summoner(
    @Id
    var id: Long? = null,
    var puuid: String,
    var recentGame: LocalDateTime? = null,
    var gameName: String,
    var tagLine: String,
) : BaseEntity() {
    suspend fun updateRecentGame(): Unit = coroutineScope {
        recentGame = LocalDateTime.now()
    }
}
