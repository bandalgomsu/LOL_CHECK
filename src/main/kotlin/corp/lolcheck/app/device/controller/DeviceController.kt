package corp.lolcheck.app.device.controller

import corp.lolcheck.app.auth.data.CustomUserDetails
import corp.lolcheck.app.device.dto.DeviceRequest
import corp.lolcheck.app.device.dto.DeviceResponse
import corp.lolcheck.app.device.service.interfaces.DeviceService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.coroutineScope
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*


@Tag(name = "디바이스", description = "디바이스 정보를 관리 합니다")
@RestController
class DeviceController(
    private val deviceService: DeviceService
) {

    @Operation(summary = "디바이스 생성", description = "디바이스를 생성합니다")
    @PostMapping("/api/v1/devices")
    suspend fun createDevice(
        @AuthenticationPrincipal principal: CustomUserDetails,
        @RequestBody request: DeviceRequest.DeviceCreateRequest
    ): DeviceResponse.DeviceInfo = coroutineScope {
        deviceService.createDevice(principal.getId(), request)
    }

    @Operation(summary = "디바이스 수정", description = "디바이스를 수정합니다")
    @PostMapping("/api/v1/devices/{deviceId}")
    suspend fun updateDevice(
        @AuthenticationPrincipal principal: CustomUserDetails,
        @PathVariable deviceId: Long,
        @RequestBody request: DeviceRequest.DeviceUpdateRequest
    ): DeviceResponse.DeviceInfo = coroutineScope {
        deviceService.updateDevice(principal.getId(), deviceId, request)
    }

    @Operation(summary = "모든 디바이스 삭제", description = "모든 디바이스를 삭제합니다")
    @DeleteMapping("/api/v1/devices")
    suspend fun deleteAllDevice(
        @AuthenticationPrincipal principal: CustomUserDetails,
    ) = coroutineScope {
        deviceService.deleteAllDevice(principal.getId())
    }
}