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

import cn.dreampie.annotation.http.*;
import cn.dreampie.config.Controllers;
import cn.dreampie.config.Interceptors;
import cn.dreampie.http.HttpMethod;
import cn.dreampie.http.Request;
import cn.dreampie.interceptor.Interceptor;
import cn.dreampie.interceptor.InterceptorBuilder;
import cn.dreampie.route.match.RouteMatch;
import cn.dreampie.route.match.RouterMatch;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.List;

/**
 * ActionMapping
 */
public final class RouterBuilder {

  private Controllers controllers;
  private Interceptors interceptors;

  private ImmutableList<RouterMatch> routerMatches;

  RouterBuilder(Controllers controllers, Interceptors interceptors) {
    this.controllers = controllers;
    this.interceptors = interceptors;
  }

  void build() {
    InterceptorBuilder interceptorBuilder = new InterceptorBuilder();
    Interceptor[] defaultInters = interceptors.getInterceptorArray();
    interceptorBuilder.addToInterceptorsMap(defaultInters);

    ImmutableList.Builder<RouterMatch> matchBuilder = ImmutableList.builder();

    DELETE delete = null;
    GET get = null;
    POST post = null;
    PUT put = null;
    HEAD head = null;
    PATCH patch = null;
    //addResources
    for (Class<? extends Controller> controllerClazz : controllers.getControllers()) {

      Method[] methods = controllerClazz.getMethods();
      for (Method method : methods) {
        delete = method.getAnnotation(DELETE.class);
        if (delete != null) {
          matchBuilder.add(new RouterMatch(controllerClazz, HttpMethod.DELETE, delete.value(), method));
          continue;
        }

        get = method.getAnnotation(GET.class);
        if (get != null) {
          matchBuilder.add(new RouterMatch(controllerClazz, HttpMethod.GET, get.value(), method));
          continue;
        }

        post = method.getAnnotation(POST.class);
        if (post != null) {
          matchBuilder.add(new RouterMatch(controllerClazz, HttpMethod.POST, post.value(), method));
          continue;
        }

        put = method.getAnnotation(PUT.class);
        if (put != null) {
          matchBuilder.add(new RouterMatch(controllerClazz, HttpMethod.PUT, put.value(), method));
          continue;
        }

        head = method.getAnnotation(HEAD.class);
        if (put != null) {
          matchBuilder.add(new RouterMatch(controllerClazz, HttpMethod.HEAD, head.value(), method));
          continue;
        }

        patch = method.getAnnotation(PATCH.class);
        if (put != null) {
          matchBuilder.add(new RouterMatch(controllerClazz, HttpMethod.PATCH, patch.value(), method));
          continue;
        }
      }
    }
    routerMatches = matchBuilder.build();
  }

  List<RouterMatch> getRouterMatches() {
    return routerMatches;
  }
}





