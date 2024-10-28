import corp.lolcheck.app.auth.exception.CustomAuthenticationException
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
        return jwtService.validate(token)
            .onErrorResume {
                Mono.error(CustomAuthenticationException(it.message))
            }
            .then(Mono.defer {
                jwtService.getEmail(token)
                    .flatMap { users.findByUsername(it) }
                    .map {
                        UsernamePasswordAuthenticationToken(
                            it,
                            null,
                            it.authorities
                        )
                    }
            })
    }
}