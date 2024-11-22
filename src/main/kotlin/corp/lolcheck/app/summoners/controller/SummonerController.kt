package corp.lolcheck.app.summoners.controller

import corp.lolcheck.app.summoners.domain.Summoner
import corp.lolcheck.app.summoners.repository.SummonerRepository
import corp.lolcheck.app.summoners.service.interfaces.SummonerService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.coroutineScope
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "소환사", description = "소환사 정보를 관리 합니다")
@RestController
class SummonerController(
    private val summonerService: SummonerService,
    private val summonerRepository: SummonerRepository,
) {
    @Operation(summary = "TagLine , GameName을 통한 소환사 정보 조회", description = "TagLine , GameName을 통한 소환사 정보를 조회합니다")
    @GetMapping("/api/v1/summoner")
    suspend fun getSummonerInfoByTagLineAndGameName(
        @RequestParam tagLine: String,
        @RequestParam gameName: String
    ) = coroutineScope {
        
        summonerService.getSummonerInfoByGameNameAndTagLine(gameName, tagLine)
    }
}