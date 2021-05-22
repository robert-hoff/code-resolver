package resolvertool.demoinput;
import java.io.*;
import java.util.*;
import static java.lang.Math.*;

import extclasses.*;
import extmethods.*;


/*
 *
 * These interactions are demonstrated
 *
 * - use of external classes (DiagonalIterator and Seg)
 * - Use of local class (SegCollection)
 * - Call to MATH methods at different locations
 * - MATH.findSegmentWidth(s) requires resolving its type argument as Seg
 *
 *
 */
public class A03 {

  static int USE_FILE = 0;
  static int HAS_TESTCASES = 1;
  static String INPUT = "1\n"
      + "3\n"
      + "1 2 3\n"
      + "4 5 6\n"
      + "7 8 9";

  void solve(int cNr) {
    int n = IN.nextInt();
    int[][] mat = IN.nextIntMatrix(n, n);

    ZZ.showIntegerArray(mat);
    SegCollection mySegs = new SegCollection();

    for (int v : new DiagonalIterator(mat)) {
      System.out.print(v + " ");
      Seg s = new Seg(1+v, 20-v);
      mySegs.addSeg(s);
    }


    System.out.println();
    System.out.printf("collection size: %s, segs-id: %d \n", mySegs.segs.size(), mySegs.id);
    solveThis(mySegs.getFirst());
  }


  void solveThis(Seg s) {
    long w2 = TRIAL.findSegmentWidth(s);
    System.out.println(w2);
  }


  class SegCollection {
    ArrayList<Seg> segs;
    int id;
    public SegCollection() {
      segs = new ArrayList<>();
      id = TRIAL.addOne(123);
    }
    public void addSeg(Seg s) {
      segs.add(s);
    }
    public Seg getFirst() {
      return segs.get(0);
    }
  }





  // ---------------------------------------------------------------------------------------------- //
  // ---------------------------------------------------------------------------------------------- //


  // These are left in for conversion purposes. They remain the same in the final submission
  // NOTE - the compiler script uses the 'br' line as an insertion reference
  static BufferedReader br;
  static StringTokenizer st = new StringTokenizer("");
  static PrintWriter out = new PrintWriter(System.out);

  public static void main(String[] args) {
    IN.br = getBufferedReader();
    IN.st = new StringTokenizer("");
    long startTime = java.lang.System.currentTimeMillis();
    int t = HAS_TESTCASES>0 ? IN.nextInt() : 1;
    for (int i=1; i<=t; i++) {
      new A03().solve(i);
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







