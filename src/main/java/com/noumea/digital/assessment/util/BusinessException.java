package com.noumea.digital.assessment.util;

public class BusinessException extends RuntimeException {

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
