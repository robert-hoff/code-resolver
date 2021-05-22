package resolvertool.demoinput;
import java.io.*;
import java.util.*;
import static java.lang.Math.*;

import extclasses.*;
import extmethods.*;



/*
 *
 * This source uses an external object indirectly getNewDog() returning a extclasses.Dog
 *
 *        TRIAL.getNewDog().nrOfLegs()
 *
 * Note, the compilation target will require the extclasses.Dog written in
 *
 *
 */
public class A04 {

  static int USE_FILE = 0;
  static int HAS_TESTCASES = 1;
  static String INPUT = "1";

  void solve(int cNr) {
    int nrOfLegs = TRIAL.getNewDog().nrOfLegs();
    System.out.println(nrOfLegs);
  }




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
      new A04().solve(i);
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







