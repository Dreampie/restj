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


import cn.dreampie.config.*;
import cn.dreampie.log.Logger;
import cn.dreampie.log.LoggerFactory;
import cn.dreampie.plugin.IPlugin;

import java.util.List;

class ConfigLoader {

  private static final Constants constants = new Constants();
  private static final Routes routes = new Routes();
  private static final Plugins plugins = new Plugins();
  private static final Interceptors interceptors = new Interceptors();
  private static final Handlers handlers = new Handlers();
  private static final Logger LOGGER = LoggerFactory.getLogger(ConfigLoader.class);

  // prevent new Config();
  private ConfigLoader() {
  }

  /*
   * Config order: constant, route, plugin, interceptor, handler
   */
  static void config(Config config) {
    config.configConstant(constants);
    config.configRoute(routes);
    config.configPlugin(plugins);
    startPlugins();  // very important!!!
    config.configInterceptor(interceptors);
    config.configHandler(handlers);
  }

  public static Constants getConstants() {
    return constants;
  }

  public static Routes getRoutes() {
    return routes;
  }

  public static Plugins getPlugins() {
    return plugins;
  }

  public static Interceptors getInterceptors() {
    return interceptors;
  }

  public static Handlers getHandlers() {
    return handlers;
  }

  private static void startPlugins() {
    List<IPlugin> pluginList = plugins.getPluginList();
    if (pluginList == null)
      return;

    for (IPlugin plugin : pluginList) {
      try {
        if (!plugin.start()) {
          LOGGER.error("Plugin start error: " + plugin.getClass().getName());
        }
      } catch (Exception e) {
        LOGGER.error("Plugin start error: " + plugin.getClass().getName() + ". \n" + e.getMessage(), e);
      }
    }
  }

}
