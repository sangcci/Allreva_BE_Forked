package com.backend.allreva.common.storage.exception;

import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.common.exception.code.GlobalErrorCode;

public class InvalidUrlException extends CustomException {

    public InvalidUrlException() {
        super(GlobalErrorCode.INVALID_URL);
    }

    public InvalidUrlException(String message) {
        super(GlobalErrorCode.INVALID_URL, message);
    }
}
