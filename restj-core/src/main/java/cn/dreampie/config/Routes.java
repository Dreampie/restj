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

package cn.dreampie.config;


import cn.dreampie.route.Controller;
import com.google.common.collect.Lists;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Routes.
 */
public class Routes {

  private final List<Class<? extends Controller>> controllers = Lists.newArrayList();

  public Routes add(Routes routes) {
    if (routes != null) {
      routes.build();
      controllers.addAll(routes.controllers);
    }
    return this;
  }

  public void build() {
    throw new RuntimeException("Not build action.");
  }


  /**
   * Add url mapping to controller. The view path is controllerKey
   *
   * @param controllerClass Controller Class
   */
  public Routes add(Class<? extends Controller> controllerClass) {
    controllers.add(controllerClass);
    return this;
  }

  public List<Class<? extends Controller>> getControllers() {
    return controllers;
  }
}






