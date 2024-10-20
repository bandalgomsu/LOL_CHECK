package corp.lolcheck.common.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.common.util.concurrent.ListeningExecutorService
import com.google.common.util.concurrent.MoreExecutors
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import java.io.IOException

@Configuration
class FcmConfig(
    @Value("\${fcm.key.path}") private val fcmKeyPath: String,
) {

    @Bean
    fun firebaseMessaging(): FirebaseMessaging {
        try {
            val options =
                FirebaseOptions.builder()
                    .setCredentials(
                        GoogleCredentials.fromStream(ClassPathResource(fcmKeyPath).inputStream),
                    )
                    .build()
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options)
            }
        } catch (e: IOException) {
            throw RuntimeException(e.message)
        }

        return FirebaseMessaging.getInstance()
    }

    @Bean
    fun firebaseAppExecutor(): ListeningExecutorService {
        return MoreExecutors.newDirectExecutorService()
    }
}
