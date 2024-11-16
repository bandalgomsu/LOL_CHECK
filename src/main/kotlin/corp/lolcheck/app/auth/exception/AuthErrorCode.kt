import corp.lolcheck.common.exception.ErrorCode
import org.springframework.http.HttpStatus

enum class AuthErrorCode(val code: String, val message: String, var status: Int) : ErrorCode {

    UNSUPPORTED_TOKEN("A01", "UNSUPPORTED_TOKEN", HttpStatus.UNAUTHORIZED.value()),
    EXPIRED_TOKEN("A02", "EXPIRED_TOKEN", HttpStatus.UNAUTHORIZED.value()),
    INVALID_TOKEN("A03", "INVALID_TOKEN", HttpStatus.UNAUTHORIZED.value()),
    ACCESS_DENIED("A04", "INVALID_TOKEN", HttpStatus.FORBIDDEN.value()),
    UNAUTHORIZED("A05", "INVALID_TOKEN", HttpStatus.UNAUTHORIZED.value()),
    NOT_FOUND_AUTH_NUMBER("A06", "NOT_FOUND_AUTH_NUMBER", HttpStatus.UNAUTHORIZED.value()),
    NOT_MATCHED_AUTH_NUMBER("A07", "NOT_MATCHED_AUTH_NUMBER", HttpStatus.UNAUTHORIZED.value()),
    NOT_VERIFIED_USER("A08", "NOT_VERIFIED_USER", HttpStatus.UNAUTHORIZED.value()),
    DUPLICATE_EMAIL("A09", "DUPLICATE_EMAIL", HttpStatus.UNAUTHORIZED.value()),
    ;

    override fun getCodeValue(): String {
        return this.code
    }

    override fun getStatusValue(): Int {
        return this.status
    }

    override fun getMessageValue(): String {
        return this.message
    }
}