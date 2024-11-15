package corp.lolcheck.app.auth.service

import kotlinx.coroutines.coroutineScope
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service
import java.util.*

@Service
class MailService(
    private val mailSender: JavaMailSender
) {

    private suspend fun makeRandomNumber(): Int = coroutineScope {
        val r = Random()
        var randomNumber = ""

        for (i in 0..5) {
            randomNumber += r.nextInt(10).toString()
        }

        randomNumber.toInt()
    }

    suspend fun sendSignUpEmail(email: String) = coroutineScope {
        val authNumber = makeRandomNumber()

        val title = "LOL_CHECK 회원 가입 인증 이메일 입니다."
        val content = "Crewing에 방문해주셔서 감사합니다." +
                "<br><br>" +
                "회원가입 인증 번호는 " + authNumber + "입니다." +
                "<br>" +
                "인증번호를 제대로 입력해주세요"

        mailSend(to = email, title = title, content = content)
    }

    private fun mailSend(to: String, title: String, content: String) {
        val setFrom = "maildevgogo@gmail.com"

        redisUtil.deleteData(toMail)
        val message: MimeMessage = mailSender.createMimeMessage() //JavaMailSender 객체를 사용하여 MimeMessage 객체를 생성
        try {
            val helper = MimeMessageHelper(message, true, "utf-8") //이메일 메시지와 관련된 설정을 수행합니다.
            // true를 전달하여 multipart 형식의 메시지를 지원하고, "utf-8"을 전달하여 문자 인코딩을 설정
            helper.setFrom(setFrom) //이메일의 발신자 주소 설정
            helper.setTo(toMail) //이메일의 수신자 주소 설정
            helper.setSubject(title) //이메일의 제목을 설정
            helper.setText(content, true) //이메일의 내용 설정 두 번째 매개 변수에 true를 설정하여 html 설정으로한다.
            mailSender.send(message)
        } catch (e: MessagingException) { //이메일 서버에 연결할 수 없거나, 잘못된 이메일 주소를 사용하거나, 인증 오류가 발생하는 등 오류
            // 이러한 경우 MessagingException이 발생
            e.printStackTrace() //e.printStackTrace()는 예외를 기본 오류 스트림에 출력하는 메서드
        }
        redisUtil.setDataExpire(MailService.REDIS_KEY + toMail, authNumber.toString(), 60 * 5L)
    }

    fun verifySignUpEmail(email: String, inputAuthNum: String): Boolean {
        val redisKey: String = MailService.REDIS_KEY + email
        MailService.log.info("redis Start")
        val authCode: String = redisUtil.getData(redisKey, String::class.java)
        if (authCode == null) {
            return false
        } else if (authCode != inputAuthNum) {
            return false
        }
        redisUtil.deleteData(redisKey)
        return true
    }
}