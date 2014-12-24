package cn.dreampie.security;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;

/**
 * Created by ice on 14-12-23.
 */
public interface Authenticator {

  public Optional<? extends Principal> findByName(String name);

  public ImmutableSet<Permission> loadAllPermissions();
}
