package cn.dreampie;


import cn.dreampie.annotation.http.GET;
import cn.dreampie.security.Principal;
import cn.dreampie.security.Subject;
import cn.dreampie.security.SubjectKit;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * Created by ice on 14-12-19.
 */

public class TestResource extends cn.dreampie.route.Resource {
  @GET("/login")
  public Subject login() {
    return SubjectKit.login("a", "b", false);
  }

  @GET("/current")
  public Subject current() {
    return SubjectKit.current();
  }

  @GET("/tests/{name}/api/:value")//@GET("/tests/:name")  两种格式   http://localhost:8081/tests/a/api/value?other=x
  public ImmutableMap find(String name, String value, String other) {
    return ImmutableMap.of("name", name, "value", value, "other", other);
  }

  @GET("/tests")
  public ImmutableList find(Test test) {
    return ImmutableList.of(test, new Test("x", "1"));
  }


}
