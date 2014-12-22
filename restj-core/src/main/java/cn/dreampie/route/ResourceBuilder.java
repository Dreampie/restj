package cn.dreampie.route;

import cn.dreampie.annotation.Resource;
import cn.dreampie.annotation.http.*;
import cn.dreampie.config.Interceptors;
import cn.dreampie.config.Resources;
import cn.dreampie.http.HttpMethod;
import cn.dreampie.interceptor.Interceptor;
import cn.dreampie.interceptor.InterceptorBuilder;
import cn.dreampie.route.match.ResourceMatch;
import com.google.common.collect.ImmutableList;

import java.lang.reflect.Method;
import java.util.List;

/**
 * ActionMapping
 */
public final class ResourceBuilder {

  private Resources resources;
  private Interceptors interceptors;

  private ImmutableList<ResourceMatch> resourceMatches;

  ResourceBuilder(Resources resources, Interceptors interceptors) {
    this.resources = resources;
    this.interceptors = interceptors;
  }

  void build() {
    InterceptorBuilder interceptorBuilder = new InterceptorBuilder();
    Interceptor[] defaultInters = interceptors.getInterceptorArray();
    interceptorBuilder.addToInterceptorsMap(defaultInters);

    ImmutableList.Builder<ResourceMatch> matchBuilder = ImmutableList.builder();

    Resource resource = null;
    DELETE delete = null;
    GET get = null;
    POST post = null;
    PUT put = null;
    HEAD head = null;
    PATCH patch = null;
    String apiPath = "";
    //addResources
    for (Class<? extends cn.dreampie.route.Resource> controllerClazz : resources.getControllers()) {

      resource = controllerClazz.getAnnotation(Resource.class);
      if (resource != null) {
        apiPath = resource.value();
      } else {
        apiPath = "";
      }

      Method[] methods = controllerClazz.getMethods();
      for (Method method : methods) {
        delete = method.getAnnotation(DELETE.class);
        if (delete != null) {
          matchBuilder.add(new ResourceMatch(controllerClazz, HttpMethod.DELETE, apiPath + delete.value(), method));
          continue;
        }

        get = method.getAnnotation(GET.class);
        if (get != null) {
          matchBuilder.add(new ResourceMatch(controllerClazz, HttpMethod.GET, apiPath + get.value(), method));
          continue;
        }

        post = method.getAnnotation(POST.class);
        if (post != null) {
          matchBuilder.add(new ResourceMatch(controllerClazz, HttpMethod.POST, apiPath + post.value(), method));
          continue;
        }

        put = method.getAnnotation(PUT.class);
        if (put != null) {
          matchBuilder.add(new ResourceMatch(controllerClazz, HttpMethod.PUT, apiPath + put.value(), method));
          continue;
        }

        head = method.getAnnotation(HEAD.class);
        if (head != null) {
          matchBuilder.add(new ResourceMatch(controllerClazz, HttpMethod.HEAD, apiPath + head.value(), method));
          continue;
        }

        patch = method.getAnnotation(PATCH.class);
        if (patch != null) {
          matchBuilder.add(new ResourceMatch(controllerClazz, HttpMethod.PATCH, apiPath + patch.value(), method));
          continue;
        }
      }
    }
    resourceMatches = matchBuilder.build();
  }

  public List<ResourceMatch> getResourceMatches() {
    return resourceMatches;
  }
}





