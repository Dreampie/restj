package cn.dreampie.route;

import cn.dreampie.config.Config;
import cn.dreampie.config.ConstantLoader;
import cn.dreampie.config.SessionLoader;
import cn.dreampie.cors.CORSHandler;
import cn.dreampie.handler.Handler;
import cn.dreampie.handler.HandlerFactory;
import cn.dreampie.log.Logger;
import cn.dreampie.log.LoggerFactory;
import cn.dreampie.plugin.IPlugin;
import cn.dreampie.security.SessionBuilder;

import javax.servlet.ServletContext;
import java.util.List;

/**
 * Restj
 */
public final class Restj {

  private static final Logger LOGGER = LoggerFactory.getLogger(Restj.class);

  private ConstantLoader constantLoader;
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
    constantLoader = ConfigLoader.getConstantLoader();

    initRouter();
    initHandler();

    return true;
  }


  private void initHandler() {
    SessionLoader sessionLoader = ConfigLoader.getSessionLoader();
    SessionBuilder sessionBuilder = new SessionBuilder(sessionLoader.getLimit(), sessionLoader.getRememberDay(), sessionLoader.getAuthenticator(), sessionLoader.getPasswordService());
    Handler actionHandler = new ResourceHandler(resourceBuilder, constantLoader, sessionBuilder);
    Handler corsHandler = new CORSHandler(ConfigLoader.getCORSLoader().getCorsConst());
    ConfigLoader.getHandlerLoader().add(corsHandler);//cors 最好放在第一道拦截器
    handler = HandlerFactory.getHandler(ConfigLoader.getHandlerLoader().getHandlerList(), actionHandler);
  }


  private void initRouter() {
    ConfigLoader.getResourceLoader().build();
    resourceBuilder = new ResourceBuilder(ConfigLoader.getResourceLoader(), ConfigLoader.getInterceptorLoader());
    resourceBuilder.build();
  }

  public void stopPlugins() {
    List<IPlugin> plugins = ConfigLoader.getPluginLoader().getPluginList();
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

  public ConstantLoader getConstantLoader() {
    return ConfigLoader.getConstantLoader();
  }

}










