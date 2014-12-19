package cn.dreampie.http;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

/**
 * Created by ice on 14-12-19.
 */
public interface RestjRequest {

  /**
   * Is this request performed through a secured connection or not.
   * <p/>
   * This will return true if:
   * - the HttpSettings proto() is set to 'https'
   * - the request has a 'X-Forwarded-Proto' header with value 'https', and comes from an authorized proxy
   * as defined by HttpSettings.forwardedSupport()
   * - the request was performed in HTTPS on this server
   *
   * @return true if this request is performed through a secured (HTTPS) connection.
   */
  boolean isSecured();

  /**
   * HTTP METHOD, eg GET, POST, ...
   *
   * @return the request HTTP method
   */
  HttpMethod getHttpMethod();

  Optional<String> getQueryParam(String param);

  List<String> getQueryParams(String param);

  ImmutableMap<String, ImmutableList<String>> getQueryParams();

  Optional<String> getHeader(String headerName);

  String getContentType();

  Optional<String> getCookieValue(String cookieName);

  boolean isPersistentCookie(String cookie);

  ImmutableMap<String, String> getCookiesMap();

  /**
   * The address (IP) of the client.
   * <p/>
   * If X-Forwarded-For header is present, it will return its value, otherwise it returns
   * the remote client address.
   * <p/>
   * see http://httpd.apache.org/docs/current/mod/mod_proxy.html#x-headers for details on this header.
   *
   * @return IP address of the client.
   */
  String getClientAddress();

  InputStream getContentStream() throws IOException;

  /**
   * Closes the request content input stream.
   * <p>
   * Closing the content stream using the close method may not definitely close it, in case a buffered input stream
   * has been used to provide access to request content for logs and error processing.
   * </p>
   * <p>
   * Restx framework will always call this method at the end of request processing.
   * </p>
   *
   * @throws IOException
   */
  void closeContentStream() throws IOException;

  /**
   * Unwraps the underlying native implementation of given class.
   * <p/>
   * Examnple: This is a HttpServletRequest in a servlet container.
   *
   * @param clazz the class of the underlying implementation
   * @param <T>   unwrapped class
   * @return the unwrapped implementation.
   * @throws java.lang.IllegalArgumentException if the underlying implementation is not of given type.
   */
  <T> T unwrap(Class<T> clazz);

  /**
   * Returns the preferred <code>Locale</code> that the client will
   * accept content in, based on the Accept-Language header.
   * If the client request doesn't provide an Accept-Language header,
   * this method returns the default locale for the server.
   *
   * @return the preferred <code>Locale</code> for the client
   */
  Locale getLocale();

  /**
   * Returns an <code>ImmutableList</code> of <code>Locale</code> objects
   * indicating, in decreasing order starting with the preferred locale, the
   * locales that are acceptable to the client based on the Accept-Language
   * header.
   * If the client request doesn't provide an Accept-Language header,
   * this method returns an <code>ImmutableList</code> containing one
   * <code>Locale</code>, the default locale for the server.
   *
   * @return an <code>ImmutableList</code> of preferred
   * <code>Locale</code> objects for the client
   */
  ImmutableList<Locale> getLocales();
}
