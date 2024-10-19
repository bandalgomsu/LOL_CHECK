package corp.lolcheck.common.exception

open class BusinessException : RuntimeException {

    val errorCode: ErrorCode

    constructor(message: String, errorCode: ErrorCode) : super(message) {
        this.errorCode = errorCode
    }

    constructor(errorCode: ErrorCode) : super(errorCode.getMessageValue()) {
        this.errorCode = errorCode
    }
}
