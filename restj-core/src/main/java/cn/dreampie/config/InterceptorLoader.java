package cn.dreampie.config;

import cn.dreampie.interceptor.Interceptor;

import java.util.ArrayList;
import java.util.List;

/**
 * The interceptors applied to all actions.
 */
final public class InterceptorLoader {

  private final List<Interceptor> interceptorList = new ArrayList<Interceptor>();

  public InterceptorLoader add(Interceptor globalInterceptor) {
    if (globalInterceptor != null)
      this.interceptorList.add(globalInterceptor);
    return this;
  }

  public Interceptor[] getInterceptorArray() {
    Interceptor[] result = interceptorList.toArray(new Interceptor[interceptorList.size()]);
    return result;
  }
}
