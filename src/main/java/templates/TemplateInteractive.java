package templates;

import java.util.*;
import static java.lang.Math.*;


/*
 * Notes
 * - Note the line break is essential in the printout statements
 * - remember to type in inputs in the console "6 1 2"<return>, "2"<return., "2"<return>
 * - interactive template not yet supported by the resolver-tool (external calls won't work!)
 *
 *
 */
public class TemplateInteractive {
  void solve() {

    int n = in.nextInt();
    int t = in.nextInt();
    int k = in.nextInt();

    int lo = 1;
    int hi = n;
    int zeroes_ahead = 0;

    while (hi > lo) {
      int mid = (hi+lo)/2;
      int interval = mid-lo+1;
      System.out.printf("? %d %d\n", lo, mid);
      System.out.flush();
      int sum = in.nextInt();
      int nr_zeroes = interval-sum;
      if (zeroes_ahead+nr_zeroes >= k) {
        hi = mid;
      } else {
        zeroes_ahead += nr_zeroes;
        lo = mid+1;
      }
    }

    System.out.printf("! %d\n", lo);
    System.out.flush();

  }

  static Scanner in = new Scanner(System.in);

  public static void main(String[] args) throws Exception {
    new TemplateInteractive().solve();
  }


}







