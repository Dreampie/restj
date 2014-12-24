package cn.dreampie.security;

import com.google.common.base.Optional;

/**
 * Created by ice on 14-12-23.
 */
public interface PasswordService {

  public String hash(String password);

  public boolean match(String password, String passwordHash);
}
