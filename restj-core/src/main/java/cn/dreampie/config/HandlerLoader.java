package cn.dreampie.config;


import java.util.ArrayList;
import java.util.List;

/**
 * Handlers.
 */
final public class HandlerLoader {

  private final List<cn.dreampie.handler.Handler> handlerList = new ArrayList<cn.dreampie.handler.Handler>();

  public HandlerLoader add(cn.dreampie.handler.Handler handler) {
    if (handler != null)
      handlerList.add(handler);
    return this;
  }

  public List<cn.dreampie.handler.Handler> getHandlerList() {
    return handlerList;
  }
}
