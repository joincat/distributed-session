package com.wind.web.session;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HttpContext {

    private HttpServletRequest request;
    private HttpServletResponse response;
    private CookieConfig cookieConfig;

    public HttpContext(HttpServletRequest request, HttpServletResponse response, CookieConfig cookieConfig) {
        this.request = request;
        this.response = response;
        this.cookieConfig = cookieConfig;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public CookieConfig getCookieConfig() {
        return cookieConfig;
    }
}
