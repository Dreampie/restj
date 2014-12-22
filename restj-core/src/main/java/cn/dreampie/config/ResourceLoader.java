package cn.dreampie.config;

import cn.dreampie.kit.ClassScanerKit;
import cn.dreampie.log.Logger;
import cn.dreampie.log.LoggerFactory;
import cn.dreampie.route.Resource;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Routes.
 */
final public class ResourceLoader {

  private final List<Class<? extends Resource>> controllers = Lists.newArrayList();
  private List<Class<? extends Resource>> excludeClasses = Lists.newArrayList();
  private List<Class<? extends Resource>> includeClasses = Lists.newArrayList();
  private List<String> includeClassPaths = Lists.newArrayList();
  private List<String> excludeClassPaths = Lists.newArrayList();
  private static final Logger LOGGER = LoggerFactory.getLogger(ResourceLoader.class);

  public ResourceLoader add(ResourceLoader resourceLoader) {
    if (resourceLoader != null) {
      resourceLoader.build();
      this.controllers.addAll(resourceLoader.controllers);
    }
    return this;
  }


  /**
   * Add url mapping to controller. The view path is controllerKey
   *
   * @param controllerClass Controller Class
   */
  public ResourceLoader add(Class<? extends Resource> controllerClass) {
    controllers.add(controllerClass);
    return this;
  }

  public ResourceLoader addExcludeClasses(Class<? extends Resource>... clazzes) {
    for (Class<? extends Resource> clazz : clazzes) {
      excludeClasses.add(clazz);
    }
    return this;
  }

  public ResourceLoader addExcludeClasses(List<Class<? extends Resource>> clazzes) {
    if (clazzes != null) {
      excludeClasses.addAll(clazzes);
    }
    return this;
  }

  public ResourceLoader addExcludePaths(String... paths) {
    for (String path : paths) {
      excludeClassPaths.add(path);
    }
    return this;
  }

  public ResourceLoader addIncludeClasses(Class<? extends Resource>... clazzes) {
    for (Class<? extends Resource> clazz : clazzes) {
      includeClasses.add(clazz);
    }
    return this;
  }

  public ResourceLoader addIncludeClasses(List<Class<? extends Resource>> clazzes) {
    if (clazzes != null) {
      includeClasses.addAll(clazzes);
    }
    return this;
  }

  public ResourceLoader addIncludePaths(String... paths) {
    for (String path : paths) {
      includeClassPaths.add(path);
    }
    return this;
  }

  public void build() {
    List<Class<? extends Resource>> controllerClasses = ClassScanerKit.of(Resource.class).includepaths(includeClassPaths).search();
    for (Class controller : controllerClasses) {
      if (excludeClasses.contains(controller)) {
        continue;
      }
      this.add(controller);
      LOGGER.info("routes.add(" + controller.getName() + ")");
    }
  }

  public List<Class<? extends Resource>> getControllers() {
    return controllers;
  }
}






