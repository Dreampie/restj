package cn.dreampie.security;

import com.google.common.collect.ImmutableSet;

/**
 * User: xavierhanin
 * Date: 1/30/13
 * Time: 6:30 PM
 */
public class Principal {
  public static final String SESSION_DEF_KEY = "principal";
  private String username;
  private ImmutableSet<String> roles;
  private ImmutableSet<String> premissions;

  public Principal(String username, ImmutableSet<String> roles, ImmutableSet<String> premissions) {
    this.username = username;
    this.roles = roles;
    this.premissions = premissions;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public ImmutableSet<String> getRoles() {
    return roles;
  }

  public void setRoles(ImmutableSet<String> roles) {
    this.roles = roles;
  }

  public ImmutableSet<String> getPremissions() {
    return premissions;
  }

  public void setPremissions(ImmutableSet<String> premissions) {
    this.premissions = premissions;
  }
}
