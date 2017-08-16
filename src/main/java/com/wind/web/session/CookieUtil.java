package com.wind.web.session;

import org.apache.commons.lang.StringUtils;

import javax.servlet.http.Cookie;
import java.util.UUID;

public class CookieUtil {

    public static String getCurrentSessionId(HttpContext httpContext) {
        //TODO 浏览器禁止cookie的情况没考虑，需要优化

        Cookie cookies[] = httpContext.getRequest().getCookies();
        if (cookies != null && cookies.length != 0) {
            for (Cookie sessionCookie : cookies) {
                if (httpContext.getCookieConfig().getSessionIdName().equalsIgnoreCase(sessionCookie.getName())) {
                    return sessionCookie.getValue();
                }
            }
        }
        return StringUtils.EMPTY;
    }

    public static String generateNewSessionId() {
        return StringUtils.replace(UUID.randomUUID().toString().toUpperCase(), "-", "");
    }

    public static void writeCookie(String sessioId, HttpContext httpContext) {
        httpContext.getResponse().addHeader("Set-Cookie", buildCookie(sessioId, httpContext.getCookieConfig(), httpContext.getCookieConfig().getMaxAge()));
    }

    private static String buildCookie(String sessioId, CookieConfig cookieConfig, int maxAge) {
        StringBuilder cookieString = new StringBuilder(cookieConfig.getSessionIdName());
        cookieString.append("=").append(sessioId).append(";");
        if (StringUtils.isNotEmpty(cookieConfig.getPath())) {
            cookieString.append("Path=").append(cookieConfig.getPath()).append(";");
        }

        if (StringUtils.isNotEmpty(cookieConfig.getDomain())) {
            cookieString.append("Domain=").append(cookieConfig.getDomain()).append(";");
        }

        if (cookieConfig.isSecure()) {
            cookieString.append("Secure;");
        }
        cookieString.append("HttpOnly;");
        cookieString.append("Max-Age=").append(maxAge).append(";");
        return cookieString.toString();
    }

    public static void deleteCookie(HttpContext httpContext) {
        httpContext.getResponse().addHeader("Set-Cookie", buildCookie("", httpContext.getCookieConfig(), 0));
    }
}
