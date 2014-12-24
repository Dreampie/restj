package cn.dreampie;

import cn.dreampie.aop.LoggingHandler;

import java.lang.reflect.Proxy;

/**
 * Created by ice on 14-12-22.
 */
public class Test {
  private String name;
  private String value;

  public Test() {
  }

  public Test(String name, String value) {
    this.name = name;
    this.value = value;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
