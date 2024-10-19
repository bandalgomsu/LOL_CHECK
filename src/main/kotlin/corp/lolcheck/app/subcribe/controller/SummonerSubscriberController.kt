package corp.lolcheck.app.subcribe.controller

import corp.lolcheck.app.summoners.service.interfaces.SummonerService
import org.springframework.web.bind.annotation.RestController

@RestController
class SummonerSubscriberController(
    private val summonerService: SummonerService
) {
}