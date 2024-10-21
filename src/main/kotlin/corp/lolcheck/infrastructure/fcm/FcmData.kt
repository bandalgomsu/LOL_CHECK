package corp.lolcheck.infrastructure.fcm

class FcmData {

    data class FcmMulticastData(
        val title: String,
        val body: String,
        val tokens: MutableList<String>
    )
}