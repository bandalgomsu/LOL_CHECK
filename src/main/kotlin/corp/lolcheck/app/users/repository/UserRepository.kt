package corp.lolcheck.app.users.repository

import corp.lolcheck.app.users.domain.User
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : ReactiveCrudRepository<User, Long> {
}