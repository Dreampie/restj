package cn.dreampie.interceptor;


import cn.dreampie.route.ResourceInvocation;

/**
 * Interceptor.
 */
public interface Interceptor {
  void intercept(ResourceInvocation ai);
}
