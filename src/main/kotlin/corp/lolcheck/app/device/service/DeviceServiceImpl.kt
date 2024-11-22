package corp.lolcheck.app.device.service

import corp.lolcheck.app.device.domain.Device
import corp.lolcheck.app.device.dto.DeviceRequest
import corp.lolcheck.app.device.dto.DeviceResponse
import corp.lolcheck.app.device.repsitory.DeviceRepository
import corp.lolcheck.app.device.service.interfaces.DeviceService
import corp.lolcheck.app.summoners.exception.DeviceErrorCode
import corp.lolcheck.common.exception.BusinessException
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeviceServiceImpl(
    private val deviceRepository: DeviceRepository
) : DeviceService {

    @Transactional
    override suspend fun createDevice(
        userId: Long,
        request: DeviceRequest.DeviceCreateRequest
    ): DeviceResponse.DeviceInfo = coroutineScope {
        val device: Device = Device(
            deviceToken = request.deviceToken,
            userId = userId,
        )

        val save = deviceRepository.save(device)

        DeviceResponse.DeviceInfo(
            deviceId = save.id!!,
            deviceToken = save.deviceToken,
            userId = save.userId
        )
    }

    override suspend fun getDevicesInfoByUserId(userId: Long): List<DeviceResponse.DeviceInfo> = coroutineScope {
        deviceRepository.findAllByUserId(userId).map {
            DeviceResponse.DeviceInfo(
                deviceId = it.id!!,
                deviceToken = it.deviceToken,
                userId = it.userId
            )
        }.toList()
    }

    override suspend fun getDeviceTokensByUserIds(userIds: List<Long>): List<String> = coroutineScope {
        deviceRepository.findAllByUserIdIn(userIds).map {
            it.deviceToken
        }.toList()
    }

    @Transactional
    override suspend fun updateDevice(
        userId: Long,
        deviceId: Long,
        request: DeviceRequest.DeviceUpdateRequest
    ): DeviceResponse.DeviceInfo = coroutineScope {
        val device: Device = deviceRepository.findByIdAndUserId(deviceId, userId)
            ?: throw BusinessException(DeviceErrorCode.DEVICE_NOT_FOUND)

        DeviceResponse.DeviceInfo(
            deviceId = device.id!!,
            deviceToken = device.deviceToken,
            userId = device.userId
        )
    }

    @Transactional
    override suspend fun deleteDevice(userId: Long, deviceId: Long) = coroutineScope {
        val device: Device = deviceRepository.findByIdAndUserId(deviceId, userId) ?: throw BusinessException(
            DeviceErrorCode.DEVICE_NOT_FOUND
        )

        deviceRepository.delete(device)
    }

    @Transactional
    override suspend fun deleteAllDevice(userId: Long) = coroutineScope {
        val devices = deviceRepository.findAllByUserId(userId).toList()
        deviceRepository.deleteAll(devices)
    }
}