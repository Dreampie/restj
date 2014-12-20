/**
 * Copyright (c) 2011-2015, James Zhan 詹波 (jfinal@126.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.dreampie.route;

import cn.dreampie.exception.WebException;
import cn.dreampie.http.HttpStatus;
import cn.dreampie.interceptor.Interceptor;
import cn.dreampie.route.match.RouteMatch;
import cn.dreampie.route.match.RouterMatch;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableList;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

/**
 * ActionInvocation invoke the action
 */
public class RouterInvocation {

  private Interceptor[] inters;
  private RouterMatch routerMatch;
  private RouteMatch routeMatch;
  private int index = 0;

  private static final Object[] NULL_ARGS = new Object[0];  // Prevent new Object[0] by jvm for paras of action invocation.

  // ActionInvocationWrapper need this constructor
  private RouterInvocation() {

  }

  RouterInvocation(RouterMatch routerMatch, RouteMatch routeMatch) {
    this.routerMatch = routerMatch;
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
      Controller controller = null;

      try {
        controller = routerMatch.getControllerClass().newInstance();
        Object[] args = new Object[routerMatch.getAllParamNames().size()];
        int i = 0;
        Class paraType = null;
        ImmutableList<String> valueArr = null;
        for (String name : routerMatch.getAllParamNames()) {
          paraType = routerMatch.getAllParamTypes().get(i);
          if (routerMatch.getPathParamNames().contains(name)) {
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
        routerMatch.getMethod().setAccessible(true);
        return routerMatch.getMethod().invoke(controller, args);
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
