package corp.lolcheck.app.auth.dto

class AuthRequest {
    data class SignUpRequest(
        val email: String,
        val password: String
    )
}