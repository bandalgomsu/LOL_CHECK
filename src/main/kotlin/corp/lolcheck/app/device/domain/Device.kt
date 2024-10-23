package corp.lolcheck.app.device.domain

import corp.lolcheck.common.entity.BaseEntity
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("device")
class Device(
    @Id
    var id: Long? = null,
    var deviceToken: String,
    var userId: Long
) : BaseEntity() {
}