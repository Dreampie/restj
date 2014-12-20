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


import cn.dreampie.kit.ClassScanerKit;
import cn.dreampie.log.Logger;
import cn.dreampie.log.LoggerFactory;
import cn.dreampie.route.Controller;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Routes.
 */
public class Controllers {

  private final List<Class<? extends Controller>> controllers = Lists.newArrayList();
  private List<Class<? extends Controller>> excludeClasses = Lists.newArrayList();
  private List<Class<? extends Controller>> includeClasses = Lists.newArrayList();
  private List<String> includeClassPaths = Lists.newArrayList();
  private List<String> excludeClassPaths = Lists.newArrayList();
  private static final Logger LOGGER = LoggerFactory.getLogger(Controllers.class);

  public Controllers add(Controllers controllers) {
    if (controllers != null) {
      controllers.build();
      this.controllers.addAll(controllers.controllers);
    }
    return this;
  }


  /**
   * Add url mapping to controller. The view path is controllerKey
   *
   * @param controllerClass Controller Class
   */
  public Controllers add(Class<? extends Controller> controllerClass) {
    controllers.add(controllerClass);
    return this;
  }

  public Controllers addExcludeClasses(Class<? extends Controller>... clazzes) {
    for (Class<? extends Controller> clazz : clazzes) {
      excludeClasses.add(clazz);
    }
    return this;
  }

  public Controllers addExcludeClasses(List<Class<? extends Controller>> clazzes) {
    if (clazzes != null) {
      excludeClasses.addAll(clazzes);
    }
    return this;
  }

  public Controllers addExcludePaths(String... paths) {
    for (String path : paths) {
      excludeClassPaths.add(path);
    }
    return this;
  }

  public Controllers addIncludeClasses(Class<? extends Controller>... clazzes) {
    for (Class<? extends Controller> clazz : clazzes) {
      includeClasses.add(clazz);
    }
    return this;
  }

  public Controllers addIncludeClasses(List<Class<? extends Controller>> clazzes) {
    if (clazzes != null) {
      includeClasses.addAll(clazzes);
    }
    return this;
  }

  public Controllers addIncludePaths(String... paths) {
    for (String path : paths) {
      includeClassPaths.add(path);
    }
    return this;
  }

  public void build() {
    List<Class<? extends Controller>> controllerClasses = ClassScanerKit.of(Controller.class).includepaths(includeClassPaths).search();
    for (Class controller : controllerClasses) {
      if (excludeClasses.contains(controller)) {
        continue;
      }
      this.add(controller);
      LOGGER.info("routes.add(" + controller.getName() + ")");
    }
  }

  public List<Class<? extends Controller>> getControllers() {
    return controllers;
  }
}






