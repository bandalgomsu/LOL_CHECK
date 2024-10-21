package corp.lolcheck.app.device.service.interfaces

import corp.lolcheck.app.device.dto.DeviceRequest
import corp.lolcheck.app.device.dto.DeviceResponse

interface DeviceService {
    suspend fun createDevice(userId: Long, request: DeviceRequest.DeviceCreateRequest): DeviceResponse.DeviceInfo
    suspend fun getDevicesInfoByUserId(userId: Long): List<DeviceResponse.DeviceInfo>
    suspend fun getDeviceTokensByUserIds(userIds: MutableList<Long>): List<String>
    suspend fun updateDevice(
        userId: Long,
        deviceId: Long,
        request: DeviceRequest.DeviceUpdateRequest
    ): DeviceResponse.DeviceInfo

    suspend fun deleteDevice(userId: Long, deviceId: Long): Unit
}