package com.fyp.crms_backend.exception

import com.fyp.crms_backend.utils.ErrorCode

class ErrorCodeException(val errorCode: ErrorCode) : RuntimeException(errorCode.toString()) {

}
