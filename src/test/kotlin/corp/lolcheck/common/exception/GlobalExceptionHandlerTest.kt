package corp.lolcheck.common.exception

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GlobalExceptionHandlerTest {

    val globalExceptionHandler = GlobalExceptionHandler()

    @Test
    @DisplayName("HANDLE_BUSINESS_EXCEPTION_SUCCESS")
    fun HANDLE_BUSINESS_EXCEPTION_SUCCESS() = runTest {
        val businessException = BusinessException("TEST", CommonErrorCode.METHOD_NOT_ALLOWED)

        val response = globalExceptionHandler.handleBusinessException(businessException).block()!!

        assertEquals(businessException.errorCode.getStatusValue(), response.statusCode.value())
        assertEquals(businessException.errorCode.getCodeValue(), response.body!!.code)
        assertEquals(businessException.errorCode.getMessageValue(), response.body!!.message)
        assertEquals(businessException.errorCode.getStatusValue(), response.body!!.status)
    }

    @Test
    @DisplayName("HANDLE_EXCEPTION_SUCCESS")
    fun HANDLE_EXCEPTION_SUCCESS() = runTest {
        val exception = Exception()

        val response = globalExceptionHandler.handleException(exception).block()!!

        assertEquals(CommonErrorCode.INTERNAL_SERVER_ERROR.status, response.statusCode.value())
        assertEquals(CommonErrorCode.INTERNAL_SERVER_ERROR.code, response.body!!.code)
        assertEquals(CommonErrorCode.INTERNAL_SERVER_ERROR.message, response.body!!.message)
        assertEquals(CommonErrorCode.INTERNAL_SERVER_ERROR.status, response.body!!.status)
    }

}