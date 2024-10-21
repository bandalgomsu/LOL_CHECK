package corp.lolcheck.app.notification.service.interfaces

import corp.lolcheck.app.notification.dto.NotificationRequest

interface NotificationService {
    suspend fun sendMulticastMessage(request: NotificationRequest.SendMulticastRequest)
}