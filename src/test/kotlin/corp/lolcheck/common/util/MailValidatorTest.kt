package corp.lolcheck.common.util

import corp.lolcheck.common.exception.BusinessException
import corp.lolcheck.common.exception.CommonErrorCode
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.assertEquals

class MailValidatorTest {
    val mailValidator = MailValidator()

    @Test
    @DisplayName("VALIDATE_EMAIL_SUCCESS")
    fun VALIDATE_EMAIL_SUCCESS() = runTest {
        val validEmail = "asd@naver.com"

        assertDoesNotThrow { MailValidator.validateEmail(validEmail) }
    }

    @ParameterizedTest
    @DisplayName("VALIDATE_EMAIL_FAILUNRE_THROW_BY_INVALID_EMAIL")
    @ValueSource(strings = ["asd", "asd@", "asd@asd."])
    fun VALIDATE_EMAIL_FAILUNRE_THROW_BY_INVALID_EMAIL(invalidEmail: String) = runTest {
        val exception = assertThrows<BusinessException> { MailValidator.validateEmail(invalidEmail) }

        assertEquals(CommonErrorCode.INVALID_EMAIL.code, exception.errorCode.getCodeValue())
        assertEquals(CommonErrorCode.INVALID_EMAIL.message, exception.errorCode.getMessageValue())
        assertEquals(CommonErrorCode.INVALID_EMAIL.status, exception.errorCode.getStatusValue())
    }
}