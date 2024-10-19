package corp.lolcheck.app.subcribe.exception

import corp.lolcheck.common.exception.ErrorCode
import org.springframework.http.HttpStatus

enum class SummonerSubscriberErrorCode(val code: String, val message: String, var status: Int) : ErrorCode {
    SUMMONER_SUBSCRIBER_NOT_FOUND("SS01", "SUMMONER_SUBSCRIBER_NOT_FOUND", HttpStatus.BAD_REQUEST.value()),
    DUPLICATE_SUMMONER_SUBSCRIBER("SS02", "DUPLICATE_SUMMONER_SUBSCRIBER", HttpStatus.BAD_REQUEST.value()),
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



