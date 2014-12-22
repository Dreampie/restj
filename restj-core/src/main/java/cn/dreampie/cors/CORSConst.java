package cn.dreampie.cors;

import com.google.common.collect.ImmutableList;

/**
 * Created by wangrenhui on 14/12/22.
 */
public class CORSConst {
  private String allowedOrigins = "*";
  private String allowedMethods = "GET,POST,HEAD";
  private String allowedHeaders = "GET,POST,HEAD";
  private String exposedHeaders = "X-Requested-With,Content-Type,Accept,Origin";
  private int preflightMaxAge = 1800;
  private boolean allowCredentials = true;
  private boolean chainPreflight = true;


  public String getAllowedOrigins() {
    return allowedOrigins;
  }

  public void setAllowedOrigins(String allowedOrigins) {
    this.allowedOrigins = allowedOrigins;
  }

  public String getAllowedMethods() {
    return allowedMethods;
  }

  public void setAllowedMethods(String allowedMethods) {
    this.allowedMethods = allowedMethods;
  }

  public String getAllowedHeaders() {
    return allowedHeaders;
  }

  public void setAllowedHeaders(String allowedHeaders) {
    this.allowedHeaders = allowedHeaders;
  }

  public String getExposedHeaders() {
    return exposedHeaders;
  }

  public void setExposedHeaders(String exposedHeaders) {
    this.exposedHeaders = exposedHeaders;
  }

  public int getPreflightMaxAge() {
    return preflightMaxAge;
  }

  public void setPreflightMaxAge(int preflightMaxAge) {
    this.preflightMaxAge = preflightMaxAge;
  }

  public boolean isAllowCredentials() {
    return allowCredentials;
  }

  public void setAllowCredentials(boolean allowCredentials) {
    this.allowCredentials = allowCredentials;
  }

  public boolean isChainPreflight() {
    return chainPreflight;
  }

  public void setChainPreflight(boolean chainPreflight) {
    this.chainPreflight = chainPreflight;
  }
}
