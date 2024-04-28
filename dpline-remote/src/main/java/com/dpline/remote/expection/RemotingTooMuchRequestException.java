package com.dpline.remote.expection;

/**
 *  too much request exception
 */
public class RemotingTooMuchRequestException extends RemotingException {

    public RemotingTooMuchRequestException(String message) {
        super(message);
    }
}
