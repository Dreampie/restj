package cn.dreampie.http;

import com.google.common.base.Optional;
import com.google.common.collect.*;

import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Date: 1/22/13
 * Time: 2:52 PM
 */
public class HttpRequest extends AbstractRequest {
  private final HttpServletRequest request;
  private BufferedInputStream bufferedInputStream;
  private ImmutableMap<String, ImmutableList<String>> queryParams;

  public HttpRequest(HttpServletRequest request) {
    this.request = request;
  }

  @Override
  public String getLocalClientAddress() {
    return request.getRemoteAddr();
  }

  @Override
  protected String getBasePath() {
    return request.getContextPath();
  }

  @Override
  protected String getLocalScheme() {
    return request.getScheme();
  }

  @Override
  public String getRestPath() {
    String basepath = getBasePath();
    if (basepath.length() > 0)
      return request.getRequestURI().substring(basepath.length());
    else
      return request.getRequestURI();
  }

  @Override
  public String getRestUri() {
    if (request.getQueryString() == null) {
      return getRestPath();
    } else {
      return getRestPath() + "?" + request.getQueryString();
    }
  }

  @Override
  public Optional<String> getQueryParam(String param) {
    return Optional.fromNullable(request.getParameter(param));
  }

  @Override
  public List<String> getQueryParams(String param) {
    return Lists.newArrayList(request.getParameterValues(param));
  }

  @Override
  public ImmutableMap<String, ImmutableList<String>> getQueryParams() {
    if (queryParams == null) {
      Map<String, String[]> parameterMap = getParameterMap();
      ImmutableMap.Builder<String, ImmutableList<String>> paramsBuilder = ImmutableMap.builder();
      for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
        paramsBuilder.put(entry.getKey(), ImmutableList.copyOf(entry.getValue()));
      }
      queryParams = paramsBuilder.build();
    }
    return queryParams;
  }

  @SuppressWarnings("unchecked")
  protected Map<String, String[]> getParameterMap() {
    return request.getParameterMap();
  }

  @Override
  public InputStream getContentStream() throws IOException {
        /*
           maybe we could do this buffering only in dev mode?
           It is used to be able to read data again in case of json processing error.
         */
    if (bufferedInputStream == null) {
      bufferedInputStream = new BufferedInputStream(request.getInputStream()) {
        @Override
        public void close() throws IOException {
          // NO OP, see #closeContentStream
        }
      };
      bufferedInputStream.mark(10 * 1024);
    }
    return bufferedInputStream;
  }

  @Override
  public void closeContentStream() throws IOException {
    bufferedInputStream.close();
  }

  @Override
  public HttpMethod getHttpMethod() {
    HttpMethod httpMethod = HttpMethod.valueOf(request.getMethod());
    if (httpMethod == null)
      throw new IllegalArgumentException("Invalid HTTP Method : " + httpMethod);
    return httpMethod;
  }

  @Override
  public ImmutableMap<String, String> getCookiesMap() {
    Map<String, String> cookies = Maps.newLinkedHashMap();
    Cookie[] requestCookies = request.getCookies();
    if (requestCookies != null) {
      for (int i = 0; i < requestCookies.length; i++) {
        Cookie cookie = requestCookies[i];
        cookies.put(cookie.getName(), cookie.getValue());
      }
    }
    return ImmutableMap.copyOf(cookies);
  }

  @Override
  public Optional<String> getCookieValue(String cookieName) {
    return Optional.fromNullable(getCookieValue(request.getCookies(), cookieName));
  }

  @Override
  public boolean isPersistentCookie(String cookie) {
    Cookie c = getCookie(request.getCookies(), cookie);
    return c == null ? false : c.getMaxAge() > 0;
  }

  private static String getCookieValue(Cookie[] cookies,
                                       String cookieName) {
    if (cookies == null) {
      return null;
    }
    for (int i = 0; i < cookies.length; i++) {
      Cookie cookie = cookies[i];
      if (cookieName.equals(cookie.getName()))
        return cookie.getValue();
    }
    return null;
  }

  static Cookie getCookie(Cookie[] cookies, String cookieName) {
    if (cookies == null) {
      return null;
    }
    for (int i = 0; i < cookies.length; i++) {
      Cookie cookie = cookies[i];
      if (cookieName.equals(cookie.getName()))
        return cookie;
    }
    return null;
  }

  @Override
  public Optional<String> getHeader(String headerName) {
    return Optional.fromNullable(request.getHeader(headerName));
  }

  public Enumeration<String> getHeaders(String headerName) {
    return request.getHeaders(headerName);
  }

  @Override
  public String getContentType() {
    return request.getContentType();
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T unwrap(Class<T> clazz) {
    if (clazz == HttpServletRequest.class || clazz == ServletRequest.class) {
      return (T) request;
    }
    throw new IllegalArgumentException("underlying implementation is HttpServletRequest, not " + clazz.getName());
  }

  @Override
  public Locale getLocale() {
    return request.getLocale();
  }

  @Override
  @SuppressWarnings("unchecked")
  public ImmutableList<Locale> getLocales() {
    return ImmutableList.copyOf(Iterators.<Locale>forEnumeration(request.getLocales()));
  }
}
