

package com.dpline.alert.plugin.email.exception;

public class AlertEmailException extends RuntimeException {
    public AlertEmailException(String errMsg) {
        super(errMsg);
    }

    public AlertEmailException(String errMsg, Throwable cause) {
        super(errMsg, cause);
    }
}
