package corp.lolcheck.app.auth.service

import UserErrorCode
import corp.lolcheck.app.auth.AuthTestConstant
import corp.lolcheck.app.users.domain.User
import corp.lolcheck.app.users.repository.UserReactiveRepository
import corp.lolcheck.app.users.type.Role
import corp.lolcheck.common.exception.BusinessException
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.security.core.authority.SimpleGrantedAuthority
import reactor.core.publisher.Mono
import kotlin.test.assertEquals

class UserDetailServiceTest {
    private val userRepository = mockk<UserReactiveRepository>()

    private val userDetailService = UserDetailService(userRepository)

    @Test
    @DisplayName("FIND_BY_USER_NAME_SUCCESS")
    fun FIND_BY_USER_NAME_SUCCESS() = runTest {

        every {
            userRepository.findByEmail(AuthTestConstant.EMAIL)
        } returns Mono.just(
            User(
                id = AuthTestConstant.USER_ID,
                email = AuthTestConstant.EMAIL,
                password = AuthTestConstant.PASSWORD,
                role = Role.USER
            )
        )

        val response = userDetailService.findByUsername(AuthTestConstant.EMAIL).block()!!

        assertEquals(AuthTestConstant.EMAIL, response.username)
        assertEquals(AuthTestConstant.PASSWORD, response.password)
        assertEquals(SimpleGrantedAuthority(Role.USER.toString()), response.authorities.toList()[0])
    }

    @Test
    @DisplayName("FIND_BY_USER_NAME_FALIURE_THROW_BY_USER_NOT_FOUND")
    fun FIND_BY_USER_NAME_FALIURE_THROW_BY_USER_NOT_FOUND() = runTest {

        every {
            userRepository.findByEmail(AuthTestConstant.EMAIL)
        } returns Mono.empty()

        val exception =
            assertThrows<BusinessException> { userDetailService.findByUsername(AuthTestConstant.EMAIL).block()!! }

        assertEquals(UserErrorCode.USER_NOT_FOUND.code, exception.errorCode.getCodeValue())
        assertEquals(UserErrorCode.USER_NOT_FOUND.message, exception.errorCode.getMessageValue())
        assertEquals(UserErrorCode.USER_NOT_FOUND.status, exception.errorCode.getStatusValue())
    }
}