package corp.lolcheck.common.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.AwsCredentials
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.ses.SesClient

@Configuration
class AwsConfig(
    @Value("\${aws.access-key}") private val accessKey: String,
    @Value("\${aws.secret-key}") private val secretKey: String,
    @Value("\${aws.region}") private val region: String,
) {

    @Bean
    fun sesClient(): SesClient {
        val credential: AwsCredentials = AwsBasicCredentials
            .create(accessKey, secretKey)

        val provider: AwsCredentialsProvider = StaticCredentialsProvider.create(credential)
        
        return SesClient.builder()
            .region(Region.AP_NORTHEAST_2)
            .credentialsProvider(provider)
            .build()
    }
}