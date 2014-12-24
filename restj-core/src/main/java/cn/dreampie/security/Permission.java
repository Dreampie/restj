package cn.dreampie.security;

/**
 * Created by ice on 14-12-24.
 */
public class Permission {

  public static final String PERMISSION_DEF_KEY="permission";
  private String method;
  private String pattern;
  private String permission;

  public Permission(String method, String pattern, String permission) {
    this.method = method;
    this.pattern = pattern;
    this.permission = permission;
  }

  public String getMethod() {
    return method;
  }

  public void setMethod(String method) {
    this.method = method;
  }

  public String getPattern() {
    return pattern;
  }

  public void setPattern(String pattern) {
    this.pattern = pattern;
  }

  public String getPermission() {
    return permission;
  }

  public void setPermission(String permission) {
    this.permission = permission;
  }
}
