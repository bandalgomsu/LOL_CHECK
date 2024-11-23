package corp.lolcheck.app.notification.dto

import corp.lolcheck.infrastructure.fcm.FcmData
import kotlinx.coroutines.coroutineScope

class NotificationRequest {
    data class SendMulticastRequest(
        val title: String,
        val body: String,
        val tokens: List<String>
    ) {
//        companion object {
//            suspend fun createPlayingGameMulticastRequest(
//                summoner: Summoner,
//                tokens: List<String>
//            ): SendMulticastRequest = coroutineScope {
//                SendMulticastRequest(
//                    title = summoner.gameName + " " + summoner.tagLine + "님이 게임을 시작했습니다",
//                    body = summoner.introduce ?: (summoner.gameName + " " + summoner.tagLine + "님이 게임을 시작했습니다"),
//                    tokens = tokens
//                )
//            }
//        }

        suspend fun toFcmDate(): FcmData.FcmMulticastData = coroutineScope {
            FcmData.FcmMulticastData(
                title = title,
                body = body,
                tokens = tokens
            )
        }
    }
}