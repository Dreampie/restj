package cn.dreampie.route.match;

import cn.dreampie.http.HttpMethod;
import com.google.common.base.Optional;

/**
 * Created by ice on 14-12-19.
 */
public interface RestjRequestMatcher {
  Optional<? extends RestjRequestMatch> match(HttpMethod method, String path);
}
