package cn.dreampie.security;

import cn.dreampie.exception.WebException;
import cn.dreampie.http.HttpStatus;
import com.google.common.base.Optional;
import org.joda.time.Duration;

import java.util.UUID;

/**
 * Created by ice on 14-12-23.
 */
public class SubjectKit {

  private static Authenticator authenticator;
  private static int rememberDay;

  public static void setRememberDay(int rememberDay) {
    SubjectKit.rememberDay = rememberDay;
  }

  public static void setAuthenticator(Authenticator authenticator) {
    SubjectKit.authenticator = authenticator;
  }

  public static Session getSession() {
    String sessionKey = Subject.current().get(Session.SESSION_DEF_KEY).get().toString();
    Principal principal = Subject.current().getPrincipal().get();
    return new Session(sessionKey, principal);
  }

  public static Optional<? extends Principal> getPrincipal() {
    return Subject.current().getPrincipal();
  }

  public static Session login(String name, String password, boolean rememberMe) {
    if (authenticator != null) {
      Optional<? extends Principal> principal = authenticator.findByName(name);
      if (!principal.isPresent())
        throw new WebException(HttpStatus.UNAUTHORIZED);
      else {
        principal = authenticator.authenticate(name, password);
        if (principal.isPresent())
          Subject.current().expires(rememberMe
              ? Duration.standardDays(rememberDay) : Duration.ZERO);
        else
          throw new WebException(HttpStatus.UNAUTHORIZED);
      }
      Subject.current().authenticateAs(principal.get());
      String sessionKey = UUID.randomUUID().toString();
      Subject.current().define(Session.SESSION_DEF_KEY, sessionKey);
      return new Session(sessionKey, principal.get());
    } else {
      throw new WebException(HttpStatus.UNAUTHORIZED, "AuthenticateService not found!");
    }
  }

  public static Subject logout() {
    return Subject.current().clearPrincipal();
  }

  public static <T> Optional<T> get(String id) {
    return (Optional<T>) Subject.current().get(id);
  }

  public static Subject set(String id, Object value) {
    return Subject.current().set(id, value);
  }

  public static Subject cleanUpCaches() {
    return Subject.current().cleanUpCaches();
  }

  public static Subject expires(Duration duration) {
    return Subject.current().expires(duration);
  }

  public static Duration getExpires() {
    return Subject.current().getExpires();
  }

}
