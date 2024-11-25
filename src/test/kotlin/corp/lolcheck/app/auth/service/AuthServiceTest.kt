package corp.lolcheck.app.auth.service

import AuthErrorCode
import JwtToken
import UserErrorCode
import corp.lolcheck.app.auth.AuthTestConstant
import corp.lolcheck.app.auth.data.AuthRedisKey
import corp.lolcheck.app.auth.dto.AuthRequest
import corp.lolcheck.app.auth.dto.AuthResponse
import corp.lolcheck.app.users.domain.User
import corp.lolcheck.app.users.repository.UserRepository
import corp.lolcheck.app.users.type.Role
import corp.lolcheck.common.exception.BusinessException
import corp.lolcheck.infrastructure.redis.RedisClient
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.security.crypto.password.PasswordEncoder
import reactor.core.publisher.Mono
import kotlin.test.assertEquals

class AuthServiceTest {
    val authRequest = AuthRequest()
    val authResponse = AuthResponse()

    private val jwtService: JwtService = mockk()
    private val userRepository: UserRepository = mockk()
    private val passwordEncoder: PasswordEncoder = mockk()
    private val redisClient: RedisClient = mockk()

    private val authService = AuthService(jwtService, userRepository, passwordEncoder, redisClient)

    val user = User(
        id = AuthTestConstant.USER_ID,
        email = AuthTestConstant.EMAIL,
        password = AuthTestConstant.PASSWORD,
        role = Role.USER
    )

    @Test
    @DisplayName("SIGN_UP_SUCCESS")
    fun SIGN_UP_SUCCESS() = runTest {

        coEvery {
            redisClient.getData(
                AuthRedisKey.IS_VERIFIED_USER.combineKeyValue(AuthTestConstant.EMAIL),
                String::class
            )
        } returns AuthTestConstant.EMAIL

        coEvery {
            userRepository.findByEmail(AuthTestConstant.EMAIL)
        } returns null

        coEvery { passwordEncoder.encode(AuthTestConstant.PASSWORD) } returns AuthTestConstant.PASSWORD
        coEvery { userRepository.save(any()) } returns user

        coEvery {
            jwtService.generateAccessToken(AuthTestConstant.EMAIL)
        } returns Mono.just(JwtToken(value = AuthTestConstant.TEST_ACCESS_TOKEN))
        coEvery {
            jwtService.generateRefreshToken(AuthTestConstant.EMAIL)
        } returns Mono.just(JwtToken(value = AuthTestConstant.TEST_REFRESH_TOKEN))

        val request = AuthRequest.SignUpRequest(
            email = AuthTestConstant.EMAIL,
            password = AuthTestConstant.PASSWORD
        )

        val response = authService.signUp(request)

        assertEquals(AuthTestConstant.TEST_ACCESS_TOKEN, response.accessToken)
        assertEquals(AuthTestConstant.TEST_REFRESH_TOKEN, response.refreshToken)
    }

    @Test
    @DisplayName("SIGN_UP_FAILURE_THROW_BY_NOT_VERIFIED_USER")
    fun SIGN_UP_FAILURE_THROW_BY_NOT_VERIFIED_USER() = runTest {

        coEvery {
            redisClient.getData(
                AuthRedisKey.IS_VERIFIED_USER.combineKeyValue(AuthTestConstant.EMAIL),
                String::class
            )
        } returns null

        coEvery {
            userRepository.findByEmail(AuthTestConstant.EMAIL)
        } returns null

        val request = AuthRequest.SignUpRequest(
            email = AuthTestConstant.EMAIL,
            password = AuthTestConstant.PASSWORD
        )

        val exception = assertThrows<BusinessException> { authService.signUp(request) }

        assertEquals(AuthErrorCode.NOT_VERIFIED_USER.code, exception.errorCode.getCodeValue())
        assertEquals(AuthErrorCode.NOT_VERIFIED_USER.message, exception.errorCode.getMessageValue())
        assertEquals(AuthErrorCode.NOT_VERIFIED_USER.status, exception.errorCode.getStatusValue())
    }

    @Test
    @DisplayName("SIGN_UP_FAILURE_THROW_BY_DUPLICATE_EMAIL")
    fun SIGN_UP_FAILURE_THROW_BY_DUPLICATE_EMAIL() = runTest {

        coEvery {
            redisClient.getData(
                AuthRedisKey.IS_VERIFIED_USER.combineKeyValue(AuthTestConstant.EMAIL),
                String::class
            )
        } returns AuthTestConstant.EMAIL

        coEvery {
            userRepository.findByEmail(AuthTestConstant.EMAIL)
        } returns user

        val request = AuthRequest.SignUpRequest(
            email = AuthTestConstant.EMAIL,
            password = AuthTestConstant.PASSWORD
        )

        val exception = assertThrows<BusinessException> { authService.signUp(request) }

        assertEquals(AuthErrorCode.DUPLICATE_EMAIL.code, exception.errorCode.getCodeValue())
        assertEquals(AuthErrorCode.DUPLICATE_EMAIL.message, exception.errorCode.getMessageValue())
        assertEquals(AuthErrorCode.DUPLICATE_EMAIL.status, exception.errorCode.getStatusValue())
    }

