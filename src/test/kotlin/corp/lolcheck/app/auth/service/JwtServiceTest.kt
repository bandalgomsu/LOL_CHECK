package corp.lolcheck.app.auth.service

import AuthErrorCode
import JwtToken
import corp.lolcheck.app.auth.AuthTestConstant
import corp.lolcheck.common.exception.BusinessException
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertNull

class JwtServiceTest {
    private val jwtService = JwtService(AuthTestConstant.TEST_KEY, 2L, 2L)

    @Test
    @DisplayName("GENERATE_ACCESS_TOKEN_SUCCESS")
    fun GENERATE_ACCESS_TOKEN_SUCCESS() = runTest {
        val accessToken = jwtService.generateAccessToken(AuthTestConstant.EMAIL).block()!!

        val email = jwtService.getEmail(accessToken).block()

        assertEquals(AuthTestConstant.EMAIL, email)
    }

    @Test
    @DisplayName("GENERATE_REFRESH_TOKEN_SUCCESS")
    fun GENERATE_REFRESH_TOKEN_SUCCESS() = runTest {
        val refreshToken = jwtService.generateRefreshToken(AuthTestConstant.EMAIL).block()!!

        val email = jwtService.getEmail(refreshToken).block()

        assertEquals(AuthTestConstant.EMAIL, email)
    }

    @Test
    @DisplayName("VALIDATE_SUCCESS")
    fun VALIDATE_SUCCESS() = runTest {
        val accessToken = jwtService.generateAccessToken(AuthTestConstant.EMAIL).block()!!

        val valid = jwtService.validate(accessToken).block()

        assertNull(valid)
    }

    @Test
    @DisplayName("VALIDATE_FAILURE_THORW_BY_EXPIRED_TOKEN")
    fun VALIDATE_FAILURE_THORW_BY_EXPIRED_TOKEN() = runTest {
        val accessToken = jwtService.generateAccessToken(AuthTestConstant.EMAIL).block()!!

        Thread.sleep(2500)

        val exception = assertThrows<BusinessException> { jwtService.validate(accessToken).block() }

        assertEquals(AuthErrorCode.EXPIRED_TOKEN.code, exception.errorCode.getCodeValue())
        assertEquals(AuthErrorCode.EXPIRED_TOKEN.message, exception.errorCode.getMessageValue())
        assertEquals(AuthErrorCode.EXPIRED_TOKEN.status, exception.errorCode.getStatusValue())
    }

    @Test
    @DisplayName("VALIDATE_FAILURE_THORW_BY_INVALID_TOKEN")
    fun VALIDATE_FAILURE_THORW_BY_INVALID_TOKEN() = runTest {
        val accessToken = JwtToken(value = "asdasdasdasdasdasdasdasdasd")

        val exception = assertThrows<BusinessException> { jwtService.validate(accessToken).block() }

        assertEquals(AuthErrorCode.INVALID_TOKEN.code, exception.errorCode.getCodeValue())
        assertEquals(AuthErrorCode.INVALID_TOKEN.message, exception.errorCode.getMessageValue())
        assertEquals(AuthErrorCode.INVALID_TOKEN.status, exception.errorCode.getStatusValue())
    }

    @Test
    @DisplayName("GET_EMAIL_SUCCESS")
    fun GET_EMAIL_SUCCESS() = runTest {
        val accessToken = jwtService.generateAccessToken(AuthTestConstant.EMAIL).block()!!

        val email = jwtService.getEmail(accessToken).block()

        assertEquals(AuthTestConstant.EMAIL, email)
    }
}