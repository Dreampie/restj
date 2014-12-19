/**
 * Copyright (c) 2011-2015, James Zhan 詹波 (jfinal@126.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.dreampie.kit;

/**
 * StrKit.
 */
public class StrKit {

  /**
   * 首字母变小写
   */
  public static String firstCharToLowerCase(String str) {
    char firstChar = str.charAt(0);
    if (firstChar >= 'A' && firstChar <= 'Z') {
      char[] arr = str.toCharArray();
      arr[0] += ('a' - 'A');
      return new String(arr);
    }
    return str;
  }

  /**
   * 首字母变大写
   */
  public static String firstCharToUpperCase(String str) {
    char firstChar = str.charAt(0);
    if (firstChar >= 'a' && firstChar <= 'z') {
      char[] arr = str.toCharArray();
      arr[0] -= ('a' - 'A');
      return new String(arr);
    }
    return str;
  }

  /**
   * 字符串为 null 或者为  "" 时返回 true
   */
  public static boolean isBlank(String str) {
    return str == null || "".equals(str.trim());
  }

  /**
   * 字符串不为 null 而且不为  "" 时返回 true
   */
  public static boolean notBlank(String str) {
    return !isBlank(str);
  }

  public static boolean notBlank(String... strs) {
    if (strs == null)
      return false;
    for (String str : strs)
      return !isBlank(str);
    return true;
  }

  public static boolean notNull(Object... paras) {
    if (paras == null)
      return false;
    for (Object obj : paras)
      if (obj == null)
        return false;
    return true;
  }
}




