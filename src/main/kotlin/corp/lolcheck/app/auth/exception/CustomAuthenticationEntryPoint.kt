package corp.lolcheck.app.auth.exception

import AuthErrorCode
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import corp.lolcheck.common.exception.BusinessException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.server.ServerAuthenticationEntryPoint
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.nio.charset.StandardCharsets


class CustomAuthenticationEntryPoint() : ServerAuthenticationEntryPoint {

    private val logger: Logger = LoggerFactory.getLogger(CustomAuthenticationEntryPoint::class.java)

    override fun commence(exchange: ServerWebExchange?, ex: AuthenticationException?): Mono<Void> {

        val serverHttpResponse: ServerHttpResponse = exchange!!.response
        serverHttpResponse.headers.contentType = MediaType.APPLICATION_JSON
        serverHttpResponse.setStatusCode(HttpStatus.UNAUTHORIZED)

        logger.error("[ERROR] : UNAUTHORIZED")
        
        return try {
            val body: String = ObjectMapper()
                .writeValueAsString(BusinessException(AuthErrorCode.UNAUTHORIZED))

            val bytes: ByteArray = body.toByteArray(StandardCharsets.UTF_8)
            val wrap = serverHttpResponse.bufferFactory().wrap(bytes)

            serverHttpResponse.writeWith(Mono.just(wrap))
        } catch (e: JsonProcessingException) {
            serverHttpResponse.setComplete()
        }
    }
}