package cn.dreampie.route;


import cn.dreampie.config.Constants;
import cn.dreampie.handler.Handler;
import cn.dreampie.http.ContentType;
import cn.dreampie.http.HttpStatus;
import cn.dreampie.http.Request;
import cn.dreampie.http.Response;
import cn.dreampie.log.Logger;
import cn.dreampie.log.LoggerFactory;
import cn.dreampie.route.match.ResourceMatch;
import cn.dreampie.route.match.RouteMatch;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Optional;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * ActionHandler
 */
public final class ResourceHandler extends Handler {

  private final boolean devMode;
  private final ResourceBuilder resourceBuilder;
  private static final Logger LOGGER = LoggerFactory.getLogger(ResourceHandler.class);

  public ResourceHandler(ResourceBuilder resourceBuilder, Constants constants) {
    this.resourceBuilder = resourceBuilder;
    this.devMode = constants.isDevMode();
  }

  /**
   * handle
   * 1: Action action = actionMapping.getAction(target)
   * 2: new ActionInvocation(...).invoke()
   * 3: render(...)
   */
  public final void handle(String target, Request request, Response response, boolean[] isHandled) {
    Optional<? extends RouteMatch> routeMatch = Optional.absent();
    ResourceMatch resourceMatch = null;
    isHandled[0] = true;

    for (ResourceMatch matcher : resourceBuilder.getResourceMatches()) {
      routeMatch = matcher.match(request);
      if (routeMatch.isPresent()) {
        resourceMatch = matcher;
        break;
      }
    }

    if (routeMatch.isPresent()) {
      String json = JSON.toJSONString(new ResourceInvocation(resourceMatch, routeMatch.get()).invoke());
      response.setStatus(HttpStatus.OK);
      response.setContentType(ContentType.JSON.toString());
      write(response, json);
    } else {
      // no route matched
      String path = request.getRestPath();
      StringBuilder sb = new StringBuilder()
          .append("No rest route found for ")
          .append(request.getHttpMethod()).append(" ").append(path).append("\n");

      sb.append("routes:\n")
          .append("-----------------------------------\n");
      for (ResourceMatch router : resourceBuilder.getResourceMatches()) {
        sb.append(router).append("\n");
      }
      sb.append("-----------------------------------");
      response.setStatus(HttpStatus.NOT_FOUND);
      response.setContentType(ContentType.TEXT.toString());
      write(response, sb.toString());
    }
  }

  private void write(Response response, String content) {

    PrintWriter writer = null;
    try {
      writer = response.getWriter();
      writer.print(content);
      writer.flush();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (writer != null)
        writer.close();
    }
  }


}





