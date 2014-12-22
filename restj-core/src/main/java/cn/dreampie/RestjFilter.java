package cn.dreampie;

import cn.dreampie.config.Constants;
import cn.dreampie.config.Config;
import cn.dreampie.handler.Handler;
import cn.dreampie.http.HttpRequest;
import cn.dreampie.http.HttpResponse;
import cn.dreampie.log.Logger;
import cn.dreampie.log.LoggerFactory;
import cn.dreampie.route.ConfigLoader;
import cn.dreampie.route.Restj;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Restj framework filter
 */
public final class RestjFilter implements Filter {

  private Handler handler;
  private String encoding;
  private Config config;
  private Constants constants;
  private static final Restj RESTJ = Restj.instance();
  private static final Logger LOGGER = LoggerFactory.getLogger(RestjFilter.class);
  private int contextPathLength;

  public void init(FilterConfig filterConfig) throws ServletException {
    createConfig(filterConfig.getInitParameter("configClass"));

    if (!RESTJ.init(config, filterConfig.getServletContext()))
      throw new RuntimeException("Restj init error!");

    handler = RESTJ.getHandler();
    constants = ConfigLoader.getConstants();
    encoding = constants.getEncoding();
    config.afterRestjStart();

    String contextPath = filterConfig.getServletContext().getContextPath();
    contextPathLength = (contextPath == null || "/".equals(contextPath) ? 0 : contextPath.length());
  }

  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
    HttpServletRequest request = (HttpServletRequest) req;
    HttpServletResponse response = (HttpServletResponse) res;
    request.setCharacterEncoding(encoding);

    String target = request.getRequestURI();
    if (contextPathLength != 0)
      target = target.substring(contextPathLength);

    boolean[] isHandled = {false};
    try {
      handler.handle(target, new HttpRequest(request), new HttpResponse(response, request), isHandled);
    } catch (Exception e) {
      if (LOGGER.isErrorEnabled()) {
        String qs = request.getQueryString();
        LOGGER.error(qs == null ? target : target + "?" + qs, e);
      }
    }

    if (!isHandled[0])
      chain.doFilter(request, response);
  }

  public void destroy() {
    config.beforeRestjStop();
    RESTJ.stopPlugins();
  }

  private void createConfig(String configClass) {
    if (configClass == null)
      throw new RuntimeException("Please set configClass parameter of RestjFilter in web.xml");

    Object temp = null;
    try {
      temp = Class.forName(configClass).newInstance();
    } catch (Exception e) {
      throw new RuntimeException("Can not create instance of class: " + configClass, e);
    }

    if (temp instanceof Config)
      config = (Config) temp;
    else
      throw new RuntimeException("Can not create instance of class: " + configClass + ". Please check the config in web.xml");
  }
}
