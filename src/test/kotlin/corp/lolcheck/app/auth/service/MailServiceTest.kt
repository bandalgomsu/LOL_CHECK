package corp.lolcheck.app.auth.service

import AuthErrorCode
import corp.lolcheck.app.auth.AuthTestConstant
import corp.lolcheck.app.auth.data.AuthRedisKey
import corp.lolcheck.app.auth.dto.MailResponse
import corp.lolcheck.common.exception.BusinessException
import corp.lolcheck.infrastructure.redis.RedisClient
import io.mockk.coEvery
import io.mockk.mockk
import jakarta.mail.internet.MimeMessage
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.mail.javamail.JavaMailSender
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MailServiceTest {
    val mailResponse = MailResponse()

    private val mailSender: JavaMailSender = mockk<JavaMailSender>()
    private val redisClient: RedisClient = mockk<RedisClient>()

    private val message: MimeMessage = mockk(relaxed = true)

    private val mailService = MailService(mailSender, redisClient)

    @Test
    @DisplayName("SEND_SIGN_UP_VERIFYING_MAIL_SUCCESS")
    fun SEND_SIGN_UP_VERIFYING_MAIL_SUCCESS() = runTest {
        coEvery { mailSender.createMimeMessage() } returns message
        coEvery { redisClient.setData(any(), any(), any()) } returns true
        coEvery { mailSender.send(message) } returns Unit

        val response = mailService.sendSignUpVerifyingMail(AuthTestConstant.EMAIL)

        assertEquals("NUMBER", response.authNumber)
    }

    @Test
    @DisplayName("VERIFY_SIGN_UP_EMAIL_SUCCESS")
    fun VERIFY_SIGN_UP_EMAIL_SUCCESS() = runTest {
        val key = AuthRedisKey.SIGN_UP_VERIFYING_MAIL.combineKeyValue(AuthTestConstant.EMAIL)
        val successKey = AuthRedisKey.IS_VERIFIED_USER.combineKeyValue(AuthTestConstant.EMAIL)
        val authNumber = "TEST"

        coEvery { redisClient.getData(key, String::class) } returns authNumber

        coEvery { redisClient.deleteData(key) } returns true
        coEvery { redisClient.setData(successKey, AuthTestConstant.EMAIL, 180) } returns true

        val response = mailService.verifySignUpEmail(AuthTestConstant.EMAIL, authNumber)

        assertEquals(AuthTestConstant.EMAIL, response.email)
        assertTrue { response.isVerified }
    }

    @Test
    @DisplayName("VERIFY_SIGN_UP_EMAIL_FAILURE_THROW_BY_NOT_FOUND_AUTH_NUMBER")
    fun VERIFY_SIGN_UP_EMAIL_FAILURE_THROW_BY_NOT_FOUND_AUTH_NUMBER() = runTest {
        val key = AuthRedisKey.SIGN_UP_VERIFYING_MAIL.combineKeyValue(AuthTestConstant.EMAIL)
        val successKey = AuthRedisKey.IS_VERIFIED_USER.combineKeyValue(AuthTestConstant.EMAIL)
        val authNumber = "TEST"

        coEvery { redisClient.getData(key, String::class) } returns null

        val exception =
            assertThrows<BusinessException> { mailService.verifySignUpEmail(AuthTestConstant.EMAIL, authNumber) }

        assertEquals(AuthErrorCode.NOT_FOUND_AUTH_NUMBER.code, exception.errorCode.getCodeValue())
        assertEquals(AuthErrorCode.NOT_FOUND_AUTH_NUMBER.message, exception.errorCode.getMessageValue())
        assertEquals(AuthErrorCode.NOT_FOUND_AUTH_NUMBER.status, exception.errorCode.getStatusValue())
    }

    @Test
    @DisplayName("VERIFY_SIGN_UP_EMAIL_FAILURE_THROW_BY_NOT_MATCHED_AUTH_NUMBER")
    fun VERIFY_SIGN_UP_EMAIL_FAILURE_THROW_BY_NOT_MATCHED_AUTH_NUMBER() = runTest {
        val key = AuthRedisKey.SIGN_UP_VERIFYING_MAIL.combineKeyValue(AuthTestConstant.EMAIL)

        val authNumber = "TEST"
        val notMatchedAuthNumber = "NOT_MATCHED"

        coEvery { redisClient.getData(key, String::class) } returns authNumber

        val exception =
            assertThrows<BusinessException> {
                mailService.verifySignUpEmail(
                    AuthTestConstant.EMAIL,
                    notMatchedAuthNumber
                )
            }

        assertEquals(AuthErrorCode.NOT_MATCHED_AUTH_NUMBER.code, exception.errorCode.getCodeValue())
        assertEquals(AuthErrorCode.NOT_MATCHED_AUTH_NUMBER.message, exception.errorCode.getMessageValue())
        assertEquals(AuthErrorCode.NOT_MATCHED_AUTH_NUMBER.status, exception.errorCode.getStatusValue())
    }
}