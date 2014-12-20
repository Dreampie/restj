package cn.dreampie.kit;

import cn.dreampie.data.Tuple;
import com.alibaba.fastjson.JSON;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.*;

public class ParamNamesScanerKitTest {

  @Test
  public void testGetParamNames() throws Exception {
    Method[] methods = Tuple.class.getDeclaredMethods();
    for (Method m : methods)
      System.out.println(ParamNamesScanerKit.getParamNames(m));


    System.out.println(JSON.parseObject("1", Integer.class));
  }
}