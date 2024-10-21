package corp.lolcheck.infrastructure.fcm

import com.google.api.core.ApiFuture
import com.google.firebase.messaging.BatchResponse
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.MulticastMessage
import com.google.firebase.messaging.Notification
import kotlinx.coroutines.coroutineScope
import org.springframework.stereotype.Component

@Component
class FcmClient(
    private val firebaseMessage: FirebaseMessaging
) {

    suspend fun sendMulticastMessage(data: FcmData.FcmMulticastData, isDryRun: Boolean): ApiFuture<BatchResponse> =
        coroutineScope {
            val message: MulticastMessage = createMulticastMessage(data.tokens, data.body, data.title)

            firebaseMessage.sendEachForMulticastAsync(message, isDryRun)
        }

    private suspend fun createMulticastMessage(
        tokens: MutableList<String>,
        body: String,
        title: String
    ): MulticastMessage =
        coroutineScope {
            val notification: Notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build()

            MulticastMessage
                .builder()
                .setNotification(notification)
                .addAllTokens(tokens)
                .build()
        }
}