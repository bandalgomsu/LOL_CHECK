package corp.lolcheck.app.summoners.domain

import corp.lolcheck.common.entity.BaseEntity
import kotlinx.coroutines.coroutineScope
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("summoner")
class Summoner(
    @Id
    var id: Long? = null,
    var puuid: String,
    var gameName: String,
    var tagLine: String,
    var introduce: String? = null,
    var recentGameId: Long? = null
) : BaseEntity() {
    suspend fun updateRecentGameId(gameId: Long): Unit = coroutineScope {
        recentGameId = gameId
    }
}
