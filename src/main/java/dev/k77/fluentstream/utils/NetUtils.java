package dev.k77.fluentstream.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NetUtils {
    private static final Pattern IP_ADDR_PATTERN = Pattern.compile("(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})");
    private static final Pattern CIDR_PATTERN = Pattern.compile("(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})/(\\d{1,2})");

    public static int aton(String address) {
        Matcher matcher = IP_ADDR_PATTERN.matcher(address);
        if (matcher.matches()) {
            int addr = 0;

            for(int i = 1; i <= 4; ++i) {
                int n = rangeCheck(Integer.parseInt(matcher.group(i)));
                addr |= (n & 255) << 8 * (4 - i);
            }

            return addr;
        } else {
            throw new IllegalArgumentException(String.format("Could not parse [%s]", address));
        }
    }

    public static String ntoa(int base) {
        return String.format("%d.%d.%d.%d", (base >> 24 & 0xff), (base >> 16 & 0xff), (base >> 8 & 0xff), (base & 0xff));
    }

    public static boolean isIpAddress(String ipAddress) {
        Matcher matcher = IP_ADDR_PATTERN.matcher(ipAddress);
        return matcher.matches();
    }

    public static boolean isCidr(String cidr) {
        Matcher matcher = CIDR_PATTERN.matcher(cidr);
        return matcher.matches();
    }

    private static int rangeCheck(int value) {
        return rangeCheck(value, 0, 255);
    }
    private static int rangeCheck(int value, int begin, int end) {
        if (value >= begin && value <= end) {
            return value;
        } else {
            throw new IllegalArgumentException("Value [" + value + "] not in range [" + begin + "," + end + "]");
        }
    }
}
