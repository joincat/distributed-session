package com.wind.web.session;

import com.wind.web.common.EnumerationImpl;
import com.wind.web.redis.RedisStorage;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpSession;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class HttpSessionWrapper extends HttpSessionBasic {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpSessionWrapper.class);

    private final static String LAST_UPDATE_TIME = "LAST_UPDATE_TIME";
    private final static String CREATION_TIME = "CREATION_TIME";

    private String currentSessionId;
    private Map<String, Object> sessionMap;
    private Boolean isNew = false;
    private RedisStorage redisStorage;
    private HttpContext httpContext;
    private CookieConfig cookieConfig;

    public HttpSessionWrapper(HttpSession session, HttpContext httpContext, RedisStorage redisStorage) {
        super(session);
        this.httpContext = httpContext;
        this.redisStorage = redisStorage;
        this.cookieConfig = httpContext.getCookieConfig();
        initSession(httpContext);
    }

    @SuppressWarnings("unchecked")
    private void initSession(HttpContext httpContext) {
        currentSessionId = CookieUtil.getCurrentSessionId(httpContext);
        if (StringUtils.isEmpty(currentSessionId)) {
            currentSessionId = CookieUtil.generateNewSessionId();
            CookieUtil.writeCookie(currentSessionId, httpContext);
            isNew = true;
        }
        LOGGER.info("currentSessionId:{}", currentSessionId);

        //从redis中获取该sessionId对应的session数据
        Object session = redisStorage.get(currentSessionId);
        if (session == null) {
            LOGGER.info("session is null");
            isNew = true;
            sessionMap = new HashMap<>();
            sessionMap.put(CREATION_TIME, System.currentTimeMillis());
            sessionMap.put(LAST_UPDATE_TIME, System.currentTimeMillis());
        } else {
            LOGGER.info("session existed");
            sessionMap = (Map<String, Object>) session;
            long lastUpdateTime = (long) sessionMap.get(LAST_UPDATE_TIME);
            long period = (System.currentTimeMillis() - lastUpdateTime) / 1000;//计算存活时间，单位秒
            // 过期
            if (period > httpContext.getCookieConfig().getMaxAge()) {
                LOGGER.info("session expired");
                invalidate();
                sessionMap.put(CREATION_TIME, System.currentTimeMillis());
                sessionMap.put(LAST_UPDATE_TIME, System.currentTimeMillis());
                isNew = true;
            } else {
                //未过期
                LOGGER.info("update session");
                sessionMap.put(LAST_UPDATE_TIME, System.currentTimeMillis());
                redisStorage.replace(currentSessionId, sessionMap, cookieConfig.getMaxAge());
            }
        }
    }

    @Override
    public Object getAttribute(String name) {
        return sessionMap.get(name);
    }

    @Override
    public Enumeration getAttributeNames() {
        return (new EnumerationImpl(sessionMap.keySet()));
    }

    @Override
    public void setAttribute(String name, Object value) {
        sessionMap.put(name, value);
        sessionMap.put(LAST_UPDATE_TIME, System.currentTimeMillis());
        redisStorage.put(currentSessionId, sessionMap, cookieConfig.getMaxAge());
    }

    @Override
    public void removeAttribute(String name) {
        sessionMap.remove(name);
        redisStorage.replace(currentSessionId, sessionMap, cookieConfig.getMaxAge());
    }

    @Override
    public String getId() {
        return currentSessionId;
    }

    @Override
    public long getLastAccessedTime() {
        return (long) sessionMap.get(LAST_UPDATE_TIME);
    }

    @Override
    public int getMaxInactiveInterval() {
        return cookieConfig.getMaxAge();
    }

    @Override
    public void invalidate() {
        sessionMap.clear();
        redisStorage.del(currentSessionId);
        CookieUtil.deleteCookie(httpContext);
    }

    @Override
    public boolean isNew() {
        return this.isNew;
    }

    @Override
    public long getCreationTime() {
        if (sessionMap.containsKey(CREATION_TIME)) {
            return (long) sessionMap.get(CREATION_TIME);
        }
        return System.currentTimeMillis();
    }
}
