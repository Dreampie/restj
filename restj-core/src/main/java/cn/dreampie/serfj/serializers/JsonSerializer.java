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
package cn.dreampie.serfj.serializers;

import cn.dreampie.log.Logger;
import cn.dreampie.log.LoggerFactory;
import com.alibaba.fastjson.JSON;


/**
 * Default Json serializer/deserializer.
 *
 * @author Eduardo Yáñez
 */
public class JsonSerializer implements ObjectSerializer {

  private static final Logger LOGGER = LoggerFactory.getLogger(JsonSerializer.class);

  /**
   * Serializes an object to Json.
   */
  public String serialize(Object object) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Serializing object to Json");
    }
    String json = JSON.toJSONString(object);
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Object serialized well");
    }
    return json;
  }

  /**
   * Deserializes a Json string representation to an object.
   */
  public Object deserialize(String jsonObject) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Deserializing Json object");
    }
    Object obj = JSON.parse(jsonObject);
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Object deserialized");
    }
    return obj;
  }

  /**
   * Returns "application/json" content-type.
   */
  public String getContentType() {
    return "application/json";
  }
}
