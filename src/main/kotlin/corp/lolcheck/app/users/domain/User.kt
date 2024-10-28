package corp.lolcheck.app.users.domain

import corp.lolcheck.app.users.type.Role
import corp.lolcheck.common.entity.BaseEntity
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("users")
class User(
    @Id
    var id: Long? = null,
    var email: String,
    var password: String,
    var role: Role
) : BaseEntity() {}