package corp.lolcheck.common.exception

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import reactor.core.publisher.Mono

@RestControllerAdvice
class GlobalExceptionHandler {
    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(BusinessException::class)
    protected fun handleBusinessException(
        exception: BusinessException,
    ): Mono<ResponseEntity<ErrorResponse>> {
        logger.error(exception.errorCode.getCodeValue(), exception)
        val errorCode = exception.errorCode
        val response = ErrorResponse(errorCode)
        return Mono.just(ResponseEntity(response, HttpStatus.valueOf(errorCode.getStatusValue())))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    protected fun handleMethodArgumentNotValidException(
        exception: MethodArgumentNotValidException?,
    ): Mono<ResponseEntity<ErrorResponse>> {
        logger.error("MethodArgumentTypeMismatchException", exception)
        val errorCode = CommonErrorCode.INVALID_INPUT_VALUE
        val response = ErrorResponse(errorCode)
        return Mono.just(ResponseEntity(response, HttpStatus.valueOf(errorCode.status)))
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    protected fun handleHttpMessageNotReadableException(
        exception: HttpMessageNotReadableException?,
    ): Mono<ResponseEntity<ErrorResponse>> {
        logger.error("HttpMessageNotReadableException", exception)
        val errorCode = CommonErrorCode.INVALID_INPUT_VALUE
        val response = ErrorResponse(errorCode)
        return Mono.just(ResponseEntity(response, HttpStatus.valueOf(errorCode.status)))
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleHttpMessageNotReadableException(
        exception: MethodArgumentTypeMismatchException
    ): Mono<ResponseEntity<ErrorResponse>> {
        logger.error("MethodArgumentTypeMismatchException", exception)
        val errorCode = CommonErrorCode.INVALID_INPUT_VALUE
        val response = ErrorResponse(errorCode)
        return Mono.just(ResponseEntity(response, HttpStatus.valueOf(errorCode.status)))
    }

    @ExceptionHandler(Exception::class)
    protected fun handleException(
        exception: Exception,
    ): Mono<ResponseEntity<ErrorResponse>> {
        logger.error("Exception", exception)
        val errorCode = CommonErrorCode.INTERNAL_SERVER_ERROR
        val response = ErrorResponse(errorCode)
        return Mono.just(ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR))
    }
}
