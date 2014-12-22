/**
 * Copyright (c) 2011-2015, James Zhan 詹波 (jfinal@126.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.dreampie.route;


import cn.dreampie.config.Constants;
import cn.dreampie.handler.Handler;
import cn.dreampie.http.ContentType;
import cn.dreampie.http.HttpStatus;
import cn.dreampie.http.Request;
import cn.dreampie.http.Response;
import cn.dreampie.log.Logger;
import cn.dreampie.log.LoggerFactory;
import cn.dreampie.route.match.RouteMatch;
import cn.dreampie.route.match.RouterMatch;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Optional;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * ActionHandler
 */
public final class RouterHandler extends Handler {

  private final boolean devMode;
  private final RouterBuilder routerBuilder;
  private static final Logger LOGGER = LoggerFactory.getLogger(RouterHandler.class);

  public RouterHandler(RouterBuilder routerBuilder, Constants constants) {
    this.routerBuilder = routerBuilder;
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
    RouterMatch routerMatch = null;
    isHandled[0] = true;

    for (RouterMatch matcher : routerBuilder.getRouterMatches()) {
      routeMatch = matcher.match(request);
      if (routeMatch.isPresent()) {
        routerMatch = matcher;
        break;
      }
    }

    if (routeMatch.isPresent()) {
      String json = JSON.toJSONString(new RouterInvocation(routerMatch, routeMatch.get()).invoke());
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
      for (RouterMatch router : routerBuilder.getRouterMatches()) {
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





