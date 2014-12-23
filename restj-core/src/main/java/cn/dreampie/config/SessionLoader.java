package cn.dreampie.config;

import cn.dreampie.security.Authenticator;

/**
 * Created by ice on 14-12-23.
 */
public class SessionLoader {
  private int limit;
  private int rememberDay;
  private Authenticator authenticator;


  public int getLimit() {
    return limit;
  }

  public void setLimit(int limit) {
    this.limit = limit;
  }

  public int getRememberDay() {
    return rememberDay;
  }

  public void setRememberDay(int rememberDay) {
    this.rememberDay = rememberDay;
  }

  public Authenticator getAuthenticator() {
    return authenticator;
  }

  public void setAuthenticator(Authenticator authenticator) {
    this.authenticator = authenticator;
  }
}
