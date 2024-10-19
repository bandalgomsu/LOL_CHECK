package corp.lolcheck.app.summoners.controller

import corp.lolcheck.app.summoners.domain.Summoner
import corp.lolcheck.app.summoners.repository.SummonerRepository
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class SummonerController(
    private val summonerRepository: SummonerRepository
) {

   
}