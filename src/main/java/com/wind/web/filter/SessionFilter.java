package com.wind.web.filter;

import com.wind.web.redis.RedisSessionManager;
import com.wind.web.redis.RedisStorage;
import com.wind.web.session.CookieConfig;
import com.wind.web.session.HttpContext;
import com.wind.web.session.HttpServletRequestWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SessionFilter extends IgnorableFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(SessionFilter.class);

    private CookieConfig cookieConfig;
    private RedisSessionManager redisSessionManager;
    private RedisStorage redisStorage;

    @Override
    protected void initFilterBean() throws ServletException {
        super.initFilterBean();
        redisStorage = redisSessionManager.getRedisStorage();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        LOGGER.info("request url :{}", httpServletRequest.getRequestURI());
        if (isIgnorable(request)) {
            LOGGER.info("ignore");
            chain.doFilter(request, response);
            return;
        }
        LOGGER.info("do filter");
        chain.doFilter(new HttpServletRequestWrapper(new HttpContext(httpServletRequest, httpServletResponse, cookieConfig), redisStorage), response);
    }

    public void setCookieConfig(CookieConfig cookieConfig) {
        this.cookieConfig = cookieConfig;
    }

    public void setRedisSessionManager(RedisSessionManager redisSessionManager) {
        this.redisSessionManager = redisSessionManager;
    }
}
