package com.backend.allreva.common.storage.exception;

import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.common.exception.code.GlobalErrorCode;

public class UploadFailedException extends CustomException {

    public UploadFailedException() {
        super(GlobalErrorCode.UPLOAD_FAILED);
    }

    public UploadFailedException(String message) {
        super(GlobalErrorCode.UPLOAD_FAILED, message);
    }
}
