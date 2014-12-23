package cn.dreampie.route;


import cn.dreampie.config.*;
import cn.dreampie.log.Logger;
import cn.dreampie.log.LoggerFactory;
import cn.dreampie.plugin.IPlugin;

import java.util.List;

public class ConfigLoader {

  private static final ConstantLoader CONSTANT_LOADER = new ConstantLoader();
  private static final SessionLoader SESSION_LOADER = new SessionLoader();
  private static final ResourceLoader RESOURCE_LOADER = new ResourceLoader();
  private static final CORSLoader CORS_LOADER = new CORSLoader();
  private static final PluginLoader PLUGIN_LOADER = new PluginLoader();
  private static final InterceptorLoader INTERCEPTOR_LOADER = new InterceptorLoader();
  private static final HandlerLoader HANDLER_LOADER = new HandlerLoader();
  private static final Logger LOGGER = LoggerFactory.getLogger(ConfigLoader.class);

  // prevent new Config();
  private ConfigLoader() {
  }

  /*
   * Config order: constant, route, plugin, interceptor, handler
   */
  static void config(Config config) {
    config.configConstant(CONSTANT_LOADER);
    config.configSession(SESSION_LOADER);
    config.configResource(RESOURCE_LOADER);
    config.configCORS(CORS_LOADER);
    config.configPlugin(PLUGIN_LOADER);
    startPlugins();  // very important!!!
    config.configInterceptor(INTERCEPTOR_LOADER);
    config.configHandler(HANDLER_LOADER);
  }

  public static ConstantLoader getConstantLoader() {
    return CONSTANT_LOADER;
  }

  public static SessionLoader getSessionLoader() {
    return SESSION_LOADER;
  }

  public static ResourceLoader getResourceLoader() {
    return RESOURCE_LOADER;
  }

  public static CORSLoader getCORSLoader() {
    return CORS_LOADER;
  }

  public static PluginLoader getPluginLoader() {
    return PLUGIN_LOADER;
  }

  public static InterceptorLoader getInterceptorLoader() {
    return INTERCEPTOR_LOADER;
  }

  public static HandlerLoader getHandlerLoader() {
    return HANDLER_LOADER;
  }

  private static void startPlugins() {
    List<IPlugin> pluginList = PLUGIN_LOADER.getPluginList();
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
