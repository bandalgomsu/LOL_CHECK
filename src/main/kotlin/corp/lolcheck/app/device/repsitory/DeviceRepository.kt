package corp.lolcheck.app.device.repsitory

import corp.lolcheck.app.device.domain.Device
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface DeviceRepository : CoroutineCrudRepository<Device, Long> {
    suspend fun findAllByUserId(useId: Long): Flow<Device>
    suspend fun findAllByUserIdIn(userIds: MutableList<Long>): Flow<Device>
    suspend fun findByIdAndUserId(deviceId: Long, userId: Long): Device?
}