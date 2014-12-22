package cn.dreampie;


import cn.dreampie.annotation.Resource;
import cn.dreampie.annotation.http.GET;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * Created by ice on 14-12-19.
 */
@Resource("/api/@")
public class TestResource extends cn.dreampie.route.Resource {
  @GET("/tests/{name}/api/:value")//@GET("/tests/:name")  两种格式   http://localhost:8081/tests/a/api/value?other=x
  public ImmutableMap find(String name, String value, String other) {
    return ImmutableMap.of("name", name, "value", value, "other", other);
  }

  @GET("/tests")
  public ImmutableList find(Test test) {
    return ImmutableList.of(test, new Test("x", "1"));
  }

}
