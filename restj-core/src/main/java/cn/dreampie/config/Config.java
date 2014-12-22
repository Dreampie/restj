package cn.dreampie.config;


/**
 * Config.
 * <p/>
 * Config order: configConstant(), configResource(), configPlugin(), configInterceptor(), configHandler()
 */
public abstract class Config {

  /**
   * Config constant
   */
  public abstract void configConstant(ConstantLoader constantLoader);

  /**
   * Config route
   */
  public abstract void configResource(ResourceLoader resourceLoader);

  /**
   * Config cors
   */
  public abstract void configCORS(CORSLoader corsLoader);

  /**
   * Config plugin
   */
  public abstract void configPlugin(PluginLoader pluginLoader);

  /**
   * Config interceptor applied to all actions.
   */
  public abstract void configInterceptor(InterceptorLoader interceptorLoader);

  /**
   * Config handler
   */
  public abstract void configHandler(HandlerLoader handlerLoader);


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