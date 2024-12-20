package corp.lolcheck.app.auth.service

import AuthErrorCode
import corp.lolcheck.app.auth.data.AuthRedisKey
import corp.lolcheck.app.auth.dto.MailResponse
import corp.lolcheck.common.exception.BusinessException
import corp.lolcheck.common.util.MailValidator
import corp.lolcheck.infrastructure.redis.RedisClient
import jakarta.mail.internet.MimeMessage
import kotlinx.coroutines.*
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import java.util.*

@Service
class MailService(
    private val mailSender: JavaMailSender,
    private val redisClient: RedisClient,
) {

    suspend fun sendSignUpVerifyingMail(email: String): MailResponse.SendSignUpVerifyingMailResponse = coroutineScope {
        MailValidator.validateEmail(email)

        val authNumber = makeRandomNumber()

        val title = "LOL_CHECK 회원 가입 인증 이메일 입니다."
        val content = "LOL_CHECK에 방문해주셔서 감사합니다." +
                "<br><br>" +
                "회원가입 인증 번호는 " + authNumber + "입니다." +
                "<br>" +
                "인증번호를 제대로 입력해주세요"

        mailSend(to = email, title = title, content = content, authNumber = authNumber)

        MailResponse.SendSignUpVerifyingMailResponse("NUMBER")
    }

    private suspend fun makeRandomNumber(): String = coroutineScope {
        val r = Random()
        var randomNumber = ""

        for (i in 0..5) {
            randomNumber += r.nextInt(10).toString()
        }

        randomNumber
    }


    private suspend fun mailSend(to: String, title: String, content: String, authNumber: String): String =
        coroutineScope {
            val from = "maildevgogo@gmail.com"

            val message: MimeMessage = mailSender.createMimeMessage()
            val helper = MimeMessageHelper(message, true, "utf-8")

            helper.setFrom(from)
            helper.setTo(to)
            helper.setSubject(title)
            helper.setText(content, true)
            CoroutineScope(Dispatchers.IO + Job()).launch { mailSender.send(message) }

            redisClient.setData(AuthRedisKey.SIGN_UP_VERIFYING_MAIL.combineKeyValue(to), authNumber, 180)

            authNumber
        }

    suspend fun verifySignUpEmail(email: String, inputAuthNumber: String): MailResponse.VerifySignUpMailResponse =
        coroutineScope {
            MailValidator.validateEmail(email)

            val redisKey: String = AuthRedisKey.SIGN_UP_VERIFYING_MAIL.combineKeyValue(email)

            val authNumber: String =
                redisClient.getData(redisKey, String::class)
                    ?: throw BusinessException(AuthErrorCode.NOT_FOUND_AUTH_NUMBER)

            val isVerified = authNumber == inputAuthNumber

            if (!isVerified) {
                throw BusinessException(AuthErrorCode.NOT_MATCHED_AUTH_NUMBER)
            }

            listOf(
                async { redisClient.deleteData(redisKey) },
                async { redisClient.setData(AuthRedisKey.IS_VERIFIED_USER.combineKeyValue(email), email, 180) }
            ).awaitAll()

            MailResponse.VerifySignUpMailResponse(
                email = email,
                isVerified = isVerified
            )
        }
}