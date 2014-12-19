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

import cn.dreampie.annotation.http.DELETE;
import cn.dreampie.annotation.http.GET;
import cn.dreampie.annotation.http.POST;
import cn.dreampie.annotation.http.PUT;
import cn.dreampie.config.Interceptors;
import cn.dreampie.config.Routes;
import cn.dreampie.interceptor.Interceptor;
import cn.dreampie.interceptor.InterceptorBuilder;

import java.lang.reflect.Method;
import java.util.*;
import java.util.Map.Entry;

/**
 * ActionMapping
 */
final class ActionMapping {

  private static final String SLASH = "/";
  private Routes routes;
  private Interceptors interceptors;

  private final Map<String, Action> actionMappings = new HashMap<String, Action>();

  ActionMapping(Routes routes, Interceptors interceptors) {
    this.routes = routes;
    this.interceptors = interceptors;
  }

  private Set<String> buildExcludedMethodName() {
    Set<String> excludedMethodName = new HashSet<String>();
    Method[] methods = Controller.class.getMethods();
    for (Method m : methods) {
      if (m.getParameterTypes().length == 0)
        excludedMethodName.add(m.getName());
    }
    return excludedMethodName;
  }

  void buildActionMapping() {
    actionMappings.clear();
    Set<String> excludedMethodName = buildExcludedMethodName();
    InterceptorBuilder interceptorBuilder = new InterceptorBuilder();
    Interceptor[] defaultInters = interceptors.getInterceptorArray();
    interceptorBuilder.addToInterceptorsMap(defaultInters);

    DELETE delete = null;
    GET get = null;
    POST post = null;
    PUT put = null;
    String url = null;
    //addResources
    for (Class<? extends Controller> controllerClazz : routes.getControllers()) {

      Method[] methods = controllerClazz.getMethods();
      for (Method method : methods) {
        delete = method.getAnnotation(DELETE.class);

        if (delete != null) {
          url = delete.value();

        }
        Action action = new Action(url, controllerClazz, method);
        get = method.getAnnotation(GET.class);
        post = method.getAnnotation(POST.class);
        put = method.getAnnotation(PUT.class);


      }
    }

    for (Entry<String, Class<? extends Controller>> entry : routes.getEntrySet()) {
      Class<? extends Controller> controllerClass = entry.getValue();
      Method[] methods = controllerClass.getMethods();
      for (Method method : methods) {
        String methodName = method.getName();
        if (!excludedMethodName.contains(methodName) && method.getParameterTypes().length == 0) {
          String controllerKey = entry.getKey();

          ActionKey ak = method.getAnnotation(ActionKey.class);
          if (ak != null) {
            String actionKey = ak.value().trim();
            if ("".equals(actionKey))
              throw new IllegalArgumentException(controllerClass.getName() + "." + methodName + "(): The argument of ActionKey can not be blank.");

            if (!actionKey.startsWith(SLASH))
              actionKey = SLASH + actionKey;

            if (actionMappings.containsKey(actionKey)) {
              warnning(actionKey, controllerClass, method);
              continue;
            }

            Action action = new Action(controllerKey, actionKey, controllerClass, method, methodName, routes.getViewPath(controllerKey));
            actionMappings.put(actionKey, action);
          } else if (methodName.equals("index")) {
            String actionKey = controllerKey;

            Action action = new Action(controllerKey, actionKey, controllerClass, method, methodName, routes.getViewPath(controllerKey));
            action = actionMappings.put(actionKey, action);

            if (action != null) {
              warnning(action.getUrl(), action.getControllerClass(), action.getMethod());
            }
          } else {
            String actionKey = controllerKey.equals(SLASH) ? SLASH + methodName : controllerKey + SLASH + methodName;

            if (actionMappings.containsKey(actionKey)) {
              warnning(actionKey, controllerClass, method);
              continue;
            }

            Action action = new Action(controllerKey, actionKey, controllerClass, method, methodName, routes.getViewPath(controllerKey));
            actionMappings.put(actionKey, action);
          }
        }
      }
    }

    // support url = controllerKey + urlParas with "/" of controllerKey
    Action actoin = actionMappings.get("/");
    if (actoin != null)
      actionMappings.put("", actoin);
  }

  private static final void warnning(String actionKey, Class<? extends Controller> controllerClass, Method method) {
    StringBuilder sb = new StringBuilder();
    sb.append("--------------------------------------------------------------------------------\nWarnning!!!\n")
        .append("ActionKey already used: \"").append(actionKey).append("\" \n")
        .append("Action can not be mapped: \"")
        .append(controllerClass.getName()).append(".").append(method.getName()).append("()\" \n")
        .append("--------------------------------------------------------------------------------");
    System.out.println(sb.toString());
  }

  /**
   * Support four types of url
   * 1: http://abc.com/controllerKey                 ---> 00
   * 2: http://abc.com/controllerKey/para            ---> 01
   * 3: http://abc.com/controllerKey/method          ---> 10
   * 4: http://abc.com/controllerKey/method/para     ---> 11
   */
  Action getAction(String url, String[] urlPara) {
    Action action = actionMappings.get(url);
    if (action != null) {
      return action;
    }

    // --------
    int i = url.lastIndexOf(SLASH);
    if (i != -1) {
      action = actionMappings.get(url.substring(0, i));
      urlPara[0] = url.substring(i + 1);
    }

    return action;
  }

  List<String> getAllActionKeys() {
    List<String> allActionKeys = new ArrayList<String>(actionMappings.keySet());
    Collections.sort(allActionKeys);
    return allActionKeys;
  }
}





