package cn.dreampie.security;


import cn.dreampie.exception.WebException;
import cn.dreampie.http.HttpStatus;
import cn.dreampie.log.Logger;
import cn.dreampie.log.LoggerFactory;
import cn.dreampie.security.matcher.AntPathMatcher;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableSet;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

/**
 * ActionHandler
 */
public final class PermissionChecker {

  public static final String PERMISSION_DEF_KEY = "permission";

  private final AntPathMatcher antPathMatcher = new AntPathMatcher();

  private final Authenticator authenticator;

  private final Cache<String, ImmutableSet<Permission>> cache = CacheBuilder.newBuilder().maximumSize(1000).build();


  public PermissionChecker(Authenticator authenticator) {
    this.authenticator = authenticator;
  }

  /**
   * handle
   * 1: Action action = actionMapping.getAction(target)
   * 2: new ActionInvocation(...).invoke()
   * 3: render(...)
   */
  public  final void check(String url) {
    ImmutableSet<Permission> permissions;
    try {
      permissions = cache.get(PERMISSION_DEF_KEY, new Callable<ImmutableSet<Permission>>() {
        public ImmutableSet<Permission> call() throws Exception {
          return authenticator.loadAllPermissions();
        }
      });
    } catch (ExecutionException e) {
      throw new WebException(e.getMessage());
    }

    if (!SubjectKit.getPrincipal().isPresent()) {
      throw new WebException(HttpStatus.UNAUTHORIZED);
    }

    if (permissions != null) {
      for (Permission permission : permissions) {
        if (antPathMatcher.match(permission.getPattern(), url)) {
          if (!SubjectKit.checkPermission(permission.getPermission())) {
            throw new WebException(HttpStatus.FORBIDDEN);
          }
        }
      }
    }
  }


}





