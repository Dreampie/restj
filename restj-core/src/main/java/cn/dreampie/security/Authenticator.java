package cn.dreampie.security;

import com.google.common.base.Optional;

/**
 * Created by ice on 14-12-23.
 */
public interface Authenticator {

  public Optional<? extends Principal> findByName(String name);

  public Optional<? extends Principal> authenticate(String name, String password);

}
