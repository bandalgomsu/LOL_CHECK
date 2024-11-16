package corp.lolcheck.app.auth.data

enum class AuthRedisKey(val value: String) {
    SIGN_UP_VERIFYING_MAIL("SIGN_UP_VERIFYING_MAIL"),
    IS_VERIFIED_USER("IS_VERIFIED_USER"),
}