package cn.dreampie.security;

import cn.dreampie.handler.Handler;
import cn.dreampie.http.HttpRequest;
import cn.dreampie.http.HttpResponse;
import cn.dreampie.log.Logger;
import cn.dreampie.log.LoggerFactory;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Optional;
import com.google.common.cache.CacheLoader;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ice on 14-12-23.
 */
public class SessionHandler extends Handler {

  public static final String COOKIE_SIGNER_NAME = "CookieSigner";

  private static final String EXPIRES = "_expires";

  private final static Logger logger = LoggerFactory.getLogger(SessionHandler.class);

  private final Sessions sessions;
  private final Session.Definition sessionDefinition;
  private final Signer signer;
  private final SessionCookieDescriptor sessionCookieDescriptor;
  private final Session emptySession;


  public SessionHandler(int limit, final Authenticator authenticator) {
    Subject.setAuthenticator(authenticator);
    this.sessions = new Sessions(limit);
    this.sessionDefinition = new Session.Definition(new Session.Definition.Entry<Principal>(Principal.SESSION_DEF_KEY,
        new CacheLoader<String, Principal>() {
          public Principal load(String key) throws Exception {
            return authenticator.findByName(key).orNull();
          }
        }));
    this.signer = new CookieSigner(Optional.<SignatureKey>absent());
    this.sessionCookieDescriptor = new SessionCookieDescriptor();
    this.emptySession = new Session(sessionDefinition, ImmutableMap.<String, String>of(),
        Optional.<Principal>absent(), Duration.ZERO);
  }

  public void handle(HttpRequest request, HttpResponse response, boolean[] isHandled) {

    Session session = buildSession(request);
    Session.setCurrent(session);

    ImmutableMap<String, String> metadata = prepareSessionStatsMetadata(request, session);
    if (session.getPrincipal().isPresent()) {
      String name = session.getPrincipal().get().getUsername();
      sessions.touch(name, metadata);
    } else {
      sessions.touch("anonymous@" + request.getClientAddress(), metadata);
    }

    //让下一个拦截器返回
    nextHandler.handle(request, response, isHandled);

    Session newSession = Session.current();
    if (newSession != session) {
      updateSessionInClient(response, newSession);
    }
  }

  protected ImmutableMap<String, String> prepareSessionStatsMetadata(HttpRequest req, Session session) {
    return ImmutableMap.of(
        "clientAddress", req.getClientAddress(),
        "userAgent", req.getHeader("User-Agent").or("Unknown"));
  }

  public Session buildSession(HttpRequest req) {
    String sessionCookieName = sessionCookieDescriptor.getCookieName();
    String cookie = req.getCookieValue(sessionCookieName).or("");
    if (cookie.trim().isEmpty()) {
      return emptySession;
    } else {
      String sig = req.getCookieValue(sessionCookieDescriptor.getCookieSignatureName()).or("");
      if (!signer.verify(cookie, sig)) {
        logger.warn("invalid  session signature. session was: {}. Ignoring session cookie.", cookie);
        return emptySession;
      }
      Map<String, String> entries = readEntries(cookie);
      DateTime expires = DateTime.parse(entries.remove(EXPIRES));
      if (expires.isBeforeNow()) {
        return emptySession;
      }

      Duration expiration = req.isPersistentCookie(sessionCookieName) ? new Duration(DateTime.now(), expires) : Duration.ZERO;
      ImmutableMap<String, String> cookieValues = ImmutableMap.copyOf(entries);
      String principalName = cookieValues.get(Principal.SESSION_DEF_KEY);
      Optional<Principal> principalOptional = Session.getValue(
          sessionDefinition, Principal.SESSION_DEF_KEY, principalName);

      return new Session(sessionDefinition, cookieValues, principalOptional, expiration);
    }
  }

  protected Map<String, String> readEntries(String cookie) {
    return JSON.parseObject(cookie, Map.class);
  }

  private void updateSessionInClient(HttpResponse resp, Session session) {
    ImmutableMap<String, String> cookiesMap = toCookiesMap(session);
    if (cookiesMap.isEmpty()) {
      resp.clearCookie(sessionCookieDescriptor.getCookieName());
      resp.clearCookie(sessionCookieDescriptor.getCookieSignatureName());
    } else {
      for (Map.Entry<String, String> cookie : cookiesMap.entrySet()) {
        resp.addCookie(cookie.getKey(), cookie.getValue(), session.getExpires());
      }
    }
  }

  public ImmutableMap<String, String> toCookiesMap(Session session) {
    ImmutableMap<String, String> sessionMap = session.getCookieValues();
    if (sessionMap.isEmpty()) {
      return ImmutableMap.of();
    } else {
      HashMap<String, String> map = Maps.newHashMap(sessionMap);
      map.put(EXPIRES, DateTime.now().plusDays(30).toString());
      String sessionJson = JSON.toJSONString(map);
      return ImmutableMap.of(sessionCookieDescriptor.getCookieName(), sessionJson,
          sessionCookieDescriptor.getCookieSignatureName(), signer.sign(sessionJson));
    }
  }

  public String toString() {
    return "SessionCookieFilter";
  }
}
