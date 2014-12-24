package cn.dreampie.route.match;

import cn.dreampie.http.HttpMethod;
import cn.dreampie.http.Request;
import cn.dreampie.interceptor.Interceptor;
import cn.dreampie.kit.ParamNamesScanerKit;
import cn.dreampie.log.Logger;
import cn.dreampie.log.LoggerFactory;
import cn.dreampie.route.Resource;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by ice on 14-12-19.
 */
public class ResourceMatch {

  private static final Logger LOGGER = LoggerFactory.getLogger(ResourceMatch.class);
  private final HttpMethod httpMethod;
  private final String pathPattern;
  private final String stdPathPattern;

  private final Pattern pattern;
  private final ImmutableList<String> pathParamNames;

  private final Class<? extends Resource> controllerClass;
  private final Method method;
  private final ImmutableList<String> allParamNames;
  private final ImmutableList<Class<?>> allParamTypes;

  private final Interceptor[] interceptors;

  public ResourceMatch(Class<? extends Resource> controllerClass, HttpMethod httpMethod, String pathPattern, Method method, Interceptor[] interceptors) {
    this.controllerClass = controllerClass;
    this.httpMethod = checkNotNull(httpMethod);
    this.pathPattern = checkNotNull(pathPattern);
    this.method = method;
    this.interceptors = interceptors;

    allParamNames = ImmutableList.copyOf(ParamNamesScanerKit.getParamNames(method));
    allParamTypes = ImmutableList.copyOf(method.getParameterTypes());


    PathPatternParser s = new PathPatternParser(pathPattern);
    s.parse();

    pattern = Pattern.compile(s.patternBuilder.toString());
    stdPathPattern = s.stdPathPatternBuilder.toString();
    pathParamNames = s.pathParamNamesBuilder.build();

    LOGGER.info("Resource match:" + httpMethod.value() + "(" + pathPattern + "->" + pattern + ")");
  }


  public Optional<? extends RouteMatch> match(Request request) {
    if (!this.httpMethod.equals(request.getHttpMethod())) {
      return Optional.absent();
    }
    Matcher m = pattern.matcher(request.getRestPath());
    if (!m.matches()) {
      return Optional.absent();
    }

    ImmutableMap.Builder<String, String> params = ImmutableMap.builder();
    for (int i = 0; i < m.groupCount() && i < pathParamNames.size(); i++) {
      params.put(pathParamNames.get(i), m.group(i + 1));
    }

    return Optional.of(new RouteMatch(pathPattern, request.getRestPath(), params.build(), request.getQueryParams()));
  }


  public String toString() {
    return method + " " + pathPattern;
  }

  public Class<? extends Resource> getControllerClass() {
    return controllerClass;
  }

  public Method getMethod() {
    return method;
  }

  public HttpMethod getHttpMethod() {
    return httpMethod;
  }

  public String getPathPattern() {
    return pathPattern;
  }

  public String getStdPathPattern() {
    return stdPathPattern;
  }

  public ImmutableList<String> getPathParamNames() {
    return pathParamNames;
  }

  public ImmutableList<String> getAllParamNames() {
    return allParamNames;
  }

  public ImmutableList<Class<?>> getAllParamTypes() {
    return allParamTypes;
  }

  public Interceptor[] getInterceptors() {
    return interceptors;
  }

  // here comes the path pattern parsing logic
  // the code is pretty ugly with lot of cross dependencies, I tried to keep it performant, correct, and maintainable
  // not sure those goals are all achieved though

  private static final class PathPatternParser {
    final int length;
    final String pathPattern;
    int offset = 0;
    PathParserCharProcessor processor = regularCharPathParserCharProcessor;
    ImmutableList.Builder<String> pathParamNamesBuilder = ImmutableList.builder();
    StringBuilder patternBuilder = new StringBuilder();
    StringBuilder stdPathPatternBuilder = new StringBuilder();

    private PathPatternParser(String pathPattern) {
      this.length = pathPattern.length();
      this.pathPattern = pathPattern;
    }

    void parse() {
      while (offset < length) {
        int curChar = pathPattern.codePointAt(offset);

        processor.handle(curChar, this);

        offset += Character.charCount(curChar);
      }
      processor.end(this);
    }
  }

  private static interface PathParserCharProcessor {
    void handle(int curChar, PathPatternParser pathPatternParser);

