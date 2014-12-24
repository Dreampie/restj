package cn.dreampie.security;

import cn.dreampie.kit.EncryptionKit;

/**
 * Created by ice on 14-12-23.
 */
public class DefaultPasswordService implements PasswordService {

  public String hash(String password) {
    return EncryptionKit.sha512Encrypt(password);
  }

  public boolean match(String password, String passwordHash) {
    return hash(password).equals(passwordHash);
  }
}
