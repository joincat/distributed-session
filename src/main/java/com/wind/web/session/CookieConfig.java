package com.wind.web.session;

/**
 * 头部set-cookie的语法：Set-cookie:name=name;expires=date;path=path;domain=domain;secure name=name:
 */
public class CookieConfig {

    // cookie中默认的sessionId
    private String sessionIdName = "session_id";

    // cookie生效的域名
    // 如果domain为空，domain就被设置为提供cookie的Web服务器相同
    // 如果domain不为空，并且它的值又和提供cookie的Web服务器域名不符，这个Cookie将被忽略
    private String domain = "";

    // 设置cookie支持的路径,如果path是一个路径，则cookie对这个目录下的所有文件及子目录生效，
    // 如果path是一个文件,则cookie指对这个文件生效,如果没有指定path,cookie会对此站点的所有请求发送，但是path与当前访问的url不符,则此cookie将被忽略
    private String path = "/";

    // Cookie的有效期，单位秒
    private int maxAge = 600;

    //cookieSecure为true时，Cookie只对https协议生效，否则，浏览器将忽略此Cookie
    private boolean secure = false;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
    }

    public boolean isSecure() {
        return secure;
    }

    public void setSecure(boolean secure) {
        this.secure = secure;
    }

    public String getSessionIdName() {
        return sessionIdName;
    }

    public void setSessionIdName(String sessionIdName) {
        this.sessionIdName = sessionIdName;
    }
}
