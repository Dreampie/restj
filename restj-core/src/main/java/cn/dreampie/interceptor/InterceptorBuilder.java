package cn.dreampie.interceptor;

import java.util.HashMap;
import java.util.Map;

/**
 * InterceptorBuilder
 */
public class InterceptorBuilder {

  private Map<Class<Interceptor>, Interceptor> interceptors = new HashMap<Class<Interceptor>, Interceptor>();

  public void addToInterceptorsMap(Interceptor[] defaultInters) {
    for (Interceptor inter : defaultInters)
      interceptors.put((Class<Interceptor>) inter.getClass(), inter);
  }
}




