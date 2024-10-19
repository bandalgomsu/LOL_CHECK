package corp.lolcheck.app.summoners.exception

import corp.lolcheck.common.exception.ErrorCode
import org.springframework.http.HttpStatus

enum class SummonerErrorCode(val code: String, val message: String, var status: Int) : ErrorCode {
    SUMMONER_NOT_FOUND("S01", "SUMMONER_NOT_FOUND", HttpStatus.BAD_REQUEST.value()),

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



