package cn.dreampie.cors;

import cn.dreampie.handler.Handler;
import cn.dreampie.http.HttpMethod;
import cn.dreampie.http.HttpRequest;
import cn.dreampie.http.HttpResponse;
import cn.dreampie.log.Logger;
import cn.dreampie.log.LoggerFactory;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ice on 14-12-22.
 */
public class CORSHandler extends Handler {
  private static final Logger LOGGER = LoggerFactory.getLogger(CORSHandler.class);

  // Request headers
  private static final String ORIGIN_HEADER = "Origin";
  public static final String ACCESS_CONTROL_REQUEST_METHOD_HEADER = "Access-Control-Request-Method";
  public static final String ACCESS_CONTROL_REQUEST_HEADERS_HEADER = "Access-Control-Request-Headers";
  // Response headers
  public static final String ACCESS_CONTROL_ALLOW_ORIGIN_HEADER = "Access-Control-Allow-Origin";
  public static final String ACCESS_CONTROL_ALLOW_METHODS_HEADER = "Access-Control-Allow-Methods";
  public static final String ACCESS_CONTROL_ALLOW_HEADERS_HEADER = "Access-Control-Allow-Headers";
  public static final String ACCESS_CONTROL_MAX_AGE_HEADER = "Access-Control-Max-Age";
  public static final String ACCESS_CONTROL_ALLOW_CREDENTIALS_HEADER = "Access-Control-Allow-Credentials";
  public static final String ACCESS_CONTROL_EXPOSE_HEADERS_HEADER = "Access-Control-Expose-Headers";
  // Implementation constants
  private static final String ANY_ORIGIN = "*";
  private static final ImmutableList<String> SIMPLE_HTTP_METHODS = ImmutableList.of("GET", "POST", "HEAD");
  private static final ImmutableList<String> DEFAULT_ALLOWED_METHODS = ImmutableList.of("GET", "POST", "HEAD");
  private static final ImmutableList<String> DEFAULT_ALLOWED_HEADERS = ImmutableList.of("X-Requested-With", "Content-Type", "Accept", "Origin");

  private boolean anyOriginAllowed;
  private boolean anyHeadersAllowed;
  private ImmutableList<String> allowedOrigins;
  private ImmutableList<String> allowedMethods;
  private ImmutableList<String> allowedHeaders;
  private ImmutableList<String> exposedHeaders;
  private int preflightMaxAge;
  private boolean allowCredentials;
  private boolean chainPreflight;

