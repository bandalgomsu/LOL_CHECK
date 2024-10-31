package corp.lolcheck.infrastructure.riot

import corp.lolcheck.common.exception.ErrorCode
import org.springframework.http.HttpStatus

enum class RiotClientErrorCode(val code: String, val message: String, var status: Int) : ErrorCode {
    SUMMONER_NOT_FOUND("RC01", "SUMMONER_NOT_FOUND", HttpStatus.BAD_REQUEST.value()),

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