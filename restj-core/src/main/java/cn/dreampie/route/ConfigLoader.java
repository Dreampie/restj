package cn.dreampie.route;


import cn.dreampie.config.*;
import cn.dreampie.log.Logger;
import cn.dreampie.log.LoggerFactory;
import cn.dreampie.plugin.IPlugin;

import java.util.List;

public class ConfigLoader {

  private static final Constants CONSTANTS = new Constants();
  private static final Resources RESOURCES = new Resources();
  private static final Plugins PLUGINS = new Plugins();
  private static final Interceptors INTERCEPTORS = new Interceptors();
  private static final Handlers HANDLERS = new Handlers();
  private static final Logger LOGGER = LoggerFactory.getLogger(ConfigLoader.class);

  // prevent new Config();
  private ConfigLoader() {
  }

  /*
   * Config order: constant, route, plugin, interceptor, handler
   */
  static void config(Config config) {
    config.configConstant(CONSTANTS);
    config.configController(RESOURCES);
    config.configPlugin(PLUGINS);
    startPlugins();  // very important!!!
    config.configInterceptor(INTERCEPTORS);
    config.configHandler(HANDLERS);
  }

  public static Constants getConstants() {
    return CONSTANTS;
  }

  public static Resources getResources() {
    return RESOURCES;
  }

  public static Plugins getPlugins() {
    return PLUGINS;
  }

  public static Interceptors getInterceptors() {
    return INTERCEPTORS;
  }

  public static Handlers getHandlers() {
    return HANDLERS;
  }

  private static void startPlugins() {
    List<IPlugin> pluginList = PLUGINS.getPluginList();
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
