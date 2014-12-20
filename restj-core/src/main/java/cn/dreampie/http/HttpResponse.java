package cn.dreampie.http;

import org.joda.time.Duration;

import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * User: xavierhanin
 * Date: 2/6/13
 * Time: 9:40 PM
 */
public class HttpResponse extends AbstractResponse<HttpServletResponse> {
  private final HttpServletResponse response;
  private final HttpServletRequest request;

  public HttpResponse(HttpServletResponse response, HttpServletRequest request) {
    super(HttpServletResponse.class, response);
    this.response = response;
    this.request = request;
  }

  @Override
  protected void doSetStatus(HttpStatus httpStatus) {
    response.setStatus(httpStatus.getCode());
  }

  @Override
  protected OutputStream doGetOutputStream() throws IOException {
    return response.getOutputStream();
  }

  @Override
  protected void closeResponse() throws IOException {
  }

  @Override
  public Response addCookie(String cookie, String value, Duration expiration) {
    Cookie existingCookie = HttpRequest.getCookie(request.getCookies(), cookie);
    if (existingCookie != null) {
      if ("/".equals(existingCookie.getPath())
          || existingCookie.getPath() == null // in some cases cookies set on path '/' are returned with a null path
          ) {
        // update existing cookie
        existingCookie.setPath("/");
        existingCookie.setValue(value);
        existingCookie.setMaxAge(expiration.getStandardSeconds() > 0 ? (int) expiration.getStandardSeconds() : -1);
        response.addCookie(existingCookie);
      } else {
        // we have an existing cookie on another path: clear it, and add a new cookie on root path
        existingCookie.setValue("");
        existingCookie.setMaxAge(0);
        response.addCookie(existingCookie);

        Cookie c = new Cookie(cookie, value);
        c.setPath("/");
        c.setMaxAge(expiration.getStandardSeconds() > 0 ? (int) expiration.getStandardSeconds() : -1);
        response.addCookie(c);
      }
    } else {
      Cookie c = new Cookie(cookie, value);
      c.setPath("/");
      c.setMaxAge(expiration.getStandardSeconds() > 0 ? (int) expiration.getStandardSeconds() : -1);
      response.addCookie(c);
    }
    return this;
  }

  @Override
  public Response clearCookie(String cookie) {
    Cookie existingCookie = HttpRequest.getCookie(request.getCookies(), cookie);
    if (existingCookie != null) {
      existingCookie.setPath("/");
      existingCookie.setValue("");
      existingCookie.setMaxAge(0);
      response.addCookie(existingCookie);
    }
    return this;
  }

  @Override
  public void doSetHeader(String headerName, String header) {
    response.setHeader(headerName, header);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T unwrap(Class<T> clazz) {
    if (clazz == HttpServletResponse.class || clazz == ServletResponse.class) {
      return (T) response;
    }
    throw new IllegalArgumentException("underlying implementation is HttpServletResponse, not " + clazz.getName());
  }
}
