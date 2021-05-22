package resolvertool.demoinput;
import java.io.*;
import java.util.*;
import static java.lang.Math.*;

import extclasses.*;
import extmethods.*;


/*
 *
 * This source resolves a nested method call made in an external method
 * The external call is to the class D1 which is not referred to in this class
 *
 *
 *
 */
public class A06 {

  static int USE_FILE = 0;
  static int HAS_TESTCASES = 1;
  static String INPUT = "1\n"
      + "2 3";


  void solve(int cNr) {

    int n = IN.nextInt();
    int m = IN.nextInt();

    List<Long> list1 = new ArrayList<>();
    list1.add(1l);
    list1.add(2l);
    list1.add(3l);

    System.out.println(TRIAL.sumListWithExternalCall(list1));
    PR.print(list1);

    System.out.println(abs(10-100));

    //    long x = TRIAL.getPairValue();
    //    System.out.println(x);



  }




  private int inf = Integer.MAX_VALUE;
  private int mod = (int) 1e9+7;
  // private int mod = 998244353;





  // ---------------------------------------------------------------------------------------------- //
  // ---------------------------------------------------------------------------------------------- //


  // These are left in for conversion purposes. They remain the same in the final submission
  // NOTE - the compiler script uses the 'br' line as an insertion reference

  static BufferedReader br;
  static StringTokenizer st = new StringTokenizer("");
  static PrintWriter out = new PrintWriter(System.out);

  public static void main(String[] a) {


    // code below is replaced
    IN.br = getBufferedReader();
    IN.st = new StringTokenizer("");
    long startTime = java.lang.System.currentTimeMillis();
    int t = HAS_TESTCASES>0 ? IN.nextInt() : 1;
    for (int i=1; i<=t; i++) {
      new A06().solve(i);
    }
    System.err.printf("[%dms]\n", java.lang.System.currentTimeMillis()-startTime);
  }

  private static BufferedReader getBufferedReader() {
    BufferedReader br = null;
    if (USE_FILE == 0) {
      System.setIn(new ByteArrayInputStream(INPUT.getBytes()));
      br = new BufferedReader(new InputStreamReader(System.in));
    }
    if (USE_FILE > 0) {
      String filename = String.format("in/cf%d.txt", USE_FILE);
      try {
        br = new BufferedReader(new FileReader(filename));
      } catch (FileNotFoundException e) {
        e.printStackTrace();
        throw new RuntimeException("couldn't find input file!");
      }
      System.err.printf("[%s]\n", filename);
    }
    return br;
  }





}







