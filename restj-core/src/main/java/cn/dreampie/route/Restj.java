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
 * Restj
 */
public final class Restj {

  private static final Logger LOGGER = LoggerFactory.getLogger(Restj.class);

  private Constants constants;
  private ResourceBuilder resourceBuilder;
  private Handler handler;
  private ServletContext servletContext;

  public Handler getHandler() {
    return handler;
  }

  private static final Restj instance = new Restj();

  private Restj() {
  }

  public static Restj instance() {
    return instance;
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
    Handler actionHandler = new ResourceHandler(resourceBuilder, constants);
    handler = HandlerFactory.getHandler(ConfigLoader.getHandlers().getHandlerList(), actionHandler);
  }


  private void initRouter() {
    ConfigLoader.getResources().build();
    resourceBuilder = new ResourceBuilder(ConfigLoader.getResources(), ConfigLoader.getInterceptors());
    resourceBuilder.build();
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










