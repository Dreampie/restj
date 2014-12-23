package cn.dreampie.security;

import cn.dreampie.exception.WebException;
import com.google.common.base.Optional;
import org.joda.time.Duration;

/**
 * Created by ice on 14-12-23.
 */
public class Subject {

  private static Authenticator authenticator;
  private static int rememberDay;

  public static void setRememberDay(int rememberDay) {
    Subject.rememberDay = rememberDay;
  }

  public static void setAuthenticator(Authenticator authenticator) {
    Subject.authenticator = authenticator;
  }

  public static Session getSession() {
    return Session.current();
  }

  public static Optional<? extends Principal> getPrincipal() {
    return Session.current().getPrincipal();
  }

  public static Session login(String name, String password, boolean rememberMe) {
    if (authenticator != null) {
      Optional<? extends Principal> principal = authenticator.findByName(name);
      if (!principal.isPresent())
        throw new WebException("帐号不存在");
      else {
        principal = authenticator.authenticate(name, password);
        if (principal.isPresent())
          Session.current().expires(rememberMe
              ? Duration.standardDays(rememberDay) : Duration.ZERO);
        else
          throw new WebException("秘密错误");
      }
      return Session.current().authenticateAs(principal.get());
    } else {
      throw new RuntimeException("AuthenticateService 没有找到!");
    }
  }

  public static Session logout() {
    return Session.current().clearPrincipal();
  }

  public static <T> Optional<T> get(String id) {
    return (Optional<T>) Session.current().get(id);
  }

  public static Session set(String id, Object value) {
    return Session.current().set(id, value);
  }

  public static Session cleanUpCaches() {
    return Session.current().cleanUpCaches();
  }

  public static Session expires(Duration duration) {
    return Session.current().expires(duration);
  }

  public static Duration getExpires() {
    return Session.current().getExpires();
  }

}
