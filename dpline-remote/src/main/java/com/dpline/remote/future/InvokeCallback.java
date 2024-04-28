
package com.dpline.remote.future;

/**
 * invoke callback
 */
public interface InvokeCallback {

    /**
     *  operation
     *
     * @param responseFuture responseFuture
     */
    void operationComplete(final ResponseFuture responseFuture);

}
