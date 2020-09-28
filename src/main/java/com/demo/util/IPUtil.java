package com.demo.util;

import javax.servlet.http.HttpServletRequest;

public class IPUtil {
    public static String FORWARD_HEADER = "X-FORWARDED-FOR";
    private static String REAL_HEADER = "X-REAL-IP";

    public static String getRemoteIp(HttpServletRequest request) {
        String ip = null;
        if (request == null) {
            return ip;
        }
        ip = request.getHeader(FORWARD_HEADER);
        if (ip != null) {
            return getFirstIP(ip);
        }
        ip = request.getHeader(REAL_HEADER);
        if (ip != null) {
            return getFirstIP(ip);
        }
        return request.getRemoteAddr();
    }

    private static String getFirstIP(String ip) {
        int idx = ip.indexOf(",");
        if (idx >= 0) {
            String[] ips = ip.split(",");
            String firstIP = ips[0];
            if (firstIP != null) {
                return firstIP;
            }
        }
        return ip;
    }
}