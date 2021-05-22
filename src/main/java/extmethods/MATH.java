package extmethods;
import java.util.*;

public class MATH {

  public static long sumList(ArrayList<Integer> list) {
    long sum = 0;
    for (int v : list) {
      sum += v;
    }
    return sum;
  }


  public static long gcd(long n1, long n2) {
    return n2 == 0 ? n1 : MATH.gcd(n2, n1%n2);
  }
  // int version
  public static int gcd(int n1, int n2) {
    return n2 == 0 ? n1 : MATH.gcd(n2, n1%n2);
  }




}








