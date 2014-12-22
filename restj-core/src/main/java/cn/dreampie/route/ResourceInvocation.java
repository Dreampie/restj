package cn.dreampie.route;

import cn.dreampie.exception.WebException;
import cn.dreampie.http.HttpStatus;
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

  private Interceptor[] inters;
  private ResourceMatch resourceMatch;
  private RouteMatch routeMatch;
  private int index = 0;

  private static final Object[] NULL_ARGS = new Object[0];  // Prevent new Object[0] by jvm for paras of action invocation.

  // ActionInvocationWrapper need this constructor
  private ResourceInvocation() {

  }

  public ResourceInvocation(ResourceMatch resourceMatch, RouteMatch routeMatch) {
    this.resourceMatch = resourceMatch;
    this.routeMatch = routeMatch;
    inters = new Interceptor[0];
  }

  /**
   * Invoke the action.
   */
  public Object invoke() {
    if (index < inters.length)
      inters[index++].intercept(this);
    else if (index++ == inters.length) {
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
        return resourceMatch.getMethod().invoke(resource, args);
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
    throw new WebException(HttpStatus.NO_CONTENT);

  }


}
