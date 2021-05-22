package resolvertool.demoinput;
import java.io.*;
import java.util.*;
import static java.lang.Math.*;

import extclasses.*;
import extmethods.*;


/*
 * Just a typical easy problem
 * https://codeforces.com/problemset/problem/734/A
 *
 * Some additional variables and methods written in
 *
 *
 *
 */
public class A01 {

  static int USE_FILE = 0;
  static int HAS_TESTCASES = 0;
  static String INPUT = "6 6\n"
      + "ADAAAA\n"
      + "";

  void solve(int cNr) {
    int n = IN.nextInt();
    int m = IN.nextInt();
    String s = IN.nextString();

    if (cNr != 1) return;

    // remove these
    ZZ.showIntegerArray(new int[] {1,2,3});

    List<Long> my_list = new ArrayList<>();
    my_list.add(8l);            // <-- this sucks, I've tested this in Java 13 and it still can't do it with ints
    my_list.add(9l);
    PR.print(my_list, "_");


    int aWinCount = countOccurances(s, 'A');
    int dWinCount = countOccurances(s, 'D');

    if (aWinCount>dWinCount) {
      System.out.println("Anton");
      return;
    }
    if (dWinCount>aWinCount) {
      System.out.println("Danik");
      return;
    }

    System.out.println("Friendship");
  }


  int countOccurances(String s, char c) {
    return s.length() - s.replace(""+c, "").length();
  }



  // NOTE
  // these methods are unused, but it's not apparant from the Eclipse interface
  void print(int a) {
    System.out.println(a);
  }
  void print(String a) {
    System.out.println(a);
  }



  // note yui will be removed because it doesn't have a reference
  // but v is references by yui and becomes left in
  int v = 10;
  int yui = TRIAL.addOne(v+10);

  // the static variable and methods will be left in
  static int hello = 0;
  static void doOnce(int v) {
    hello = v+1;
    System.err.println("hello");
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

  public static void main(String[] a) {
    int hello = 9;
    doOnce(hello);

    IN.br = getBufferedReader();
    IN.st = new StringTokenizer("");
    long startTime = java.lang.System.currentTimeMillis();
    int t = HAS_TESTCASES>0 ? IN.nextInt() : 1;
    for (int i=1; i<=t; i++) {
      new A01().solve(i);
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


