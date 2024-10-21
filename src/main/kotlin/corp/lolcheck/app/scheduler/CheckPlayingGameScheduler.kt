package corp.lolcheck.app.scheduler

import corp.lolcheck.app.notification.service.interfaces.NotificationService
import corp.lolcheck.app.subcribe.service.interfaces.SummonerSubscriberService
import corp.lolcheck.app.summoners.service.interfaces.SummonerService
import corp.lolcheck.infrastructure.riot.RiotClient
import kotlinx.coroutines.coroutineScope
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class CheckPlayingGameScheduler(
    private val riotClient: RiotClient,
    private val notificationService: NotificationService,
    private val summonerService: SummonerService,
    private val summonerSubscriberService: SummonerSubscriberService
) {

    @Scheduled(cron = "* 1 * * * *")
    suspend fun checkPlayingGame(): Unit = coroutineScope {
        
    }
}