import corp.lolcheck.app.auth.service.JwtService
import corp.lolcheck.app.auth.service.UserDetailService
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class JwtAuthenticationManager(
    private val jwtService: JwtService,
    private val users: UserDetailService
) : ReactiveAuthenticationManager {

    override fun authenticate(authentication: Authentication?): Mono<Authentication> {
        return Mono.justOrEmpty(authentication)
            .filter { auth -> auth is JwtToken }
            .cast(JwtToken::class.java)
            .flatMap { validate(it) }
    }

    private fun validate(token: JwtToken): Mono<Authentication> {
        jwtService.validate(token)
        val email = jwtService.getEmail(token)

        return users.findByUsername(email)
            .flatMap {
                Mono.just(
                    UsernamePasswordAuthenticationToken(
                        it,
                        null,
                        it.authorities
                    )
                )
            }
    }
}