package cn.dreampie.route.match;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by ice on 14-12-19.
 */
public class RouteMatch {
  private final String pattern;
  private final String path;
  private final ImmutableMap<String, String> pathParams;
  private final ImmutableMap<String, ImmutableList<String>> otherParams;

  public RouteMatch(String path) {
    this(path, path, ImmutableMap.<String, String>of());
  }

  public RouteMatch(String pattern, String path) {
    this(pattern, path, ImmutableMap.<String, String>of());
  }

  public RouteMatch(String pattern, String path, ImmutableMap<String, String> pathParams) {
    this(pattern, path, pathParams, ImmutableMap.<String, ImmutableList<String>>of());
  }

  public RouteMatch(String pattern, String path,
                    ImmutableMap<String, String> pathParams,
                    ImmutableMap<String, ImmutableList<String>> otherParams) {
    this.pattern = checkNotNull(pattern);
    this.path = checkNotNull(path);
    this.pathParams = checkNotNull(pathParams);
    this.otherParams = checkNotNull(otherParams);
  }

  public String getPath() {
    return path;
  }

  public String getPathParam(String paramName) {
    String v = pathParams.get(paramName);
    if (v == null) {
      throw new IllegalStateException(
          String.format("path parameter %s was not found", paramName));
    }
    return v;
  }

  public ImmutableMap<String, String> getPathParams() {
    return pathParams;
  }

  public ImmutableMap<String, ImmutableList<String>> getOtherParams() {
    return otherParams;
  }

  public ImmutableList<String> getOtherParam(String name) {
    return otherParams.get(name);
  }


  public String toString() {
    return "StdRestjHandlerMatch{" +
        "pattern='" + pattern + '\'' +
        ", path='" + path + '\'' +
        ", pathParams=" + pathParams +
        ", otherParams=" + otherParams +
        '}';
  }
}
