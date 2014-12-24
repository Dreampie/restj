package cn.dreampie;

import cn.dreampie.kit.EncryptionKit;
import cn.dreampie.security.AuthenticationInfo;
import cn.dreampie.security.Authenticator;
import cn.dreampie.security.Principal;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;

/**
 * Created by ice on 14-12-23.
 */
public class AuthenticatorImpl implements Authenticator {
  public Optional<? extends Principal> findByName(String name) {
    return Optional.fromNullable(new Principal(name, EncryptionKit.sha512Encrypt("b"), ImmutableSet.<String>of(), ImmutableSet.<String>of()));
  }

}
