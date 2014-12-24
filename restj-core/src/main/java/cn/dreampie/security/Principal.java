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
  private String passwordHash;
  private ImmutableSet<String> premissions;


  public Principal(String username, String passwordHash, ImmutableSet<String> premissions) {
    this.username = username;
    this.passwordHash = passwordHash;
    this.premissions = premissions;
  }

  String getPasswordHash() {
    return passwordHash;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public ImmutableSet<String> getPremissions() {
    return premissions;
  }

  public void setPremissions(ImmutableSet<String> premissions) {
    this.premissions = premissions;
  }

  public boolean checkPremission(String premission) {
    return premissions.contains(premission);
  }
}
