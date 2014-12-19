package cn.dreampie.route.bind;

import cn.dreampie.config.Routes;
import cn.dreampie.kit.ClassSearchKit;
import cn.dreampie.log.Logger;
import cn.dreampie.log.LoggerFactory;
import cn.dreampie.route.Controller;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by wangrenhui on 14-1-2.
 */
public class RoutesBind extends Routes {

  private List<Class<? extends Controller>> excludeClasses = Lists.newArrayList();
  private List<Class<? extends Controller>> includeClasses = Lists.newArrayList();
  private List<String> includeClassPaths = Lists.newArrayList();
  private List<String> excludeClassPaths = Lists.newArrayList();
  private static final Logger LOGGER = LoggerFactory.getLogger(RoutesBind.class);


  public RoutesBind addExcludeClasses(Class<? extends Controller>... clazzes) {
    for (Class<? extends Controller> clazz : clazzes) {
      excludeClasses.add(clazz);
    }
    return this;
  }

  public RoutesBind addExcludeClasses(List<Class<? extends Controller>> clazzes) {
    if (clazzes != null) {
      excludeClasses.addAll(clazzes);
    }
    return this;
  }

  public RoutesBind addExcludePaths(String... paths) {
    for (String path : paths) {
      excludeClassPaths.add(path);
    }
    return this;
  }

  public RoutesBind addIncludeClasses(Class<? extends Controller>... clazzes) {
    for (Class<? extends Controller> clazz : clazzes) {
      includeClasses.add(clazz);
    }
    return this;
  }

  public RoutesBind addIncludeClasses(List<Class<? extends Controller>> clazzes) {
    if (clazzes != null) {
      includeClasses.addAll(clazzes);
    }
    return this;
  }

  public RoutesBind addIncludePaths(String... paths) {
    for (String path : paths) {
      includeClassPaths.add(path);
    }
    return this;
  }

  public void build() {
    List<Class<? extends Controller>> controllerClasses = ClassSearchKit.of(Controller.class).includepaths(includeClassPaths).search();
    for (Class controller : controllerClasses) {
      if (excludeClasses.contains(controller)) {
        continue;
      }
      this.add(controller);
      LOGGER.info("routes.add(" + controller.getName() + ")");
    }
  }
}
