package com.wind.web.filter;

import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;


public abstract class IgnorableFilter extends GenericFilterBean {

    private String ignorePath;

    public void setIgnorePath(String ignorePath) {
        this.ignorePath = ignorePath;
    }

    public boolean isIgnorable(ServletRequest request) {
        String[] ignorePaths = ignorePath.split(",");
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String requestUrl = httpServletRequest.getRequestURI();
        for (String path : ignorePaths) {
            if (requestUrl.contains(path)) {
                return true;
            }
        }
        return false;
    }
}
