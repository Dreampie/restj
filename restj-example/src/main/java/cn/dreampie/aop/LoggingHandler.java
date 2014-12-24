package cn.dreampie.aop;

import cn.dreampie.log.Logger;
import cn.dreampie.log.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by ice on 14-12-24.
 */
public class LoggingHandler implements InvocationHandler {
  public Object target;

  private final Logger LOGGER;

  public LoggingHandler(Object target) {
    this.target = target;
    LOGGER = LoggerFactory.getLogger(target.getClass());
  }

  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    LOGGER.info("Excute " + method.getName() + " start...");
    Object result = method.invoke(target, args);
    LOGGER.info("Excute " + method.getName() + " end...");
    return result;
  }
}
