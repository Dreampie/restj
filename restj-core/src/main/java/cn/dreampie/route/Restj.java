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

import cn.dreampie.config.Config;
import cn.dreampie.config.Constants;
import cn.dreampie.handler.Handler;
import cn.dreampie.handler.HandlerFactory;
import cn.dreampie.log.Logger;
import cn.dreampie.log.LoggerFactory;
import cn.dreampie.plugin.IPlugin;

import javax.servlet.ServletContext;
import java.util.List;

/**
 * JFinal
 */
public final class Restj {

  private static final Logger LOGGER = LoggerFactory.getLogger(Restj.class);

  private Constants constants;
  private RouterBuilder routerBuilder;
  private Handler handler;
  private ServletContext servletContext;

  public Handler getHandler() {
    return handler;
  }

  private static final Restj me = new Restj();

  private Restj() {
  }

  public static Restj me() {
    return me;
  }

  public boolean init(Config config, ServletContext servletContext) {
    this.servletContext = servletContext;

    ConfigLoader.config(config);  // start plugin and init logger factory in this method
    constants = ConfigLoader.getConstants();

    initRouter();
    initHandler();

    return true;
  }


  private void initHandler() {
    Handler actionHandler = new RouterHandler(routerBuilder, constants);
    handler = HandlerFactory.getHandler(ConfigLoader.getHandlers().getHandlerList(), actionHandler);
  }


  private void initRouter() {
    ConfigLoader.getControllers().build();
    routerBuilder = new RouterBuilder(ConfigLoader.getControllers(), ConfigLoader.getInterceptors());
    routerBuilder.build();
  }

  public void stopPlugins() {
    List<IPlugin> plugins = ConfigLoader.getPlugins().getPluginList();
    if (plugins != null) {
      for (int i = plugins.size() - 1; i >= 0; i--) {    // stop plugins

        try {
          if (!plugins.get(i).stop())
            LOGGER.error("Plugin stop error: " + plugins.get(i).getClass().getName());
        } catch (Exception e) {
          LOGGER.error("Plugin stop error: " + plugins.get(i).getClass().getName(), e);
        }
      }
    }
  }

  public ServletContext getServletContext() {
    return this.servletContext;
  }

  public Constants getConstants() {
    return ConfigLoader.getConstants();
  }

}










