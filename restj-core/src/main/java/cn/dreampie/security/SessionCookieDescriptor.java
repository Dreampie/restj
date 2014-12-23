package cn.dreampie.security;

import static cn.dreampie.http.HTTP.headerTokenCompatible;

/**
 * Created by ice on 14-12-23.
 */
public class SessionCookieDescriptor {
  private String cookieName;
  private String cookieSignatureName;

  public SessionCookieDescriptor() {
    this("Session", "SessionSignature");
  }

  public SessionCookieDescriptor(String cookieName, String cookieSignatureName) {
    this.cookieName = headerTokenCompatible(cookieName, "_");
    this.cookieSignatureName = headerTokenCompatible(cookieSignatureName, "_");
  }

  public String getCookieName() {
    return cookieName;
  }

  public String getCookieSignatureName() {
    return cookieSignatureName;
  }
}
