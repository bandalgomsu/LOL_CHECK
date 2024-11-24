package corp.lolcheck.app.subcribe.controller

import com.ninjasquad.springmockk.MockkBean
import corp.lolcheck.app.TestSecurityConfig
import corp.lolcheck.app.auth.config.SecurityConfig
import corp.lolcheck.app.auth.data.CustomUserDetails
import corp.lolcheck.app.subcribe.SummonerSubscriberTestConst.GAME_NAME
import corp.lolcheck.app.subcribe.SummonerSubscriberTestConst.INTRODUCE
import corp.lolcheck.app.subcribe.SummonerSubscriberTestConst.SUMMONER_ID
import corp.lolcheck.app.subcribe.SummonerSubscriberTestConst.TAG_LINE
import corp.lolcheck.app.subcribe.SummonerSubscriberTestConst.USER_ID
import corp.lolcheck.app.subcribe.dto.SummonerSubscriberResponse
import corp.lolcheck.app.subcribe.service.interfaces.SummonerSubscriberService
import corp.lolcheck.app.users.domain.User
import corp.lolcheck.app.users.type.Role
import io.mockk.coEvery
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.test.context.TestSecurityContextHolder
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import kotlin.test.assertEquals
import corp.lolcheck.app.subcribe.SummonerSubscriberTestConst.SUBSCRIBER_ID as SUBSCRIBER_ID1

@ExtendWith(SpringExtension::class)
@WebFluxTest(
    controllers = [SummonerSubscriberController::class],
    excludeAutoConfiguration = [SecurityConfig::class]
)
@Import(TestSecurityConfig::class)
class SummonerSubscriberControllerTest {
    @Autowired
    private lateinit var webTestClient: WebTestClient

    @MockkBean
    private lateinit var summonerSubscriberService: SummonerSubscriberService

    val subscriberInfo = SummonerSubscriberResponse.SummonerSubscriberInfo(
        id = 1L,
        subscriberId = SUBSCRIBER_ID1,
        summonerId = SUMMONER_ID,
        summonerGameName = GAME_NAME,
        summonerTagLine = TAG_LINE,
        summonerIntroduce = INTRODUCE,
    )

    @BeforeEach
    fun BEFORE() {
        MOCK_SECURITY()
    }

    fun MOCK_SECURITY() {
        val userId = 1L

        val mockAuthentication = UsernamePasswordAuthenticationToken(
            CustomUserDetails(
                User(
                    id = USER_ID,
                    email = "TEST",
                    password = "TEST",
                    role = Role.USER
                )
            ), null, emptyList()
        )

        TestSecurityContextHolder.getContext().authentication = mockAuthentication
    }

    @Test
    @WithMockUser
    @DisplayName("SUBSCRIBE_SUMMONER_TEST_SUCCESS")
    fun SUBSCRIBE_SUMMONER_TEST_SUCCESS() = runTest {
        coEvery { summonerSubscriberService.subscribeSummoner(USER_ID, SUMMONER_ID) } returns subscriberInfo

        webTestClient
            .post()
            .uri("/api/v1/subscribe/me/${SUMMONER_ID}")
            .contentType(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody(SummonerSubscriberResponse.SummonerSubscriberInfo::class.java)
            .isEqualTo(subscriberInfo)
    }

    @Test
    @WithMockUser
    @DisplayName("GET_MY_SUBSCRIBE_SUMMONERS_SUCCESS")
    fun GET_MY_SUBSCRIBE_SUMMONERS_SUCCESS() = runTest {
        coEvery { summonerSubscriberService.getMySubscribes(userId = USER_ID) } returns flow { emit(subscriberInfo) }

        val response = webTestClient
            .get()
            .uri("/api/v1/subscribe/me")
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBodyList(SummonerSubscriberResponse.SummonerSubscriberInfo::class.java)
            .returnResult()
            .responseBody!!

        assertEquals(1, response.size)
        assertEquals(1L, response[0].id)
        assertEquals(SUMMONER_ID, response[0].summonerId)
        assertEquals(SUBSCRIBER_ID1, response[0].subscriberId)
        assertEquals(GAME_NAME, response[0].summonerGameName)
        assertEquals(TAG_LINE, response[0].summonerTagLine)
        assertEquals(INTRODUCE, response[0].summonerIntroduce)
    }

    @Test
    @DisplayName("GET_MY_SUBSCRIBE_SUMMONER_SUCCESS")
    fun GET_MY_SUBSCRIBE_SUMMONER_SUCCESS() = runTest {

        coEvery { summonerSubscriberService.getMySubscribe(USER_ID, SUMMONER_ID) } returns subscriberInfo

        webTestClient
            .get()
            .uri("/api/v1/subscribe/me/${SUMMONER_ID}")
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody(SummonerSubscriberResponse.SummonerSubscriberInfo::class.java)
            .isEqualTo(subscriberInfo)
    }

    @Test
    @DisplayName("UNSUBSCRIBE_SUCCESS")
    fun UNSUBSCRIBE_SUCCESS() = runTest {
        coEvery { summonerSubscriberService.unsubscribeSummoner(USER_ID, SUMMONER_ID) } returns Unit

        webTestClient
            .delete()
            .uri("/api/v1/subscribe/me/${SUMMONER_ID}")
            .exchange()
            .expectStatus().isOk
    }
}