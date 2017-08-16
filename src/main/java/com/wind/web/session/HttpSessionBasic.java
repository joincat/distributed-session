package com.wind.web.session;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.util.Enumeration;

public abstract class HttpSessionBasic implements HttpSession {

    private HttpSession session;

    public HttpSessionBasic(HttpSession session) {
        this.session = session;
    }

    public void setSession(HttpSession session) {
        this.session = session;
    }

    @Override
    public long getCreationTime() {
        return this.session.getCreationTime();
    }

    @Override
    public String getId() {
        return this.session.getId();
    }

    @Override
    public long getLastAccessedTime() {
        return this.session.getLastAccessedTime();
    }

    @Override
    public ServletContext getServletContext() {
        return this.session.getServletContext();
    }

    @Override
    public void setMaxInactiveInterval(int interval) {
        session.setMaxInactiveInterval(interval);
    }

    @Override
    public int getMaxInactiveInterval() {
        return this.session.getMaxInactiveInterval();
    }

    @Override
    public HttpSessionContext getSessionContext() {
        return this.session.getSessionContext();
    }

    @Override
    public Object getAttribute(String name) {
        return this.session.getAttribute(name);
    }

    @Override
    public Object getValue(String name) {
        return this.session.getValue(name);
    }

    @Override
    public Enumeration getAttributeNames() {
        return this.session.getAttributeNames();
    }

    @Override
    public String[] getValueNames() {
        return session.getValueNames();
    }

    @Override
    public void setAttribute(String name, Object value) {
        session.setAttribute(name, value);
    }

    @Override
    public void putValue(String name, Object value) {
        session.putValue(name, value);
    }

    @Override
    public void removeAttribute(String name) {
        session.removeAttribute(name);
    }

    @Override
    public void removeValue(String name) {
        session.removeValue(name);
    }

    @Override
    public void invalidate() {
        session.invalidate();
    }

    @Override
    public boolean isNew() {
        return session.isNew();
    }
}
