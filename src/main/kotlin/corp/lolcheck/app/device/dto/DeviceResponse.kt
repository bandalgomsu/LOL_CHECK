package corp.lolcheck.app.device.dto

class DeviceResponse {

    data class DeviceInfo(
        val deviceId: Long,
        val deviceToken: String,
        val userId: Long
    )
}