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


import java.lang.reflect.Method;

/**
 * Action
 */
class Action {

  private final Class<? extends Controller> controllerClass;
  private final String url;
  private final Method method;

  public Action(String url, Class<? extends Controller> controllerClass, Method method) {
    this.url = url;
    this.controllerClass = controllerClass;
    this.method = method;
  }

  public Class<? extends Controller> getControllerClass() {
    return controllerClass;
  }

  public String getUrl() {
    return url;
  }

  public Method getMethod() {
    return method;
  }

}









