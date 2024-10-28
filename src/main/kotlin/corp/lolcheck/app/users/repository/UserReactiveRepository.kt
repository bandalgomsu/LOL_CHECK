package corp.lolcheck.app.users.repository

import corp.lolcheck.app.users.domain.User
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Mono

interface UserReactiveRepository : ReactiveCrudRepository<User, Long> {
    fun findByEmail(email: String): Mono<User>
}