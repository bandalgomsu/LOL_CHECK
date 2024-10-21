package corp.lolcheck.app.scheduler

import corp.lolcheck.app.notification.service.interfaces.NotificationService
import corp.lolcheck.app.subcribe.service.interfaces.SummonerSubscriberService
import corp.lolcheck.app.summoners.domain.Summoner
import corp.lolcheck.app.summoners.service.interfaces.SummonerService
import corp.lolcheck.infrastructure.riot.RiotClient
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
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
        val summoners: Flow<Summoner> = summonerService.getSummonersLimit49()

        val updateSummonerIds: MutableList<Long> = mutableListOf()

        async {
            summoners.collect {
                try {
                    val isPlayingGame = riotClient.checkCurrentGameInfo(it.puuid)
                    updateSummonerIds.add(it.id!!)
                } catch (_: Exception) {
                }
            }
        }.await()

        async { summonerService.updateSummonerRecentGameByIds(updateSummonerIds) }
        async { }
    }
}