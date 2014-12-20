package cn.dreampie;


import cn.dreampie.annotation.http.GET;
import cn.dreampie.route.Controller;

/**
 * Created by ice on 14-12-19.
 */
public class TestController extends Controller {
  @GET("/tests/{name}/api/:value")//@GET("/tests/:name")  两种格式   http://localhost:8081/tests/a/api/value?other=x
  public String find(String name, String value, String other) {
    return "/tests/" + name + value + other;
  }

}
