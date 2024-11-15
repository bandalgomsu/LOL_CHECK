package corp.lolcheck.app.auth.exception

import AuthErrorCode
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import corp.lolcheck.common.exception.BusinessException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class CustomAccessDeniedHandler() : ServerAccessDeniedHandler {
    private val logger: Logger = LoggerFactory.getLogger(CustomAccessDeniedHandler::class.java)

    override fun handle(exchange: ServerWebExchange?, denied: AccessDeniedException?): Mono<Void> {
        val serverHttpResponse: ServerHttpResponse = exchange!!.response
        serverHttpResponse.headers.contentType = MediaType.APPLICATION_JSON
        serverHttpResponse.setStatusCode(HttpStatus.FORBIDDEN)

//        logger.error("[ERROR] : ACCESS_DENIED")

        return try {
            val errorByte = ObjectMapper()
                .writeValueAsBytes(BusinessException(AuthErrorCode.ACCESS_DENIED))
            val dataBuffer: DataBuffer = serverHttpResponse.bufferFactory().wrap(errorByte)
            serverHttpResponse.writeWith(Mono.just(dataBuffer))
        } catch (e: JsonProcessingException) {
            serverHttpResponse.setComplete()
        }
    }
}