    void end(PathPatternParser pathPatternParser);
  }

  private static final class CurlyBracesPathParamPathParserCharProcessor implements PathParserCharProcessor {
    private int openBr = 1;
    private boolean inRegexDef;
    private StringBuilder pathParamName = new StringBuilder();
    private StringBuilder pathParamRegex = new StringBuilder();


    public void handle(int curChar, PathPatternParser pathPatternParser) {
      if (curChar == '}') {
        openBr--;
        if (openBr == 0) {
          // found matching brace, end of path param

          if (pathParamName.length() == 0) {
            // it was a mere {}, can't be interpreted as a path param
            pathPatternParser.processor = regularCharPathParserCharProcessor;
            pathPatternParser.patternBuilder.append("{}");
            pathPatternParser.stdPathPatternBuilder.append("{}");
            return;
          }

          if (pathParamRegex.length() == 1) {
            // only the opening paren
            throw new IllegalArgumentException(String.format(
                "illegal path parameter definition '%s' at offset %d - custom regex must not be empty",
                pathPatternParser.pathPattern, pathPatternParser.offset));
          }

          if (pathParamRegex.length() == 0) {
            // use default regex
            pathParamRegex.append("([^\\/]+)");
          } else {
            // close paren for matching group
            pathParamRegex.append(")");
          }

          pathPatternParser.processor = regularCharPathParserCharProcessor;
          pathPatternParser.patternBuilder.append(pathParamRegex);
          pathPatternParser.stdPathPatternBuilder.append("{").append(pathParamName).append("}");
          pathPatternParser.pathParamNamesBuilder.add(pathParamName.toString());
          return;
        }
      } else if (curChar == '{') {
        openBr++;
      }

      if (inRegexDef) {
        pathParamRegex.appendCodePoint(curChar);
      } else {
        if (curChar == ':') {
          // we were in path name, the column marks the separator with the regex definition, we go in regex mode
          inRegexDef = true;
          pathParamRegex.append("(");
        } else {
          if (!Character.isLetterOrDigit(curChar)) {
            //only letters are authorized in path param name
            throw new IllegalArgumentException(String.format(
                "illegal path parameter definition '%s' at offset %d" +
                    " - only letters and digits are authorized in path param name",
                pathPatternParser.pathPattern, pathPatternParser.offset));
          } else {
            pathParamName.appendCodePoint(curChar);
          }
        }
      }
    }


    public void end(PathPatternParser pathPatternParser) {
    }
  }

  private static final class SimpleColumnBasedPathParamParserCharProcessor implements PathParserCharProcessor {
    private StringBuilder pathParamName = new StringBuilder();

    public void handle(int curChar, PathPatternParser pathPatternParser) {
      if (!Character.isLetterOrDigit(curChar)) {
        pathPatternParser.patternBuilder.append("([^\\/]+)");
        pathPatternParser.stdPathPatternBuilder.append("{").append(pathParamName).append("}");
        pathPatternParser.pathParamNamesBuilder.add(pathParamName.toString());
        pathPatternParser.processor = regularCharPathParserCharProcessor;
        pathPatternParser.processor.handle(curChar, pathPatternParser);
      } else {
        pathParamName.appendCodePoint(curChar);
      }
    }


    public void end(PathPatternParser pathPatternParser) {
      pathPatternParser.patternBuilder.append("([^\\/]+)");
      pathPatternParser.stdPathPatternBuilder.append("{").append(pathParamName).append("}");
      pathPatternParser.pathParamNamesBuilder.add(pathParamName.toString());
    }
  }

  private static final PathParserCharProcessor regularCharPathParserCharProcessor = new PathParserCharProcessor() {

    public void handle(int curChar, PathPatternParser pathPatternParser) {
      if (curChar == '{') {
        pathPatternParser.processor = new CurlyBracesPathParamPathParserCharProcessor();
      } else if (curChar == ':') {
        pathPatternParser.processor = new SimpleColumnBasedPathParamParserCharProcessor();
      } else {
        pathPatternParser.patternBuilder.appendCodePoint(curChar);
        pathPatternParser.stdPathPatternBuilder.appendCodePoint(curChar);
      }
    }


    public void end(PathPatternParser pathPatternParser) {
    }
  };
}
