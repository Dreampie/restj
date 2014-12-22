package cn.dreampie.config;

import cn.dreampie.route.Const;

/**
 * The constant for Restj runtime.
 */
final public class ConstantLoader {

  private boolean devMode = false;
  private String encoding = Const.DEFAULT_ENCODING;

  public boolean isDevMode() {
    return devMode;
  }

  public void setDevMode(boolean devMode) {
    this.devMode = devMode;
  }

  public String getEncoding() {
    return encoding;
  }

  public void setEncoding(String encoding) {
    this.encoding = encoding;
  }
}







