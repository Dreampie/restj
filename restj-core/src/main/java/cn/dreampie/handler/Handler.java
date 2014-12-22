package cn.dreampie.handler;

import cn.dreampie.http.Request;
import cn.dreampie.http.Response;

/**
 * Handler.
 * <p/>
 * You can config Handler in RestjConfig.configHandler() method,
 * Handler can do anything under the restj action.
 */
public abstract class Handler {

  protected Handler nextHandler;

  /**
   * Handle target
   *
   * @param target    url target of this web http request
   * @param request   HttpServletRequest of this http request
   * @param response  HttpServletRequest of this http request
   * @param isHandled RestjFilter will invoke doFilter() method if isHandled[0] == false,
   *                  it is usually to tell Filter should handle the static resource.
   */
  public abstract void handle(String target, Request request, Response response, boolean[] isHandled);
}




