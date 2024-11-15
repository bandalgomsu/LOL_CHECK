package corp.lolcheck.common.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl
import java.util.*

@Configuration
class MailConfig(
    @Value("\${spring.mail.username}")
    private val id: String,
    @Value("\${spring.mail.password}")
    private val password: String,
    @Value("\${spring.mail.host}")
    private val host: String,
    @Value("\${spring.mail.port}")
    private val port: Int,
) {

    @Bean
    fun javaMailSender(): JavaMailSender {
        val properties = Properties()
        properties.setProperty("mail.transport.protocol", "smtp") // 프로토콜 설정

        properties.setProperty("mail.smtp.auth", "true") // smtp 인증

        properties.setProperty("mail.smtp.starttls.enable", "true") // smtp starttls 사용


        val javaMailSender = JavaMailSenderImpl()

        javaMailSender.host = host // smtp 서버 주소
        javaMailSender.username = id // 설정(발신) 메일 아이디
        javaMailSender.password = password // 설정(발신) 메일 패스워드
        javaMailSender.port = port //smtp port
        javaMailSender.setJavaMailProperties(properties) // 메일 인증서버 정보 가져온다.
        javaMailSender.defaultEncoding = "UTF-8"

        return javaMailSender
    }


}
