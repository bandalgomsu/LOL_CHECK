package corp.lolcheck.app.subcribe.controller

import corp.lolcheck.app.subcribe.dto.SummonerSubscriberResponse
import corp.lolcheck.app.subcribe.service.interfaces.SummonerSubscriberService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import org.springframework.web.bind.annotation.*


@Tag(name = "소환사 구독", description = "소환사 구독 정보를 관리 합니다")
@RestController
class SummonerSubscriberController(
    private val summonerSubscriberService: SummonerSubscriberService
) {
    @Operation(summary = "소환사 구독", description = "소환사를 구독합니다")
    @PostMapping("/api/v1/subscribe/me/{summonerId}")
    suspend fun subscribeSummoner(
        @RequestParam userId: Long,
        @PathVariable summonerId: Long
    ): SummonerSubscriberResponse.SummonerSubscriberInfo = coroutineScope {
        summonerSubscriberService.subscribeSummoner(userId, summonerId)
    }

    @Operation(summary = "나의 구독 소환사 목록 조회", description = "나의 구독 소환사 목록 조회합니다")
    @GetMapping("/api/v1/subscribe/me")
    suspend fun getMySubscribeSummoner(@RequestParam userId: Long): Flow<SummonerSubscriberResponse.SummonerSubscriberInfo> =
        coroutineScope {
            summonerSubscriberService.getMySubscriberSummoner(userId)
        }

    @Operation(summary = "소환사 구독 취소", description = "소환사 구독을 취소합니다")
    @DeleteMapping("/api/v1/subscribe/me/{summonerId}")
    suspend fun unsubscribeSummoner(@RequestParam userId: Long, @PathVariable summonerId: Long) = coroutineScope {
        summonerSubscriberService.unsubscribeSummoner(userId, summonerId)
    }
}