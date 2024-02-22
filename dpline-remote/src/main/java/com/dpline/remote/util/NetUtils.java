
package com.dpline.remote.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.*;
import java.util.*;
import java.util.regex.Pattern;

/**
 * NetUtils
 */
public class NetUtils {

    private static final Pattern IP_PATTERN = Pattern.compile("\\d{1,3}(\\.\\d{1,3}){3,5}$");

    private static final Logger logger = LoggerFactory.getLogger(NetUtils.class);

    private NetUtils() {
        throw new UnsupportedOperationException("Construct NetUtils");
    }

    /**
     * get addr like host:port
     * @return addr
     */
    public static String getAddr(String host, int port) {
        return String.format("%s:%d", host, port);
    }


    /**
     * get host
     * @return host
     */
    public static String getHost(InetAddress inetAddress) {
        if (inetAddress != null) {
            return inetAddress.getHostAddress();
        }
        return null;
    }



    private static Optional<InetAddress> toValidAddress(InetAddress address) {
        if (address instanceof Inet6Address) {
            Inet6Address v6Address = (Inet6Address) address;
            if (isPreferIPV6Address()) {
                return Optional.ofNullable(normalizeV6Address(v6Address));
            }
        }
        if (isValidV4Address(address)) {
            return Optional.of(address);
        }
        return Optional.empty();
    }

    private static InetAddress normalizeV6Address(Inet6Address address) {
        String addr = address.getHostAddress();
        int i = addr.lastIndexOf('%');
        if (i > 0) {
            try {
                return InetAddress.getByName(addr.substring(0, i) + '%' + address.getScopeId());
            } catch (UnknownHostException e) {
                logger.debug("Unknown IPV6 address: ", e);
            }
        }
        return address;
    }

    public static boolean isValidV4Address(InetAddress address) {

        if (address == null || address.isLoopbackAddress()) {
            return false;
        }
        String name = address.getHostAddress();
        return (name != null
                && IP_PATTERN.matcher(name).matches()
                && !address.isAnyLocalAddress()
                && !address.isLoopbackAddress());
    }

    /**
     * Check if an ipv6 address
     *
     * @return true if it is reachable
     */
    private static boolean isPreferIPV6Address() {
        return Boolean.getBoolean("java.net.preferIPv6Addresses");
    }



    /**
     * @param networkInterface {@link NetworkInterface}
     * @return if the specified {@link NetworkInterface} should be ignored, return <code>true</code>
     * @throws SocketException SocketException if an I/O error occurs.
     */
    public static boolean ignoreNetworkInterface(NetworkInterface networkInterface) throws SocketException {
        return networkInterface == null
                || networkInterface.isLoopback()
                || networkInterface.isVirtual()
                || !networkInterface.isUp();
    }



    private static NetworkInterface findAddressByDefaultPolicy(List<NetworkInterface> validNetworkInterfaces) {
        NetworkInterface networkInterface;
        networkInterface = findInnerAddress(validNetworkInterfaces);
        if (networkInterface == null) {
            networkInterface = findOuterAddress(validNetworkInterfaces);
            if (networkInterface == null) {
                networkInterface = validNetworkInterfaces.get(0);
            }
        }
        return networkInterface;
    }

    /**
     * Get the Intranet IP
     *
     * @return If no {@link NetworkInterface} is available , return <code>null</code>
     */
    private static NetworkInterface findInnerAddress(List<NetworkInterface> validNetworkInterfaces) {

        NetworkInterface networkInterface = null;
        for (NetworkInterface ni : validNetworkInterfaces) {
            Enumeration<InetAddress> address = ni.getInetAddresses();
            while (address.hasMoreElements()) {
                InetAddress ip = address.nextElement();
                if (ip.isSiteLocalAddress()
                        && !ip.isLoopbackAddress()) {
                    networkInterface = ni;
                }
            }
        }
        return networkInterface;
    }

    private static NetworkInterface findOuterAddress(List<NetworkInterface> validNetworkInterfaces) {
        NetworkInterface networkInterface = null;
        for (NetworkInterface ni : validNetworkInterfaces) {
            Enumeration<InetAddress> address = ni.getInetAddresses();
            while (address.hasMoreElements()) {
                InetAddress ip = address.nextElement();
                if (!ip.isSiteLocalAddress()
                        && !ip.isLoopbackAddress()) {
                    networkInterface = ni;
                }
            }
        }
        return networkInterface;
    }

}