    @Test
    @DisplayName("LOGIN_SUCCESS")
    fun LOGIN_SUCCESS() = runTest {
        coEvery { userRepository.findByEmail(AuthTestConstant.EMAIL) } returns user
        coEvery { passwordEncoder.matches(AuthTestConstant.PASSWORD, user.password) } returns true

        coEvery {
            jwtService.generateAccessToken(AuthTestConstant.EMAIL)
        } returns Mono.just(JwtToken(value = AuthTestConstant.TEST_ACCESS_TOKEN))
        coEvery {
            jwtService.generateRefreshToken(AuthTestConstant.EMAIL)
        } returns Mono.just(JwtToken(value = AuthTestConstant.TEST_REFRESH_TOKEN))

        val request = AuthRequest.LoginRequest(
            email = AuthTestConstant.EMAIL,
            password = AuthTestConstant.PASSWORD
        )

        val response = authService.login(request)

        assertEquals(AuthTestConstant.TEST_ACCESS_TOKEN, response.accessToken)
        assertEquals(AuthTestConstant.TEST_REFRESH_TOKEN, response.refreshToken)
    }

    @Test
    @DisplayName("LOGIN_FAILURE_THROW_BY_USER_NOT_FOUND")
    fun LOGIN_FAILURE_THROW_BY_USER_NOT_FOUND() = runTest {
        coEvery { userRepository.findByEmail(AuthTestConstant.EMAIL) } returns null

        val request = AuthRequest.LoginRequest(
            email = AuthTestConstant.EMAIL,
            password = AuthTestConstant.PASSWORD
        )

        val exception = assertThrows<BusinessException> { authService.login(request) }

        assertEquals(UserErrorCode.USER_NOT_FOUND.code, exception.errorCode.getCodeValue())
        assertEquals(UserErrorCode.USER_NOT_FOUND.message, exception.errorCode.getMessageValue())
        assertEquals(UserErrorCode.USER_NOT_FOUND.status, exception.errorCode.getStatusValue())
    }

    @Test
    @DisplayName("LOGIN_FAILURE_THROW_BY_INVALID_PASSWORD")
    fun LOGIN_FAILURE_THROW_BY_INVALID_PASSWORD() = runTest {
        coEvery { userRepository.findByEmail(AuthTestConstant.EMAIL) } returns user
        coEvery { passwordEncoder.matches(AuthTestConstant.PASSWORD, user.password) } returns false

        val request = AuthRequest.LoginRequest(
            email = AuthTestConstant.EMAIL,
            password = AuthTestConstant.PASSWORD
        )

        val exception = assertThrows<BusinessException> { authService.login(request) }

        assertEquals(UserErrorCode.INVALID_PASSWORD.code, exception.errorCode.getCodeValue())
        assertEquals(UserErrorCode.INVALID_PASSWORD.message, exception.errorCode.getMessageValue())
        assertEquals(UserErrorCode.INVALID_PASSWORD.status, exception.errorCode.getStatusValue())
    }

    @Test
    @DisplayName("REFRESH_SUCCESS")
    fun REFRESH_SUCCESS() = runTest {
        val refreshToken = JwtToken(AuthTestConstant.TEST_REFRESH_TOKEN)
        coEvery { jwtService.validate(refreshToken) } returns Mono.empty()
        coEvery { jwtService.getEmail(refreshToken) } returns Mono.just(AuthTestConstant.EMAIL)

        coEvery {
            jwtService.generateAccessToken(AuthTestConstant.EMAIL)
        } returns Mono.just(JwtToken(value = AuthTestConstant.TEST_ACCESS_TOKEN + "_ACCESS"))
        coEvery {
            jwtService.generateRefreshToken(AuthTestConstant.EMAIL)
        } returns Mono.just(JwtToken(value = AuthTestConstant.TEST_REFRESH_TOKEN + "_REFRESH"))

        val request = AuthRequest.RefreshRequest(
            refreshToken = AuthTestConstant.TEST_REFRESH_TOKEN
        )

        val response = authService.refresh(request)

        assertEquals(AuthTestConstant.TEST_ACCESS_TOKEN + "_ACCESS", response.accessToken)
        assertEquals(AuthTestConstant.TEST_REFRESH_TOKEN + "_REFRESH", response.refreshToken)
    }
}