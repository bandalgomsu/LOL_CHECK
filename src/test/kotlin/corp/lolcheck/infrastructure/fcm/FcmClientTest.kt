package corp.lolcheck.infrastructure.fcm

import com.google.api.core.ApiFuture
import com.google.firebase.messaging.BatchResponse
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource

@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = ["classpath:application-test.yml"])
@SpringBootTest
class FcmClientTest(
    @Autowired private val firebaseMessaging: FirebaseMessaging
) {

    private val fcmClient: FcmClient = FcmClient(firebaseMessaging)

    val data: FcmData.FcmMulticastData = FcmData.FcmMulticastData(
        title = "test",
        body = "test",
        tokens = mutableListOf("test")
    )

    @Test
    @DisplayName("FCM_푸시알림_전송_성공_테스트_(INVALID 토큰 에러)")
    fun FCM_푸시알림_전송_성공_테스트() = runTest {
        val response: ApiFuture<BatchResponse> = fcmClient.sendMulticastMessage(data, true)

        Assertions.assertEquals(1, response.get().failureCount)
    }
}