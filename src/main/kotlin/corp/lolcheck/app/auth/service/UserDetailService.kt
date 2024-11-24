package corp.lolcheck.app.auth.service

import UserErrorCode
import corp.lolcheck.app.auth.data.CustomUserDetails
import corp.lolcheck.app.users.repository.UserReactiveRepository
import corp.lolcheck.common.exception.BusinessException
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

@Service
class UserDetailService(
    private val userRepository: UserReactiveRepository,
) : ReactiveUserDetailsService {

    override fun findByUsername(username: String): Mono<UserDetails> {
        return userRepository.findByEmail(email = username)
            .switchIfEmpty { Mono.error(BusinessException(UserErrorCode.USER_NOT_FOUND)) }
            .flatMap { Mono.just(CustomUserDetails(it)) }
    }
}