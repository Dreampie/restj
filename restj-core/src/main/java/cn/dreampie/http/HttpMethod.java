/*
 * Copyright 2010 Eduardo Yáñez Parareda
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.dreampie.http;

/**
 * Represents the possible HTTP request methods.
 * <p/>
 * OPTIONS, GET, HEAD, POST, PUT, DELETE, TRACE, CONNECT
 */
public enum HttpMethod {
  OPTIONS("OPTIONS"), GET("GET"), POST("POST"), PUT("PUT"), DELETE("DELETE"), HEAD("HEAD"), PATCH("PATCH"), TRACE("TRACE"), CONNECT("CONNECT");
  private final String value;

  private HttpMethod(String value) {
    this.value = value;
  }

  public String value() {
    return value;
  }

  public String toString() {
    return value;
  }

}
