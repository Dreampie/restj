package cn.dreampie.route.match;

import com.google.common.collect.ImmutableMap;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by ice on 14-12-19.
 */
public class StdRestjRequestMatch implements RestjRequestMatch {
  private final String pattern;
  private final String path;
  private final ImmutableMap<String, String> pathParams;
  private final ImmutableMap<String, ? extends Object> otherParams;

  public StdRestjRequestMatch(String path) {
    this(path, path, ImmutableMap.<String, String>of());
  }

  public StdRestjRequestMatch(String pattern, String path) {
    this(pattern, path, ImmutableMap.<String, String>of());
  }

  public StdRestjRequestMatch(String pattern, String path, ImmutableMap<String, String> pathParams) {
    this(pattern, path, pathParams, ImmutableMap.<String, Object>of());
  }

  public StdRestjRequestMatch(String pattern, String path,
                              ImmutableMap<String, String> pathParams,
                              ImmutableMap<String, ? extends Object> otherParams) {
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

  public ImmutableMap<String, ? extends Object> getOtherParams() {
    return otherParams;
  }


  public String toString() {
    return "StdRestxHandlerMatch{" +
        "pattern='" + pattern + '\'' +
        ", path='" + path + '\'' +
        ", pathParams=" + pathParams +
        ", otherParams=" + otherParams +
        '}';
  }
}
