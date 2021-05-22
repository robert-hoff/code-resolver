package resolvertool.demoinput;
import java.io.*;
import java.util.*;
import static java.lang.Math.*;

import extclasses.*;
import extmethods.*;


/*
 *
 * Here the local class DiagonalCheck is matched to the external signature
 * which references its interface.
 * The SymbolSolver takes care of this for us
 *
 * In extmethods.MAT
 *
 *      Collection<Pair> getPairs(int[][] mat, BooleanCheck b)
 *
 *
 */
public class A05 {

  static int USE_FILE = 0;
  static int HAS_TESTCASES = 1;
  static String INPUT = "1";

  void solve(int cNr) {

    int[][] mat = new int[6][6];
    for (Pair p : MAT.getPairs(mat, new DiagonalCheck())) {
      System.out.printf("%d %d \n", p.x, p.y);
    }

  }


  class DiagonalCheck implements BooleanCheck {
    @Override
    public boolean call(int i, int j) {
      return (i+j)%2==1 && j%2==1;
    }
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
      new A05().solve(i);
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







