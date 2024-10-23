package corp.lolcheck.app.auth.service

import corp.lolcheck.app.auth.data.CustomUserDetails
import corp.lolcheck.app.users.repository.UserReactiveRepository
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class UserDetailService(
    private val userRepository: UserReactiveRepository,
) : ReactiveUserDetailsService {

    override fun findByUsername(username: String?): Mono<UserDetails> {
        if (username == null) {
            return Mono.empty()
        }

        return userRepository.findByEmail(email = username)
            .flatMap { Mono.just(CustomUserDetails(it)) }
    }
}