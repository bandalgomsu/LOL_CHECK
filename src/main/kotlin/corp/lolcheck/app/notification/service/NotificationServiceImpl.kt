package corp.lolcheck.app.notification.service

import corp.lolcheck.app.notification.dto.NotificationRequest
import corp.lolcheck.app.notification.service.interfaces.NotificationService
import corp.lolcheck.infrastructure.fcm.FcmClient
import kotlinx.coroutines.coroutineScope
import org.springframework.stereotype.Service

@Service
class NotificationServiceImpl(
    private val fcmClient: FcmClient,
) : NotificationService {

    override suspend fun sendMulticastMessage(request: NotificationRequest.SendMulticastRequest): Unit =
        coroutineScope {
            fcmClient.sendMulticastMessage(request.toFcmDate(), false)
        }
}