package extmethods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class D1 {


  public static long sumList(List<Long> a) {
    long sum = 0;
    for (Long l : a) {
      sum += l;
    }
    return sum;
  }

  public static ArrayList<Long> toList(long[] a) {
    ArrayList<Long> list = new ArrayList<>();
    for (int i = 0; i < a.length; i++) {
      list.add(a[i]);
    }
    return list;
  }

  public static Map<Long,Long> count(long[] a) {
    Map<Long,Long> map = new HashMap<>();
    for (long l : a) {
      if (map.get(l)==null) {
        map.put(l, 1l);
      } else {
        map.put(l, map.get(l)+1);
      }
    }
    return map;
  }


  public static int[] toIntArray(String s, char offset_char) {
    int[] int_array = new int[s.length()];
    int ind = 0;
    for (char c : s.toCharArray()) {
      int_array[ind++] = c-offset_char;
    }
    return int_array;
  }



}






