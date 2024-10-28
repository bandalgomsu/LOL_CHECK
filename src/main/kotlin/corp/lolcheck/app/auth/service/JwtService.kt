package corp.lolcheck.app.auth.service

import AuthErrorCode
import JwtToken
import corp.lolcheck.common.exception.BusinessException
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtParser
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
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
    private val accessKey: SecretKey = Keys.hmacShaKeyFor(keyValue.toByteArray())
    private val parser: JwtParser = Jwts.parser().verifyWith(accessKey).build()

    fun generateAccessToken(email: String): Mono<JwtToken> {
        return Mono.just(
            JwtToken(
                Jwts.builder()
                    .subject(email)
                    .issuedAt(Date.from(Instant.now()))
                    .expiration(Date.from(Instant.now().plus(accessExp, ChronoUnit.MINUTES)))
                    .signWith(accessKey)
                    .compact()
            )
        )
    }

    fun generateRefreshToken(email: String): Mono<JwtToken> {
        return Mono.just(
            JwtToken(
                Jwts.builder()
                    .subject(email)
                    .issuedAt(Date.from(Instant.now()))
                    .expiration(Date.from(Instant.now().plus(accessExp, ChronoUnit.MINUTES)))
                    .signWith(accessKey)
                    .compact()
            )
        )
    }

    fun validate(token: JwtToken): Mono<Void> {
        return getClaims(token)
            .flatMap { Mono.empty() }
    }

    private fun getClaims(token: JwtToken): Mono<Claims> {
        return Mono.defer {
            try {
                Mono.just(
                    parser
                        .parseSignedClaims(token.value)
                        .payload
                )
            } catch (e: Exception) {
                when (e) {
                    is ExpiredJwtException -> {
                        Mono.error(BusinessException(AuthErrorCode.EXPIRED_TOKEN))
                    }

                    else -> {
                        Mono.error(BusinessException(AuthErrorCode.EXPIRED_TOKEN))
                    }
                }
            }
        }
    }

    fun getEmail(token: JwtToken): Mono<String> {
        return Mono.just(
            parser
                .parseSignedClaims(token.value)
                .payload
                .subject
        )
    }
}