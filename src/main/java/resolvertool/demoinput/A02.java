package resolvertool.demoinput;
import java.io.*;
import java.util.*;
import static java.lang.Math.*;

import extclasses.*;
import extmethods.*;


/*
 * Testing a nested PR call, in PR.print(b) below
 *
 * The attempted problem is
 * https://codeforces.com/problemset/problem/136/A
 *
 *
 */
public class A02 {

  static int USE_FILE = 0;
  static int HAS_TESTCASES = 0;
  static String INPUT = "4\n"
      + "2 3 4 1";

  void solve(int cNr) {
    IN.nextInt();
    ArrayList<Integer> a = IN.nextIntList();
    int n = a.size();

    ArrayList<Integer> b = new ArrayList<>();
    for (int i = 0; i < n; i++) {
      b.add(1);
    }
    for (int i = 0; i < n; i++) {
      int num = a.get(i);
      b.set(num-1, i+1);
    }


    // This PR call is nested, and the arguments need to be correctly infered from
    // the external method declaration
    PR.print(b);
  }



  private int inf = Integer.MAX_VALUE;
  private int mod = (int) 1e9+7;
  // private int mod = 998244353;




  // ---------------------------------------------------------------------------------------------- //
  // ---------------------------------------------------------------------------------------------- //


  // These are left in for conversion purposes. They remain the same in the final submission
  static BufferedReader br;
  static StringTokenizer st = new StringTokenizer("");
  static PrintWriter out = new PrintWriter(System.out);

  public static void main(String[] args) {
    IN.br = getBufferedReader();
    IN.st = new StringTokenizer("");
    long startTime = java.lang.System.currentTimeMillis();
    int t = HAS_TESTCASES>0 ? IN.nextInt() : 1;
    for (int i=1; i<=t; i++) {
      new A02().solve(i);
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







