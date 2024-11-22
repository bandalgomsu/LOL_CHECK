//package corp.lolcheck.app.scheduler
//
//import corp.lolcheck.app.device.service.interfaces.DeviceService
//import corp.lolcheck.app.notification.dto.NotificationRequest
//import corp.lolcheck.app.notification.service.interfaces.NotificationService
//import corp.lolcheck.app.subcribe.service.interfaces.SummonerSubscriberService
//import corp.lolcheck.app.summoners.domain.Summoner
//import corp.lolcheck.app.summoners.service.interfaces.SummonerService
//import corp.lolcheck.infrastructure.riot.RiotClient
//import corp.lolcheck.infrastructure.riot.RiotClientData
//import kotlinx.coroutines.Deferred
//import kotlinx.coroutines.async
//import kotlinx.coroutines.awaitAll
//import kotlinx.coroutines.coroutineScope
//import kotlinx.coroutines.flow.Flow
//import org.springframework.scheduling.annotation.Scheduled
//import org.springframework.stereotype.Component
//
//@Component
//class CheckPlayingGameScheduler(
//    private val riotClient: RiotClient,
//    private val notificationService: NotificationService,
//    private val summonerService: SummonerService,
//    private val summonerSubscriberService: SummonerSubscriberService,
//    private val deviceService: DeviceService,
//) {
//
//    @Scheduled(cron = "1 * * * * *")
//    suspend fun checkPlayingGame(): Unit = coroutineScope {
//        val summoners: Flow<Summoner> = summonerService.getSummonersLimit49()
//
//        val updatedSummoners: MutableList<Summoner> = mutableListOf()
//        val jobs: MutableList<Deferred<Unit>> = mutableListOf()
//
//        summoners.collect {
//            val job = async {
//                val currentGameInfo: RiotClientData.CurrentGameResponse = riotClient.checkCurrentGameInfo(it.puuid)
//
//                if (currentGameInfo.isCurrentPlayingGame && it.recentGameId != currentGameInfo.gameId) {
//                    it.updateRecentGameId(currentGameInfo.gameId!!)
//
//                    updatedSummoners.add(it)
//                }
//            }
//
//            jobs.add(job)
//        }
//
//        jobs.awaitAll()
//
//        if (updatedSummoners.isNotEmpty()) {
//            summonerService.updateSummoners(processSendMulticastMessage(updatedSummoners))
//        }
//    }
//
//    private suspend fun processSendMulticastMessage(summoners: List<Summoner>): List<Summoner> = coroutineScope {
//        summoners.map {
//            async {
//                val tokens: List<String> = getTokens(it)
//
//                val request = NotificationRequest.SendMulticastRequest.createPlayingGameMulticastRequest(
//                    summoner = it,
//                    tokens = tokens
//                )
//
//                notificationService.sendMulticastMessage(request)
//
//                it
//            }
//        }.awaitAll()
//    }
//
//
//    private suspend fun getTokens(summoner: Summoner): List<String> = coroutineScope {
//        val subscriberIds: List<Long> =
//            summonerSubscriberService.getSubscriberBySummonerId(summoner.id!!).map { it.subscriberId }
//        deviceService.getDeviceTokensByUserIds(subscriberIds)
//    }
//}
