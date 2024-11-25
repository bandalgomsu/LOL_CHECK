package corp.lolcheck.app.auth.controller

import com.ninjasquad.springmockk.MockkBean
import corp.lolcheck.app.auth.AuthTestConstant
import corp.lolcheck.app.auth.config.SecurityConfig
import corp.lolcheck.app.auth.dto.AuthRequest
import corp.lolcheck.app.auth.dto.AuthResponse
import corp.lolcheck.app.auth.dto.MailRequest
import corp.lolcheck.app.auth.dto.MailResponse
import corp.lolcheck.app.auth.service.AuthService
import corp.lolcheck.app.auth.service.JwtService
import corp.lolcheck.app.auth.service.MailService
import corp.lolcheck.app.auth.service.UserDetailService
import io.mockk.coEvery
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient

@ExtendWith(SpringExtension::class)
@WebFluxTest(
    controllers = [AuthController::class],
)
@Import(SecurityConfig::class)
class AuthControllerTest {
    val mailRequest = MailRequest()
    val authRequest = AuthRequest()

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @MockkBean
    private lateinit var authService: AuthService

    @MockkBean
    private lateinit var mailService: MailService

    @MockkBean
    private lateinit var jwtService: JwtService

    @MockkBean
    private lateinit var userDetailService: UserDetailService

    val tokenResponse = AuthResponse.TokenResponse(
        accessToken = AuthTestConstant.TEST_ACCESS_TOKEN,
        refreshToken = AuthTestConstant.TEST_REFRESH_TOKEN
    )

    @Test
    @DisplayName("HEALTH_SUCCESS")
    fun HEALTH_SUCCESS() = runTest {
        webTestClient
            .get()
            .uri("/health")
            .exchange()
            .expectStatus().isOk
            .expectBody(String::class.java)
            .isEqualTo("health")

    }

    @Test
    @DisplayName("SIGN_UP_SUCCESS")
    fun SIGN_UP_SUCCESS() = runTest {
        val request = AuthRequest.SignUpRequest(AuthTestConstant.EMAIL, AuthTestConstant.PASSWORD)

        coEvery { authService.signUp(request) } returns tokenResponse

        webTestClient
            .post()
            .uri("/api/v1/auth/signUp")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody(AuthResponse.TokenResponse::class.java)
            .isEqualTo(tokenResponse)
    }

    @Test
    @DisplayName("SEND_SIGN_UP_VERIFYING_MAIL")
    fun SEND_SIGN_UP_VERIFYING_MAIL() = runTest {
        val request = MailRequest.SendSignUpVerifyingMailRequest(email = AuthTestConstant.EMAIL)

        val response = MailResponse.SendSignUpVerifyingMailResponse(
            authNumber = "NUMBER"
        )

        coEvery { mailService.sendSignUpVerifyingMail(request.email) } returns response

        webTestClient
            .post()
            .uri("/api/v1/auth/signUp/mail")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody(MailResponse.SendSignUpVerifyingMailResponse::class.java)
            .isEqualTo(response)
    }

    @Test
    @DisplayName("VERIFY_SIGN_UP_MAIL")
    fun VERIFY_SIGN_UP_MAIL() = runTest {
        val authNumber = "123456"

        val request = MailRequest.VerifySignUpMailRequest(
            email = AuthTestConstant.EMAIL,
            authNumber = authNumber
        )

        val response = MailResponse.VerifySignUpMailResponse(
            email = AuthTestConstant.EMAIL,
            isVerified = true
        )

        coEvery { mailService.verifySignUpEmail(request.email, request.authNumber) } returns response

        webTestClient
            .post()
            .uri("/api/v1/auth/signUp/mail/verify")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody(MailResponse.VerifySignUpMailResponse::class.java)
            .isEqualTo(response)
    }

    @Test
    @DisplayName("LOGIN_SUCCESS")
    fun LOGIN_SUCCESS() = runTest {
        val request = AuthRequest.LoginRequest(
            email = AuthTestConstant.EMAIL,
            password = AuthTestConstant.PASSWORD
        )

        coEvery { authService.login(request) } returns tokenResponse

        webTestClient
            .post()
            .uri("/api/v1/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody(AuthResponse.TokenResponse::class.java)
            .isEqualTo(tokenResponse)
    }

    @Test
    @DisplayName("REFRESH_SUCCESS")
    fun REFRESH_SUCCESS() = runTest {
        val request = AuthRequest.RefreshRequest(
            refreshToken = AuthTestConstant.TEST_REFRESH_TOKEN
        )

        coEvery { authService.refresh(request) } returns tokenResponse

        webTestClient
            .post()
            .uri("/api/v1/auth/refresh")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody(AuthResponse.TokenResponse::class.java)
            .isEqualTo(tokenResponse)
    }
}