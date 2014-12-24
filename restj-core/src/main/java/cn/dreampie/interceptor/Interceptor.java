package cn.dreampie.interceptor;


import java.lang.reflect.Method;

/**
 * Interceptor.
 */
public interface Interceptor {
  public void before(Object target, Method method, Object... params);

  public void after(Object target, Object result, Method method, Object... params);
}
