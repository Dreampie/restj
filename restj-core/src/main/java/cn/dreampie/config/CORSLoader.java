package cn.dreampie.config;

import cn.dreampie.cors.CORSConst;

/**
 * The constant for Restj runtime.
 */
final public class CORSLoader {

  private static CORSConst corsConst = new CORSConst();

  public CORSConst getCorsConst() {
    return corsConst;
  }

  public void setCorsConst(CORSConst corsConst) {
    this.corsConst = corsConst;
  }

  public String getAllowedOrigins() {
    return corsConst.getAllowedOrigins();
  }

  public void setAllowedOrigins(String allowedOrigins) {
    corsConst.setAllowedOrigins(allowedOrigins);
  }

  public String getAllowedMethods() {
    return corsConst.getAllowedMethods();
  }

  public void setAllowedMethods(String allowedMethods) {
    corsConst.setAllowedMethods(allowedMethods);
  }

  public String getAllowedHeaders() {
    return corsConst.getAllowedHeaders();
  }

  public void setAllowedHeaders(String allowedHeaders) {
    corsConst.setAllowedHeaders(allowedHeaders);
  }

  public String getExposedHeaders() {
    return corsConst.getExposedHeaders();
  }

  public void setExposedHeaders(String exposedHeaders) {
    corsConst.setExposedHeaders(exposedHeaders);
  }

  public int getPreflightMaxAge() {
    return corsConst.getPreflightMaxAge();
  }

  public void setPreflightMaxAge(int preflightMaxAge) {
    corsConst.setPreflightMaxAge(preflightMaxAge);
  }

  public boolean isAllowCredentials() {
    return corsConst.isAllowCredentials();
  }

  public void setAllowCredentials(boolean allowCredentials) {
    corsConst.setAllowCredentials(allowCredentials);
  }

  public boolean isChainPreflight() {
    return corsConst.isChainPreflight();
  }

  public void setChainPreflight(boolean chainPreflight) {
    corsConst.setChainPreflight(chainPreflight);
  }
}







