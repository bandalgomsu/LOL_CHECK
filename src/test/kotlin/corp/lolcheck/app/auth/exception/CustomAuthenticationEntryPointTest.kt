package corp.lolcheck.app.auth.exception

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.mock.http.server.reactive.MockServerHttpRequest
import org.springframework.mock.web.server.MockServerWebExchange
import kotlin.test.assertEquals

class CustomAuthenticationEntryPointTest {
    private val objectMapper = ObjectMapper()
    private val customAuthenticationEntryPoint = CustomAuthenticationEntryPoint()

    @Test
    @DisplayName("COMMENCE_SUCCESS")
    fun COMMENCE_SUCCESS() {
        val request = MockServerHttpRequest.get("/")
            .header(HttpHeaders.AUTHORIZATION, "Bearer invalid-token")
            .build()

        val exchange = MockServerWebExchange.builder(request).build()
        val response = exchange.response

        val authenticationException = CustomAuthenticationException("AUTHENTICATE")

        customAuthenticationEntryPoint.commence(exchange, authenticationException).block()

        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
    }
}