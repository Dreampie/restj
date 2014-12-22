package cn.dreampie.route;

import cn.dreampie.route.match.RouteMatch;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * Controller
 * <br>
 * 昨夜西风凋碧树。独上高楼，望尽天涯路。<br>
 * 衣带渐宽终不悔，为伊消得人憔悴。<br>
 * 众里寻她千百度，蓦然回首，那人却在灯火阑珊处。
 */
public abstract class Resource {

  private RouteMatch routeMatch;


  void setRouteMatch(RouteMatch routeMatch) {
    this.routeMatch = routeMatch;
  }

  public String getPath() {
    return routeMatch.getPath();
  }

  public String getPathParam(String paramName) {
    return routeMatch.getPathParam(paramName);
  }

  public ImmutableMap<String, String> getPathParams() {
    return routeMatch.getPathParams();
  }

  public ImmutableMap<String, ImmutableList<String>> getOtherParams() {
    return routeMatch.getOtherParams();
  }

  public ImmutableList<String> getOtherParam(String name) {
    return routeMatch.getOtherParams().get(name);
  }

}


