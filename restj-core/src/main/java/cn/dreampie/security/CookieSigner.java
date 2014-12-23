package cn.dreampie.security;

import cn.dreampie.kit.CryptoKit;
import com.google.common.base.Optional;

/**
 * Default cookie signer, using HMAC-SHA1 algorithm to sign the cookie.
 *
 * @author apeyrard
 */
public class CookieSigner implements Signer {
  private final SignatureKey signatureKey;

  public CookieSigner(Optional<SignatureKey> signatureKey) {
    this.signatureKey = signatureKey.or(SignatureKey.DEFAULT);
  }

  @Override
  public String sign(String cookie) {
    return CryptoKit.sign(cookie, signatureKey.getKey());
  }

  @Override
  public boolean verify(String cookie, String signedCookie) {
    return sign(cookie).equals(signedCookie);
  }
}
