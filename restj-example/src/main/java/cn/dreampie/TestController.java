package cn.dreampie;


import cn.dreampie.annotation.http.GET;
import cn.dreampie.route.Controller;

/**
 * Created by ice on 14-12-19.
 */
public class TestController extends Controller {
  @GET("/tests/:name")
  public String find(String name) {
    return "/tests/" + name;
  }

}