  public CORSHandler(CORSConst corsConst) {

    String allowedOriginsConfig = corsConst.getAllowedOrigins();
    if (allowedOriginsConfig == null) {
      allowedOriginsConfig = "*";
    }

    ImmutableList.Builder<String> allowedOriginsBuilder = ImmutableList.builder();
    String[] allowedOrigins = allowedOriginsConfig.split(",");
    for (String allowedOrigin : allowedOrigins) {
      allowedOrigin = allowedOrigin.trim();
      if (allowedOrigin.length() > 0) {
        if (ANY_ORIGIN.equals(allowedOrigin)) {
          anyOriginAllowed = true;
          break;
        } else {
          allowedOriginsBuilder.add(allowedOrigin);
        }
      }
    }
    this.allowedOrigins = allowedOriginsBuilder.build();

    ImmutableList.Builder<String> allowedMethodsBuilder = ImmutableList.builder();
    String allowedMethodsConfig = corsConst.getAllowedMethods();
    if (allowedMethodsConfig == null)
      allowedMethodsBuilder.addAll(DEFAULT_ALLOWED_METHODS);
    else
      allowedMethodsBuilder.add(allowedMethodsConfig.split(","));
    this.allowedMethods = allowedMethodsBuilder.build();


    ImmutableList.Builder<String> allowedHeadersBuilder = ImmutableList.builder();
    String allowedHeadersConfig = corsConst.getAllowedHeaders();
    if (allowedHeadersConfig == null)
      allowedHeadersBuilder.addAll(DEFAULT_ALLOWED_HEADERS);
    else if ("*".equals(allowedHeadersConfig))
      anyHeadersAllowed = true;
    else
      allowedHeadersBuilder.add(allowedHeadersConfig.split(","));
    this.allowedHeaders = allowedHeadersBuilder.build();

    preflightMaxAge = corsConst.getPreflightMaxAge();

    allowCredentials = corsConst.isAllowCredentials();

    ImmutableList.Builder<String> exposedHeadersBuilder = ImmutableList.builder();
    String exposedHeadersConfig = corsConst.getExposedHeaders();
    if (exposedHeadersConfig == null)
      exposedHeadersConfig = "";
    exposedHeadersBuilder.add(exposedHeadersConfig.split(","));
    this.exposedHeaders = exposedHeadersBuilder.build();


    chainPreflight = corsConst.isChainPreflight();

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Cross-origin filter configuration: " +
              "allowedCredentials = " + allowedOrigins + ", " +
              "allowedMethods = " + allowedMethods + ", " +
              "allowedHeaders = " + allowedHeaders + ", " +
              "preflightMaxAge = " + preflightMaxAge + ", " +
              "allowCredentials = " + allowCredentials + "," +
              "exposedHeaders = " + exposedHeaders + "," +
              "chainPreflight = " + chainPreflight
      );
    }
  }


  public final void handle(HttpRequest request, HttpResponse response, boolean[] isHandled) {
    Optional<String> origin = request.getHeader(ORIGIN_HEADER);
    // Is it a cross origin request ?
    if (origin.isPresent() && isEnabled(request)) {
      if (originMatches(origin.get())) {
        if (isSimpleRequest(request)) {
          LOGGER.debug("Cross-origin request to {} is a simple cross-origin request", request.getRestPath());
          handleSimpleResponse(request, response, origin.get());
        } else if (isPreflightRequest(request)) {
          LOGGER.debug("Cross-origin request to {} is a preflight cross-origin request", request.getRestPath());
          handlePreflightResponse(request, response, origin.get());
          if (chainPreflight)
            LOGGER.debug("Preflight cross-origin request to {} forwarded to application", request.getRestPath());
          else
            return;
        } else {
          LOGGER.debug("Cross-origin request to {} is a non-simple cross-origin request", request.getRestPath());
          handleSimpleResponse(request, response, origin.get());
        }
      } else {
        LOGGER.debug("Cross-origin request to " + request.getRestPath() + " with origin " + origin + " does not match allowed origins " + allowedOrigins);
      }
    }
    nextHandler.handle(request, response, isHandled);
  }

  protected boolean isEnabled(HttpRequest request) {
    // WebSocket clients such as Chrome 5 implement a version of the WebSocket
    // protocol that does not accept extra response headers on the upgrade response
    for (Enumeration connections = request.getHeaders("Connection"); connections.hasMoreElements(); ) {
      String connection = (String) connections.nextElement();
      if ("Upgrade".equalsIgnoreCase(connection)) {
        for (Enumeration upgrades = request.getHeaders("Upgrade"); upgrades.hasMoreElements(); ) {
          String upgrade = (String) upgrades.nextElement();
          if ("WebSocket".equalsIgnoreCase(upgrade))
            return false;
        }
      }
    }
    return true;
  }

  private boolean originMatches(String originList) {
    if (anyOriginAllowed)
      return true;

    if (originList.trim().length() == 0)
      return false;

    String[] origins = originList.split(" ");
    for (String origin : origins) {
      if (origin.trim().length() == 0)
        continue;

      for (String allowedOrigin : allowedOrigins) {
        if (allowedOrigin.contains("*")) {
          Matcher matcher = createMatcher(origin, allowedOrigin);
          if (matcher.matches())
            return true;
        } else if (allowedOrigin.equals(origin)) {
          return true;
        }
      }
    }
    return false;
  }

  private Matcher createMatcher(String origin, String allowedOrigin) {
    String regex = parseAllowedWildcardOriginToRegex(allowedOrigin);
    Pattern pattern = Pattern.compile(regex);
    return pattern.matcher(origin);
  }

  private String parseAllowedWildcardOriginToRegex(String allowedOrigin) {
    String regex = allowedOrigin.replace(".", "\\.");
    return regex.replace("*", ".*"); // we want to be greedy here to match multiple subdomains, thus we use .*
  }

  private boolean isSimpleRequest(HttpRequest request) {
    HttpMethod method = request.getHttpMethod();
    if (SIMPLE_HTTP_METHODS.contains(method.value())) {
      // TODO: implement better detection of simple headers
      // The specification says that for a request to be simple, custom request headers must be simple.
      // Here for simplicity I just check if there is a Access-Control-Request-Method header,
      // which is required for preflight requests
      return request.getHeader(ACCESS_CONTROL_REQUEST_METHOD_HEADER) == null;
    }
    return false;
  }

  private boolean isPreflightRequest(HttpRequest request) {
    HttpMethod method = request.getHttpMethod();
    if (HttpMethod.OPTIONS != method)
      return false;
    if (request.getHeader(ACCESS_CONTROL_REQUEST_METHOD_HEADER) == null)
      return false;
    return true;
  }

  private void handleSimpleResponse(HttpRequest request, HttpResponse response, String origin) {
    response.setHeader(ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, origin);
    //W3C CORS spec http://www.w3.org/TR/cors/#resource-implementation
    if (!anyOriginAllowed)
      response.addHeader("Vary", ORIGIN_HEADER);
    if (allowCredentials)
      response.setHeader(ACCESS_CONTROL_ALLOW_CREDENTIALS_HEADER, "true");
    if (!exposedHeaders.isEmpty())
      response.setHeader(ACCESS_CONTROL_EXPOSE_HEADERS_HEADER, Joiner.on(",").join(exposedHeaders));
  }

  private void handlePreflightResponse(HttpRequest request, HttpResponse response, String origin) {
    boolean methodAllowed = isMethodAllowed(request);

    if (!methodAllowed)
      return;
    ImmutableList<String> headersRequested = getAccessControlRequestHeaders(request);
    boolean headersAllowed = areHeadersAllowed(headersRequested);
    if (!headersAllowed)
      return;
    response.setHeader(ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, origin);
    //W3C CORS spec http://www.w3.org/TR/cors/#resource-implementation
    if (!anyOriginAllowed)
      response.addHeader("Vary", ORIGIN_HEADER);
    if (allowCredentials)
      response.setHeader(ACCESS_CONTROL_ALLOW_CREDENTIALS_HEADER, "true");
    if (preflightMaxAge > 0)
      response.setHeader(ACCESS_CONTROL_MAX_AGE_HEADER, String.valueOf(preflightMaxAge));
    response.setHeader(ACCESS_CONTROL_ALLOW_METHODS_HEADER, Joiner.on(",").join(allowedMethods));
    if (anyHeadersAllowed)
      response.setHeader(ACCESS_CONTROL_ALLOW_HEADERS_HEADER, Joiner.on(",").join(headersRequested));
    else
      response.setHeader(ACCESS_CONTROL_ALLOW_HEADERS_HEADER, Joiner.on(",").join(allowedHeaders));
  }

  private boolean isMethodAllowed(HttpRequest request) {
    Optional<String> accessControlRequestMethod = request.getHeader(ACCESS_CONTROL_REQUEST_METHOD_HEADER);
    LOGGER.debug("{} is {}", ACCESS_CONTROL_REQUEST_METHOD_HEADER, accessControlRequestMethod);
    boolean result = false;
    if (!accessControlRequestMethod.isPresent())
      result = allowedMethods.contains(accessControlRequestMethod.get());
    LOGGER.debug("Method {} is" + (result ? "" : " not") + " among allowed methods {}", accessControlRequestMethod, allowedMethods);
    return result;
  }

  ImmutableList<String> getAccessControlRequestHeaders(HttpRequest request) {
    Optional<String> accessControlRequestHeaders = request.getHeader(ACCESS_CONTROL_REQUEST_HEADERS_HEADER);
    LOGGER.debug("{} is {}", ACCESS_CONTROL_REQUEST_HEADERS_HEADER, accessControlRequestHeaders);
    if (!accessControlRequestHeaders.isPresent())
      return ImmutableList.of();

    ImmutableList.Builder<String> requestedHeaders = ImmutableList.builder();
    String[] headers = accessControlRequestHeaders.get().split(",");
    for (String header : headers) {
      String h = header.trim();
      if (h.length() > 0)
        requestedHeaders.add(h);
    }
    return requestedHeaders.build();
  }


  private boolean areHeadersAllowed(ImmutableList<String> requestedHeaders) {
    if (anyHeadersAllowed) {
      LOGGER.debug("Any header is allowed");
      return true;
    }

    boolean result = true;
    for (String requestedHeader : requestedHeaders) {
      boolean headerAllowed = false;
      for (String allowedHeader : allowedHeaders) {
        if (requestedHeader.equalsIgnoreCase(allowedHeader.trim())) {
          headerAllowed = true;
          break;
        }
      }
      if (!headerAllowed) {
        result = false;
        break;
      }
    }
    LOGGER.debug("Headers [{}] are" + (result ? "" : " not") + " among allowed headers {}", requestedHeaders, allowedHeaders);
    return result;
  }

}
