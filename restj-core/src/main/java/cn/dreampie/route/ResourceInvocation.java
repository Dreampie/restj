package cn.dreampie.route;

import cn.dreampie.exception.WebException;
import cn.dreampie.interceptor.Interceptor;
import cn.dreampie.route.match.ResourceMatch;
import cn.dreampie.route.match.RouteMatch;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableList;

import java.lang.reflect.InvocationTargetException;

/**
 * ActionInvocation invoke the action
 */
public class ResourceInvocation {

  private ResourceMatch resourceMatch;
  private RouteMatch routeMatch;

  // ActionInvocationWrapper need this constructor
  private ResourceInvocation() {

  }

  public ResourceInvocation(ResourceMatch resourceMatch, RouteMatch routeMatch) {
    this.resourceMatch = resourceMatch;
    this.routeMatch = routeMatch;
  }

  /**
   * Invoke the action.
   */
  public Object invoke() {
    Resource resource = null;

    try {
      resource = resourceMatch.getControllerClass().newInstance();

      Object[] args = new Object[resourceMatch.getAllParamNames().size()];
      int i = 0;
      Class paraType = null;
      ImmutableList<String> valueArr = null;
      for (String name : resourceMatch.getAllParamNames()) {
        paraType = resourceMatch.getAllParamTypes().get(i);
        if (resourceMatch.getPathParamNames().contains(name)) {
          if (paraType == String.class) {
            args[i] = routeMatch.getPathParam(name);
          } else
            args[i] = JSON.parseObject(routeMatch.getPathParam(name), paraType);
        } else {
          valueArr = routeMatch.getOtherParam(name);
          if (valueArr != null) {
            if (valueArr.size() == 1) {
              if (paraType == String.class) {
                args[i] = valueArr.get(0);
              } else
                args[i] = JSON.parseObject(valueArr.get(0), paraType);
            } else
              throw new WebException("Not Support Array Paramaters.");
          }
        }
        i++;
      }
      resourceMatch.getMethod().setAccessible(true);

      Interceptor[] interceptors = resourceMatch.getInterceptors();

      boolean hasInterceptor = interceptors != null && interceptors.length > 0;

      if (hasInterceptor) {
        for (Interceptor interceptor : interceptors) {
          interceptor.before(resource, resourceMatch.getMethod(), args);
        }
      }
      Object result = resourceMatch.getMethod().invoke(resource, args);

      if (hasInterceptor) {
        for (int j = interceptors.length - 1; j >= 0; j--) {
          interceptors[i].after(resource, result, resourceMatch.getMethod(), args);
        }
      }
      return result;
    } catch (InvocationTargetException e) {
      Throwable cause = e.getTargetException();
      if (cause instanceof RuntimeException)
        throw (RuntimeException) cause;
      throw new RuntimeException(e);
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }


}
