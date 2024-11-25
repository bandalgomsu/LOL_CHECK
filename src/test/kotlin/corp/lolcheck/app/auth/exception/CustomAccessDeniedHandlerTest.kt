package corp.lolcheck.app.auth.exception

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.mock.http.server.reactive.MockServerHttpRequest
import org.springframework.mock.web.server.MockServerWebExchange
import kotlin.test.assertEquals

class CustomAccessDeniedHandlerTest {

    private val objectMapper = ObjectMapper()
    private val customAccessDeniedHandler = CustomAccessDeniedHandler()

    @Test
    @DisplayName("HANDLE_SUCCESS")
    fun HANDLE_SUCCESS() {
        val request = MockServerHttpRequest.get("/")
            .header(HttpHeaders.AUTHORIZATION, "Bearer invalid-token")
            .build()

        val exchange = MockServerWebExchange.builder(request).build()
        val response = exchange.response

        val accessDeniedException = org.springframework.security.access.AccessDeniedException("ACCESS_DENIED")

        customAccessDeniedHandler.handle(exchange, accessDeniedException).block()

        assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
    }
}