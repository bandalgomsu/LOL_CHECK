package corp.lolcheck.app.summoners.controller

import com.ninjasquad.springmockk.MockkBean
import corp.lolcheck.app.TestSecurityConfig
import corp.lolcheck.app.auth.config.SecurityConfig
import corp.lolcheck.app.auth.data.CustomUserDetails
import corp.lolcheck.app.subcribe.SummonerSubscriberTestConst
import corp.lolcheck.app.summoners.SummonerTestConstant
import corp.lolcheck.app.summoners.domain.Summoner
import corp.lolcheck.app.summoners.dto.SummonerResponse
import corp.lolcheck.app.summoners.service.interfaces.SummonerService
import corp.lolcheck.app.users.domain.User
import corp.lolcheck.app.users.type.Role
import io.mockk.coEvery
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

@ExtendWith(SpringExtension::class)
@WebFluxTest(
    controllers = [SummonerController::class],
    excludeAutoConfiguration = [SecurityConfig::class]
)
@Import(TestSecurityConfig::class)
class SummonerControllerTest {
    @Autowired
    private lateinit var webTestClient: WebTestClient

    @MockkBean
    private lateinit var summonerService: SummonerService

    val summoner = Summoner(
        id = SummonerTestConstant.SUMMONER_ID,
        puuid = SummonerTestConstant.PUUID,
        gameName = SummonerTestConstant.GAME_NAME,
        tagLine = SummonerTestConstant.TAG_LINE,
        introduce = SummonerTestConstant.INTRODUCE,
        recentGameId = SummonerTestConstant.RECENT_GAME_ID
    )

    val summonerInfo: SummonerResponse.SummonerInfo = SummonerResponse.SummonerInfo.from(summoner)

    @BeforeEach
    fun BEFORE() {
        MOCK_SECURITY()
    }

    fun MOCK_SECURITY() {
        val userId = 1L

        val mockAuthentication = UsernamePasswordAuthenticationToken(
            CustomUserDetails(
                User(
                    id = SummonerSubscriberTestConst.USER_ID,
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
    @DisplayName("GET_SUMMONER_INFO_BY_TAG_LINE_AND_GAME_NAME_SUCCESS")
    fun GET_SUMMONER_INFO_BY_TAG_LINE_AND_GAME_NAME_SUCCESS() = runTest {

        coEvery {
            summonerService.getSummonerInfoByGameNameAndTagLine(
                SummonerTestConstant.GAME_NAME,
                SummonerTestConstant.TAG_LINE
            )
        } returns summonerInfo

        webTestClient
            .get()
            .uri("/api/v1/summoner?tagLine=${SummonerTestConstant.TAG_LINE}&gameName=${SummonerTestConstant.GAME_NAME}")
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody(SummonerResponse.SummonerInfo::class.java)
            .isEqualTo(summonerInfo)
    }
}