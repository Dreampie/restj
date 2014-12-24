package cn.dreampie.security;

/**
 * User: xavierhanin
 * Date: 7/5/13
 * Time: 8:45 PM
 */
public class Subject {
    private String sessionKey;
    private Principal principal;

    public Subject(String sessionKey, Principal principal) {
        this.sessionKey = sessionKey;
        this.principal = principal;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public Principal getPrincipal() {
        return principal;
    }
}
