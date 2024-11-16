package corp.lolcheck.app.auth.dto

class MailResponse {

    data class SendSignUpVerifyingMailResponse(
        val authNumber: String
    )

    data class VerifySignUpMailResponse(
        val email: String,
        val isVerified: Boolean,
    )
}