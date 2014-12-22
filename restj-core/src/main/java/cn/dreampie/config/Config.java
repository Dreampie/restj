package cn.dreampie.config;


/**
 * Config.
 * <p/>
 * Config order: configConstant(), configController(), configPlugin(), configInterceptor(), configHandler()
 */
public abstract class Config {

  /**
   * Config constant
   */
  public abstract void configConstant(Constants constants);

  /**
   * Config route
   */
  public abstract void configController(Resources resources);

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