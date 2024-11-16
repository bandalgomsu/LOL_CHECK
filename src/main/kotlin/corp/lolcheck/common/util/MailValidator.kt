package corp.lolcheck.common.util

import corp.lolcheck.common.exception.BusinessException
import corp.lolcheck.common.exception.CommonErrorCode
import org.apache.commons.validator.routines.EmailValidator
import org.springframework.stereotype.Component

@Component
class MailValidator(
) {

    companion object {
        fun validateEmail(email: String) {
            if (!EmailValidator.getInstance().isValid(email)) {
                throw BusinessException(CommonErrorCode.INVALID_EMAIL)
            }
        }
    }
}