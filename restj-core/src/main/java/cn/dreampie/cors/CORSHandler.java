package cn.dreampie.cors;

import cn.dreampie.handler.Handler;
import cn.dreampie.http.HttpMethod;
import cn.dreampie.http.HttpStatus;
import cn.dreampie.http.Request;
import cn.dreampie.http.Response;
import cn.dreampie.log.Logger;
import cn.dreampie.log.LoggerFactory;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.Collection;
import java.util.HashSet;

/**
 * Created by ice on 14-12-22.
 */
public class CORSHandler extends Handler {
  private static final Logger LOGGER = LoggerFactory.getLogger(CORSHandler.class);

  /**
   * A {@link java.util.Collection} of origins consisting of zero or more origins that
   * are allowed access to the resource.
   */
  private final ImmutableList<String> allowedOrigins;

  /**
   * Determines if any origin is allowed to make request.
   */
  private boolean anyOriginAllowed;

  /**
   * A {@link java.util.Collection} of methods consisting of zero or more methods that
   * are supported by the resource.
   */
  private final ImmutableList<String> allowedHttpMethods;

  /**
   * A {@link java.util.Collection} of headers consisting of zero or more header field
   * names that are supported by the resource.
   */
  private final ImmutableList<String> allowedHttpHeaders;

  /**
   * A {@link java.util.Collection} of exposed headers consisting of zero or more header
   * field names of headers other than the simple response headers that the
   * resource might use and can be exposed.
   */
  private final ImmutableList<String> exposedHeaders;

  /**
   * A supports credentials flag that indicates whether the resource supports
   * user credentials in the request. It is true when the resource does and
   * false otherwise.
   */
  private boolean supportsCredentials;

  /**
   * Indicates (in seconds) how long the results of a pre-flight request can
   * be cached in a pre-flight result cache.
   */
  private long preflightMaxAge;

  /**
   * Controls access log logging.
   */
  private boolean loggingEnabled;

  /**
   * Determines if the request should be decorated or not.
   */
  private boolean decorateRequest;

  CORSHandler() {
    this.allowedOrigins = ImmutableList.of();
    this.allowedHttpMethods = ImmutableList.of();
    this.allowedHttpHeaders = ImmutableList.of();
    this.exposedHeaders = ImmutableList.of();
  }


  public void handle(String target, Request request, Response response, boolean[] isHandled) {
    Optional<String> origin = request.getHeader("Origin");
    if (origin.isPresent()) {
      response.setHeader("Access-Control-Allow-Origin", origin.get());
      response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
      response.setHeader("Access-Control-Allow-Credentials", Boolean.TRUE.toString());
      response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");

      if (HttpMethod.OPTIONS.equals(request.getHttpMethod())) {
        response.setStatus(HttpStatus.OK);
      } else {
        ctx.nextHandlerMatch().handle(req, resp, ctx);
      }
    } else {
      ctx.nextHandlerMatch().handle(req, resp, ctx);
    }
  }
}
