
package com.handsome.remote.handle;

import com.handsome.remote.command.Command;
import io.netty.channel.*;

/**
 *  netty request processor
 */
public interface NettyRequestProcessor {

    /**
     *  process logic
     * @param channel channel
     * @param command command
     */
    void process(final Channel channel, final Command command) throws Exception;

}
