package com.wind.web.session;

import com.wind.web.redis.RedisStorage;

import javax.servlet.http.HttpSession;

public class HttpServletRequestWrapper extends javax.servlet.http.HttpServletRequestWrapper {

    private HttpContext httpContext;
    private RedisStorage redisStorage;

    /**
     * Constructs a request object wrapping the given request.
     *
     * @param httpContext
     * @throws IllegalArgumentException if the request is null
     */
    public HttpServletRequestWrapper(HttpContext httpContext, RedisStorage redisStorage) {
        super(httpContext.getRequest());
        this.httpContext = httpContext;
        this.redisStorage = redisStorage;
    }

    @Override
    public HttpSession getSession(boolean create) {
        return new HttpSessionWrapper(super.getSession(create), httpContext, redisStorage);
    }

    @Override
    public HttpSession getSession() {
        return this.getSession(true);
    }
}
