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

/**
 * Config.
 * <p/>
 * Config order: configConstant(), configRoute(), configPlugin(), configInterceptor(), configHandler()
 */
public abstract class Config {

  /**
   * Config constant
   */
  public abstract void configConstant(Constants constants);

  /**
   * Config route
   */
  public abstract void configRoute(Routes routes);

  /**
   * Config plugin
   */
  public abstract void configPlugin(Plugins plugins);

  /**
   * Config interceptor applied to all actions.
   */
  public abstract void configInterceptor(Interceptors interceptors);

  /**
   * Config handler
   */
  public abstract void configHandler(Handlers handlers);

  /**
   * Call back after Restj start
   */
  public void afterRestjStart() {
  }

  /**
   * Call back before Restj stop
   */
  public void beforeRestjStop() {
  }

}