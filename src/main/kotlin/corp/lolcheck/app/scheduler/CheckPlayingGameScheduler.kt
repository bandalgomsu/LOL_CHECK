package corp.lolcheck.app.scheduler

import corp.lolcheck.app.device.service.interfaces.DeviceService
import corp.lolcheck.app.notification.dto.NotificationRequest
import corp.lolcheck.app.notification.service.interfaces.NotificationService
import corp.lolcheck.app.subcribe.service.interfaces.SummonerSubscriberService
import corp.lolcheck.app.summoners.domain.Summoner
import corp.lolcheck.app.summoners.service.interfaces.SummonerService
import corp.lolcheck.infrastructure.riot.RiotClient
import corp.lolcheck.infrastructure.riot.RiotClientData
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class CheckPlayingGameScheduler(
    private val riotClient: RiotClient,
    private val notificationService: NotificationService,
    private val summonerService: SummonerService,
    private val summonerSubscriberService: SummonerSubscriberService,
    private val deviceService: DeviceService,
) {

    @Scheduled(cron = "1 * * * * *")
    @Transactional
    suspend fun checkPlayingGame(): Unit = coroutineScope {
        val summoners: Flow<Summoner> = summonerService.getSummonersLimit49()

        val updatedSummoners: MutableList<Summoner> = mutableListOf()

        summoners.collect {
            val currentGameInfo: RiotClientData.CurrentGameResponse = riotClient.checkCurrentGameInfo(it.puuid)

            if (currentGameInfo.isCurrentPlayingGame) {
                updatedSummoners.add(it)
            }
        }

        if (updatedSummoners.isNotEmpty()) {
            val updateSummonerIds: List<Long> = updatedSummoners.map { it.id!! }
            launch { summonerService.updateSummonerRecentGameByIds(updateSummonerIds) }

            processSendMulticastMessage(updatedSummoners)
        }
    }

    private suspend fun processSendMulticastMessage(summoners: List<Summoner>) = coroutineScope {
        summoners.forEach {
            launch {
                val tokens: List<String> = getTokens(it)

                val request = NotificationRequest.SendMulticastRequest.createPlayingGameMulticastRequest(
                    gameName = it.gameName,
                    tagLine = it.tagLine,
                    tokens = tokens
                )
                notificationService.sendMulticastMessage(request)
            }
        }
    }


    private suspend fun getTokens(summoner: Summoner): List<String> = coroutineScope {
        val subscriberIds: List<Long> =
            summonerSubscriberService.getSubscriberBySummonerId(summoner.id!!).map { it.subscriberId }
        deviceService.getDeviceTokensByUserIds(subscriberIds)
    }
}
