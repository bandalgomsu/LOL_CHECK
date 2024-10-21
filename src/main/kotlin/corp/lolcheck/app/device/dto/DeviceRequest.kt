package corp.lolcheck.app.device.dto

class DeviceRequest {
    data class DeviceCreateRequest(
        val deviceToken: String,
    )

    data class DeviceUpdateRequest(
        val deviceToken: String,
    )
}