package corp.lolcheck.app.auth.dto

import jakarta.validation.constraints.Email

class MailRequest {

    data class SendSignUpVerifyingMailRequest(
        @Email
        val email: String
    )

    data class VerifySignUpMailRequest(
        @Email
        val email: String,
        val authNumber: String
    )
}