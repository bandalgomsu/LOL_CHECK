package corp.lolcheck.app.notification

import corp.lolcheck.app.notification.dto.NotificationRequest
import corp.lolcheck.app.notification.service.NotificationServiceImpl
import corp.lolcheck.infrastructure.fcm.FcmClient
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test


class NotificationServiceTest {
    val notificationData = NotificationRequest()

    private var fcmClient: FcmClient = mockk();

    private var notificationServiceImpl: NotificationServiceImpl = NotificationServiceImpl(fcmClient)

    @Test
    @DisplayName("SEND_MULTICAST_MESSAGE_SUCCESS")
    fun SEND_MULTICAST_MESSAGE_SUCCESS() = runTest {
        val request = NotificationRequest.SendMulticastRequest("TEST", "TEST", listOf("TEST"))

        coEvery { fcmClient.sendMulticastMessage(any(), any()) } returns mockk()

        notificationServiceImpl.sendMulticastMessage(request)

        coVerify(exactly = 1) { fcmClient.sendMulticastMessage(any(), any()) }
    }
}