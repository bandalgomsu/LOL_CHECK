package corp.lolcheck.app.auth.controller

import corp.lolcheck.app.auth.dto.AuthRequest
import corp.lolcheck.app.auth.dto.AuthResponse
import corp.lolcheck.app.auth.dto.MailRequest
import corp.lolcheck.app.auth.dto.MailResponse
import corp.lolcheck.app.auth.service.AuthService
import corp.lolcheck.app.auth.service.MailService
import io.swagger.v3.oas.annotations.Hidden
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.coroutineScope
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@Tag(name = "인증", description = "인증 정보를 관리 합니다")
@RestController
class AuthController(
    private val authService: AuthService,
    private val mailService: MailService
) {

    @GetMapping("/health")
    @Hidden
    suspend fun health(): String {
        return "health"
    }

    @Operation(summary = "회원가입", description = "Email , Password를 통해서 회원가입을 합니다")
    @PostMapping("/api/v1/auth/signUp")
    suspend fun signUp(@RequestBody request: AuthRequest.SignUpRequest): AuthResponse.TokenResponse = coroutineScope {
        authService.signUp(request)
    }

    @Operation(summary = "회원가입 인증 메일 발송", description = "회원가입 인증 메일을 발송합니다.")
    @PostMapping("/api/v1/auth/signUp/mail")
    suspend fun sendSignUpVerifyingMail(@RequestBody request: MailRequest.SendSignUpVerifyingMailRequest): MailResponse.SendSignUpVerifyingMailResponse =
        coroutineScope {
            mailService.sendSignUpVerifyingMail(request.email)
        }

    @Operation(summary = "회원가입 인증 메일 검증", description = "회원가입 인증 메일을 검증합니다.")
    @PostMapping("/api/v1/auth/signUp/mail/verified")
    suspend fun verifySignUpMail(@RequestBody request: MailRequest.VerifySignUpMailRequest): MailResponse.VerifySignUpMailResponse =
        coroutineScope {
            mailService.verifySignUpEmail(request.email, request.authNumber)
        }

    @Operation(summary = "로그인", description = "Email , Password를 통해서 로그인을 합니다")
    @PostMapping("/api/v1/auth/login")
    suspend fun login(@RequestBody request: AuthRequest.LoginRequest): AuthResponse.TokenResponse = coroutineScope {
        authService.login(request)
    }

    @Operation(summary = "리프레쉬", description = "리프레쉬 토큰을 통해서 토큰을 재발급 합니다")
    @PostMapping("/api/v1/auth/refresh")
    suspend fun refresh(@RequestBody request: AuthRequest.RefreshRequest): AuthResponse.TokenResponse = coroutineScope {
        authService.refresh(request)
    }
}