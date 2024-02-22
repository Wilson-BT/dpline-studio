

package com.dpline.remote.util;

import com.dpline.common.Constants;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * NettyUtils
 */
public class NettyUtils {

    private NettyUtils() {
    }

    public static boolean useEpoll() {
        String osName = Constants.OS_NAME;
        if (!osName.toLowerCase().contains("linux")) {
            return false;
        }
        if (!Epoll.isAvailable()) {
            return false;
        }
        String enableNettyEpoll = Constants.NETTY_EPOLL_ENABLE;
        return Boolean.parseBoolean(enableNettyEpoll);
    }

    public static Class<? extends ServerSocketChannel> getServerSocketChannelClass() {
        if (useEpoll()) {
            return EpollServerSocketChannel.class;
        }
        return NioServerSocketChannel.class;
    }

    public static Class<? extends SocketChannel> getSocketChannelClass() {
        if (useEpoll()) {
            return EpollSocketChannel.class;
        }
        return NioSocketChannel.class;
    }

}
