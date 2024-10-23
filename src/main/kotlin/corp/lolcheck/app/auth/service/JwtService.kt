package corp.lolcheck.app.auth.service

import AuthErrorCode
import JwtToken
import corp.lolcheck.common.exception.BusinessException
import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*
import javax.crypto.SecretKey

@Service
class JwtService(
    @Value("\${jwt.key}")
    private val keyValue: String,

    @Value("\${jwt.exp.access}")
    private val accessExp: Long,

    @Value("\${jwt.exp.refresh}")
    private val refreshExp: Long,
) {
    private val key: SecretKey = Keys.hmacShaKeyFor(keyValue.toByteArray())
    private val parser: JwtParser = Jwts.parser().verifyWith(key).build()

    fun generateAccessToken(email: String): JwtToken {
        return JwtToken(
            Jwts.builder()
                .subject(email)
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plus(accessExp, ChronoUnit.MINUTES)))
                .signWith(key)
                .compact()
        )
    }

    fun validate(token: JwtToken): Boolean {
        try {
            getClaims(token)
            return true
        } catch (e: Exception) {
            when (e) {
                is UnsupportedJwtException -> {
                    throw BusinessException(AuthErrorCode.UNSUPPORTED_TOKEN)
                }

                is ExpiredJwtException -> {
                    throw BusinessException(AuthErrorCode.EXPIRED_TOKEN)
                }

                else -> {
                    throw BusinessException(AuthErrorCode.INVALID_TOKEN)
                }
            }
        }
    }

    private fun getClaims(token: JwtToken): Claims {
        return parser
            .parseSignedClaims(token.value)
            .payload
    }

    fun getEmail(token: JwtToken): String {
        return parser
            .parseSignedClaims(token.value)
            .payload
            .subject
    }
}