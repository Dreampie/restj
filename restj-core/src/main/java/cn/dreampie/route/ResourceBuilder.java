package cn.dreampie.route;

import cn.dreampie.annotation.Resource;
import cn.dreampie.annotation.http.*;
import cn.dreampie.config.InterceptorLoader;
import cn.dreampie.config.ResourceLoader;
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

  private ResourceLoader resourceLoader;
  private InterceptorLoader interceptorLoader;

  private ImmutableList<ResourceMatch> resourceMatches;

  ResourceBuilder(ResourceLoader resourceLoader, InterceptorLoader interceptorLoader) {
    this.resourceLoader = resourceLoader;
    this.interceptorLoader = interceptorLoader;
  }

  void build() {
    InterceptorBuilder interceptorBuilder = new InterceptorBuilder();
    Interceptor[] defaultInters = interceptorLoader.getInterceptorArray();
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
    for (Class<? extends cn.dreampie.route.Resource> resourceClazz : resourceLoader.getControllers()) {
      Interceptor[] resourceInters = interceptorBuilder.buildResourceInterceptors(resourceClazz);
      resource = resourceClazz.getAnnotation(Resource.class);
      if (resource != null) {
        apiPath = resource.value();
      } else {
        apiPath = "";
      }

      Method[] methods = resourceClazz.getMethods();
      for (Method method : methods) {

        Interceptor[] methodInters = interceptorBuilder.buildMethodInterceptors(method);
        Interceptor[] routeInters = interceptorBuilder.buildRouteInterceptors(defaultInters, resourceInters, resourceClazz, methodInters, method);

        delete = method.getAnnotation(DELETE.class);
        if (delete != null) {
          matchBuilder.add(new ResourceMatch(resourceClazz, HttpMethod.DELETE, apiPath + delete.value(), method, routeInters));
          continue;
        }

        get = method.getAnnotation(GET.class);
        if (get != null) {
          matchBuilder.add(new ResourceMatch(resourceClazz, HttpMethod.GET, apiPath + get.value(), method, routeInters));
          continue;
        }

        post = method.getAnnotation(POST.class);
        if (post != null) {
          matchBuilder.add(new ResourceMatch(resourceClazz, HttpMethod.POST, apiPath + post.value(), method, routeInters));
          continue;
        }

        put = method.getAnnotation(PUT.class);
        if (put != null) {
          matchBuilder.add(new ResourceMatch(resourceClazz, HttpMethod.PUT, apiPath + put.value(), method, routeInters));
          continue;
        }

        head = method.getAnnotation(HEAD.class);
        if (head != null) {
          matchBuilder.add(new ResourceMatch(resourceClazz, HttpMethod.HEAD, apiPath + head.value(), method, routeInters));
          continue;
        }

        patch = method.getAnnotation(PATCH.class);
        if (patch != null) {
          matchBuilder.add(new ResourceMatch(resourceClazz, HttpMethod.PATCH, apiPath + patch.value(), method, routeInters));
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





