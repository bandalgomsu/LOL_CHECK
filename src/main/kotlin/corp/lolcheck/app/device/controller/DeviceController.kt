package corp.lolcheck.app.device.controller

import corp.lolcheck.app.device.dto.DeviceRequest
import corp.lolcheck.app.device.dto.DeviceResponse
import corp.lolcheck.app.device.service.interfaces.DeviceService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.coroutineScope
import org.springframework.web.bind.annotation.*


@Tag(name = "디바이스", description = "디바이스 정보를 관리 합니다")
@RestController
class DeviceController(
    private val deviceService: DeviceService
) {

    @Operation(summary = "디바이스 생성", description = "디바이스를 생성합니다")
    @PostMapping("/api/v1/devices")
    suspend fun createDevice(
        @RequestParam userId: Long,
        @RequestBody request: DeviceRequest.DeviceCreateRequest
    ): DeviceResponse.DeviceInfo = coroutineScope {
        deviceService.createDevice(userId, request)
    }

    @Operation(summary = "디바이스 수정", description = "디바이스를 수정합니다")
    @PostMapping("/api/v1/devices/{deviceId}")
    suspend fun updateDevice(
        @RequestParam userId: Long,
        @PathVariable deviceId: Long,
        @RequestBody request: DeviceRequest.DeviceUpdateRequest
    ): DeviceResponse.DeviceInfo = coroutineScope {
        deviceService.updateDevice(userId, deviceId, request)
    }
}