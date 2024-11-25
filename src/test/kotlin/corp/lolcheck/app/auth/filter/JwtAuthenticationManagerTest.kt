package corp.lolcheck.app.auth.filter

import AuthErrorCode
import JwtAuthenticationManager
import JwtToken
import corp.lolcheck.app.auth.AuthTestConstant
import corp.lolcheck.app.auth.data.CustomUserDetails
import corp.lolcheck.app.auth.exception.CustomAuthenticationException
import corp.lolcheck.app.auth.service.JwtService
import corp.lolcheck.app.auth.service.UserDetailService
import corp.lolcheck.app.users.domain.User
import corp.lolcheck.app.users.type.Role
import corp.lolcheck.common.exception.BusinessException
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import reactor.core.publisher.Mono
import kotlin.test.assertTrue

class JwtAuthenticationManagerTest {

    private val jwtService = mockk<JwtService>()
    private val userDetailService = mockk<UserDetailService>()

    private val jwtAuthenticationManager = JwtAuthenticationManager(jwtService, userDetailService)

    val jwtToken = JwtToken(value = AuthTestConstant.TEST_ACCESS_TOKEN)

    val user = User(
        id = AuthTestConstant.USER_ID,
        email = AuthTestConstant.EMAIL,
        password = AuthTestConstant.PASSWORD,
        role = Role.USER
    )

    val userDetail = CustomUserDetails(user)

    @Test
    @DisplayName("AUTHENTICATE_SUCCESS")
    fun AUTHENTICATE_SUCCESS() = runTest {
        coEvery { jwtService.validate(jwtToken) } returns Mono.empty()

        coEvery { jwtService.getEmail(jwtToken) } returns Mono.just(AuthTestConstant.EMAIL)
        coEvery { userDetailService.findByUsername(AuthTestConstant.EMAIL) } returns Mono.just(userDetail)

        val response = jwtAuthenticationManager.authenticate(jwtToken).block()!!

        assertTrue { response.isAuthenticated }
    }

    @Test
    @DisplayName("AUTHENTICATE_FAILURE_THROW_BY_AUTHENTICATION_EXCEPTION")
    fun AUTHENTICATE_FAILURE_THROW_BY_AUTHENTICATION_EXCEPTION() = runTest {
        coEvery { jwtService.validate(jwtToken) } returns Mono.error { BusinessException(AuthErrorCode.EXPIRED_TOKEN) }

        val exception =
            assertThrows<CustomAuthenticationException> { jwtAuthenticationManager.authenticate(jwtToken).block()!! }
    }
}