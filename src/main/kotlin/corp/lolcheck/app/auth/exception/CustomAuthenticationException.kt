package corp.lolcheck.app.auth.exception

import org.springframework.security.core.AuthenticationException

class CustomAuthenticationException(message: String?) : AuthenticationException(message) {
}