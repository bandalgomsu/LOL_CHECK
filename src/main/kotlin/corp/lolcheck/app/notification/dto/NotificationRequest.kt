package corp.lolcheck.app.notification.dto

import corp.lolcheck.infrastructure.fcm.FcmData

class NotificationRequest {
    data class SendMulticastRequest(
        val title: String,
        val body: String,
        val tokens: MutableList<String>
    ) {
        fun toFcmDate(): FcmData.FcmMulticastData {
            return FcmData.FcmMulticastData(
                title = this.title,
                body = this.body,
                tokens = this.tokens
            )
        }
    }
}