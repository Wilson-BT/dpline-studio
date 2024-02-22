

package com.handsome.remote.expection;

/**
 *  remote exception
 */
public class RemotingException extends Exception {

    public RemotingException() {
        super();
    }

    /**
     * Construct a new runtime exception with the detail message
     *
     * @param   message  detail message
     */
    public RemotingException(String message) {
        super(message);
    }

    /**
     * Construct a new runtime exception with the detail message and cause
     *
     * @param  message the detail message
     * @param  cause the cause
     * @since  1.4
     */
    public RemotingException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Construct a new runtime exception with throwable
     *
     * @param  cause the cause
     */
    public RemotingException(Throwable cause) {
        super(cause);
    }


}
