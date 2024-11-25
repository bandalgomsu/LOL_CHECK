package corp.lolcheck.app.auth.filter

import JwtServerAuthenticationConverter
import corp.lolcheck.app.auth.AuthTestConstant
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.mock.http.server.reactive.MockServerHttpRequest
import org.springframework.mock.web.server.MockServerWebExchange
import kotlin.test.assertEquals

class JwtServerAuthenticationConverterTest {

    private val jwtServerAuthenticationConverter = JwtServerAuthenticationConverter()

    @Test
    @DisplayName("CONVERT_SUCCESS")
    fun CONVERTER_SUCCESS() = runTest {
        val token = AuthTestConstant.TEST_ACCESS_TOKEN

        val request = MockServerHttpRequest.get("/")
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .build()
        val exchange = MockServerWebExchange.from(request)

        val response = jwtServerAuthenticationConverter.convert(exchange).block()!!

        assertEquals(token, response.credentials)
        assertEquals(token, response.principal)
    }
}