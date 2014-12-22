package cn.dreampie.kit;

import org.joda.time.DateTime;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Date: 5/5/13
 * Time: 7:40 AM
 */
public class VersionKit {

  private static final String GROUP_ID = "cn.dreampie";
  private static final String MODULE = "restj-core";

  public static String getVersion() {
    return getVersion(GROUP_ID, MODULE);
  }

  public static String getVersion(String groupId, String module) {
    try {
      InputStream stream = VersionKit.class.getResourceAsStream("/META-INF/maven/" + groupId + "/" + module + "/pom.properties");
      if (stream == null) {
        return "DEV-" + DateTime.now().toString();
      }

      Properties properties = new Properties();
      properties.load(stream);
      return properties.getProperty("version");
    } catch (IOException e) {
      return "DEV-" + DateTime.now().toString();
    }
  }
}
