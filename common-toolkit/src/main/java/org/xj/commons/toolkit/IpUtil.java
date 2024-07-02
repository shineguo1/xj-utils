package org.xj.commons.toolkit;

import org.jetbrains.annotations.Nullable;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author xj
 * @version 1.0.0 createTime:  2023/8/16 16:51
 */
public class IpUtil {

    private static InetAddress localHost;
    private static String localHostSignature;

    private static String pattern = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
            "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
            "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
            "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

    @Nullable
    public static InetAddress getInetAddress() {
        if (localHost == null) {
            try {
                localHost = InetAddress.getLocalHost();
            } catch (UnknownHostException e) {
                return null;
            }
        }
        return localHost;
    }

    @Nullable
    public static String getInetAddressSignature() {
        if (localHostSignature == null) {
            InetAddress inetAddress = getInetAddress();
            if (inetAddress != null) {
                return localHostSignature = inetAddress.getHostAddress() + " | " + inetAddress.getHostName();
            }
        }
        return localHostSignature;
    }

    public static boolean isIpAddress(String input) {
        return input.matches(pattern);
    }
}
