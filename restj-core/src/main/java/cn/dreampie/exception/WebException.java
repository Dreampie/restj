package cn.dreampie.exception;

import cn.dreampie.http.HttpStatus;
import cn.dreampie.http.RestjRequest;
import cn.dreampie.http.RestjResponse;
import cn.dreampie.log.Logger;
import cn.dreampie.log.LoggerFactory;

import java.io.IOException;

/**
 * Created by ice on 14-12-19.
 /**
 * A WebException can be raised to make restx return immediately an HTTP response with a specific HTTP status.
 */
public class WebException extends RuntimeException {
  private final static Logger logger = LoggerFactory.getLogger(WebException.class);

  private final HttpStatus status;

  public WebException(HttpStatus status) {
    super(status.getDesc());
    this.status = status;
  }

  public WebException(HttpStatus status, String message) {
    super(message);
    this.status = status;
  }

  public WebException(String message) {
    super(message);
    this.status = HttpStatus.BAD_REQUEST;
  }

  public HttpStatus getStatus() {
    return status;
  }

  /**
   * Returns the content type to use in the HTTP response generated for this exception.
   *
   * Developer's note: override to provide another content type than text-plain
   * Alternatively you can override the writeTo method for full control over the response.
   *
   * @return the content type to use in the response.
   */
  public String getContentType() {
    return "text/plain";
  }

  /**
   * Returns the content to use in the HTTP response generated for this exception.
   *
   * Developer's note: override to provide a content different from the exception message.
   * Alternatively you can override the writeTo method for full control over the response.
   *
   * @return the content to use in the response.
   */
  public String getContent() {
    return getMessage();
  }

  /**
   * Writes this web exception in a RestxResponse.
   *
   * This implementation uses the status, contentType and content found on the exception.
   * Override it to provide custom response.
   * Note that the implementation is also responsible for logging the exception.
   *
   *
   * @param restjRequest
   * @param restjResponse the response to write to
   * @throws java.io.IOException in case of IO error.
   */
  public void writeTo(RestjRequest restjRequest, RestjResponse restjResponse) throws IOException {
    // by default log stack trace at debug level only
    if (logger.isDebugEnabled()) {
      logger.debug("request raised WebException - " + restjRequest, this);
    }

    restjResponse.setStatus(getStatus());
    restjResponse.setContentType(getContentType());
    restjResponse.getWriter().print(getContent());
  }
}
