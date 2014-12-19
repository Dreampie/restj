package cn.dreampie.data;

/**
 * Created by ice on 14-12-19.
 */
public class Tuple {

  public static Tuple v(Object... args) {
    return new Tuple(args);
  }

  private Object[] items;

  private Tuple(Object[] items) {
    this.items = items;
  }

  public Object _(int index) {
    if (index < 0 || items == null || index > items.length - 1) {
      return null;
    }
    return items[index];
  }

  public static void main(String[] args) {
    Tuple t = Tuple.v("Unmi", "fantasia@sina.come");
    System.out.println(t._(0)); //输出 Unmi
  }
}
