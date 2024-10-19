package corp.lolcheck.app.subcribe.controller

import corp.lolcheck.app.subcribe.dto.SummonerSubscriberResponse
import corp.lolcheck.app.subcribe.service.interfaces.SummonerSubscriberService
import kotlinx.coroutines.coroutineScope
import org.springframework.web.bind.annotation.*


@RestController
class SummonerSubscriberController(
    private val summonerSubscriberService: SummonerSubscriberService
) {

    @PostMapping("/api/v1/subscribe/{summonerId}")
    suspend fun subscribeSummoner(
        @RequestParam userId: Long,
        @PathVariable summonerId: Long
    ): SummonerSubscriberResponse.SummonerSubscriberInfo = coroutineScope {
        summonerSubscriberService.subscribeSummoner(userId, summonerId)
    }

    @DeleteMapping("/api/v1/subscribe/{summonerId}")
    suspend fun unsubscribeSummoner(@RequestParam userId: Long, @PathVariable summonerId: Long) = coroutineScope {
        summonerSubscriberService.unsubscribeSummoner(userId, summonerId)
    }
}