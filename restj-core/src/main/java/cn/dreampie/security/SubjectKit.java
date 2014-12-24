package cn.dreampie.security;

import cn.dreampie.exception.WebException;
import cn.dreampie.http.HttpStatus;
import com.google.common.base.Optional;
import org.joda.time.Duration;

import java.util.UUID;

/**
 * Created by wangrenhui on 14/12/23.
 */
public class SubjectKit {
  private static Authenticator authenticator;
  private static PasswordService passwordService;
  private static int rememberDay = 7;


  static void init(int rememberDay, Authenticator authenticator, PasswordService passwordService) {
    SubjectKit.rememberDay = rememberDay;
    SubjectKit.authenticator = authenticator;
    SubjectKit.passwordService = passwordService;
  }

  public static Subject current() {
    String sessionKey = (String) Session.current().get(Session.SESSION_DEF_KEY).get();
    Principal principal = Session.current().getPrincipal().get();
    return new Subject(sessionKey, principal);
  }


  public static void cleanUpCaches() {
    Session.current().cleanUpCaches();
  }

  public static Duration getExpires() {
    return Session.current().getExpires();
  }

  public static Subject login(String name, String password, boolean rememberMe) {
    if (authenticator != null) {
      Optional<? extends Principal> principal = authenticator.findByName(name);
      if (principal.isPresent() && passwordService.match(password, principal.get().getPasswordHash())) {
        //清理已经登陆的对象
        Session.current().clearPrincipal();
        Session.current().define(Session.SESSION_DEF_KEY, null);
        //授权用户
        Session.current().expires(rememberMe
            ? Duration.standardDays(rememberDay) : Duration.ZERO);
        String sessionKey = UUID.randomUUID().toString();
        Session.current().authenticateAs(principal.get());
        Session.current().define(Session.SESSION_DEF_KEY, sessionKey);
        return new Subject(sessionKey, principal.get());
      } else
        throw new WebException(HttpStatus.UNAUTHORIZED);

    } else {
      throw new WebException(HttpStatus.UNAUTHORIZED, "AuthenticateService not found!");
    }
  }

  public void logout() {
    Session.current().clearPrincipal();
    Session.current().define(Session.SESSION_DEF_KEY, null);
  }

  public static Optional<? extends Principal> getPrincipal() {
    return Session.current().getPrincipal();
  }

  public static boolean checkPermission(String permission) {
    Optional<? extends Principal> principal = getPrincipal();
    if (principal.isPresent())
      return principal.get().checkPremission(permission);
    else
      return false;
  }
}
