//package corp.lolcheck.app.scheduler
//
//import corp.lolcheck.app.device.service.interfaces.DeviceService
//import corp.lolcheck.app.notification.dto.NotificationRequest
//import corp.lolcheck.app.notification.service.interfaces.NotificationService
//import corp.lolcheck.app.scheduler.SchedulerTestConstant.DEVICE_TOKEN
//import corp.lolcheck.app.scheduler.SchedulerTestConstant.GAME_ID
//import corp.lolcheck.app.scheduler.SchedulerTestConstant.GAME_MODE
//import corp.lolcheck.app.scheduler.SchedulerTestConstant.GAME_NAME
//import corp.lolcheck.app.scheduler.SchedulerTestConstant.GAME_TYPE
//import corp.lolcheck.app.scheduler.SchedulerTestConstant.PUUID
//import corp.lolcheck.app.scheduler.SchedulerTestConstant.SUMMONER_ID
//import corp.lolcheck.app.scheduler.SchedulerTestConstant.SUMMONER_SUBSCRIBER_ID
//import corp.lolcheck.app.scheduler.SchedulerTestConstant.TAG_LINE
//import corp.lolcheck.app.scheduler.SchedulerTestConstant.USER_ID
//import corp.lolcheck.app.subcribe.domain.SummonerSubscriber
//import corp.lolcheck.app.subcribe.dto.SummonerSubscriberResponse
//import corp.lolcheck.app.subcribe.service.interfaces.SummonerSubscriberService
//import corp.lolcheck.app.summoners.domain.Summoner
//import corp.lolcheck.app.summoners.service.interfaces.SummonerService
//import corp.lolcheck.infrastructure.riot.RiotClient
//import corp.lolcheck.infrastructure.riot.RiotClientData
//import io.mockk.coEvery
//import io.mockk.coVerify
//import io.mockk.junit5.MockKExtension
//import io.mockk.mockk
//import io.mockk.spyk
//import kotlinx.coroutines.flow.flow
//import kotlinx.coroutines.test.runTest
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.DisplayName
//import org.junit.jupiter.api.Test
//import org.junit.jupiter.api.extension.ExtendWith
//import org.mockito.MockitoAnnotations
//
//@ExtendWith(MockKExtension::class)
//class CheckPlayingGameSchedulerTest {
//
//    private var riotClient: RiotClient = mockk<RiotClient>()
//    private val notificationService: NotificationService = mockk<NotificationService>()
//    private val summonerService: SummonerService = mockk<SummonerService>()
//    private val summonerSubscriberService: SummonerSubscriberService = mockk<SummonerSubscriberService>()
//    private val deviceService: DeviceService = mockk<DeviceService>()
//
//    private val scheduler: CheckPlayingGameScheduler = spyk(
//        CheckPlayingGameScheduler(
//            riotClient,
//            notificationService,
//            summonerService,
//            summonerSubscriberService,
//            deviceService
//        )
//    )
//
//    val summoner: Summoner = Summoner(
//        id = SUMMONER_ID,
//        puuid = PUUID,
//        gameName = GAME_NAME,
//        tagLine = TAG_LINE,
//    )
//
//    val subscriber: SummonerSubscriber = SummonerSubscriber(
//        id = SUMMONER_SUBSCRIBER_ID,
//        subscriberId = USER_ID,
//        summonerId = SUMMONER_ID,
//    )
//
//    val subscriberInfo: SummonerSubscriberResponse.SummonerSubscriberInfo =
//        SummonerSubscriberResponse.SummonerSubscriberInfo(
//            id = SUMMONER_SUBSCRIBER_ID,
//            subscriberId = USER_ID,
//            summonerId = SUMMONER_ID,
//            summonerGameName = "TEST",
//            summonerTagLine = "TEST",
//        )
//
//    val currentGameResponse: RiotClientData.CurrentGameResponse = RiotClientData.CurrentGameResponse(
//        gameId = GAME_ID,
//        gameType = GAME_TYPE,
//        gameMode = GAME_MODE,
//        isCurrentPlayingGame = true
//    )
//
//    val notCurrentGameResponse: RiotClientData.CurrentGameResponse = RiotClientData.CurrentGameResponse(
//        isCurrentPlayingGame = false
//    )
//
//    val sendMulticastRequest: NotificationRequest.SendMulticastRequest = NotificationRequest.SendMulticastRequest(
//        title = "test",
//        body = "test",
//        tokens = listOf(DEVICE_TOKEN)
//    )
//
//    @BeforeEach
//    fun beforeEach() {
//        MockitoAnnotations.openMocks(this)
//    }
//
//    @Test
//    @DisplayName("스케쥴러_성공_테스트")
//    fun 스케쥴러_성공_테스트() = runTest {
//        coEvery { summonerService.getSummonersLimit49() } returns flow { emit(summoner) }
//        coEvery { riotClient.checkCurrentGameInfo(PUUID) } returns currentGameResponse
//        coEvery { summonerService.updateSummoners(any()) } returns Unit
//        coEvery { summonerSubscriberService.getSubscriberBySummonerId(SUMMONER_ID) } returns listOf(subscriberInfo)
//        coEvery { deviceService.getDeviceTokensByUserIds(listOf(USER_ID)) } returns listOf(DEVICE_TOKEN)
//        coEvery { notificationService.sendMulticastMessage(any()) } returns Unit
//
//        scheduler.checkPlayingGame()
//
//        coVerify(exactly = 1) { notificationService.sendMulticastMessage(any()) }
//    }
//
//    @Test
//    @DisplayName("스케쥴러_성공_소환사_없음_테스트")
//    fun 스케쥴러_성공_소환사_없음_테스트() = runTest {
//        coEvery { summonerService.getSummonersLimit49() } returns flow { }
//
//        scheduler.checkPlayingGame()
//
//        coVerify(exactly = 0) { riotClient.checkCurrentGameInfo(any()) }
//    }
//
//    @Test
//    @DisplayName("스케쥴러_성공_플레이_하는_유저_없음_테스트")
//    fun 스케쥴러_성공_플레이_하는_소환사_없음_테스트() = runTest {
//        coEvery { summonerService.getSummonersLimit49() } returns flow { emit(summoner) }
//        coEvery { riotClient.checkCurrentGameInfo(PUUID) } returns notCurrentGameResponse
//
//        scheduler.checkPlayingGame()
//
//        coVerify(exactly = 0) { summonerService.updateSummonerRecentGameByIds(any()) }
//    }
//}