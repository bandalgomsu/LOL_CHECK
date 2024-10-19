package corp.lolcheck.common.exception

interface ErrorCode {

    fun getCodeValue(): String
    fun getStatusValue(): Int
    fun getMessageValue(): String
}