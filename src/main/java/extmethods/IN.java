package extmethods;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

public class IN {

  public static BufferedReader br;
  public static StringTokenizer st;


  // the parser sorts the external methods alphabetically
  public static void tokens() {
    while (!st.hasMoreTokens()) {
      try {
        st = new StringTokenizer(br.readLine());
      } catch (Exception e) {
        throw new RuntimeException("out of tokens!");
      }
    }
  }

  // IN nextToken
  public static String nextString() {
    IN.tokens();
    return st.nextToken();
  }

  // IN nextInt
  public static int nextInt() {
    return Integer.parseInt(IN.nextString());
  }

  // IN nextLong
  public static long nextLong() {
    return Long.parseLong(IN.nextString());
  }

  // inintarray
  // IN next_int_array int
  public static int[] nextIntArray(int n) {
    int[] a = new int[n];
    for (int i=0; i<n; i++) {
      a[i] = IN.nextInt();
    }
    return a;
  }

  // R: try not to use arrays at all!
  // inintarray_offset1
  //  public static int[] nextIntArrayOffset1(int n) {
  //    int[] a = new int[n+1];
  //    for (int i=1; i<=n; i++) {
  //      a[i] = IN.nextInt();
  //    }
  //    return a;
  //  }

  // infer the length
  public static int[] nextIntArray() {
    IN.tokens();
    List<String> tokens = new ArrayList<>();
    while(st.hasMoreTokens()) {
      tokens.add(st.nextToken());
    }
    int[] a = new int[tokens.size()];
    int i=0;
    for (String t : tokens) {
      a[i++] = Integer.parseInt(t);
    }
    return a;
  }



  //  public static ArrayList<Integer> nextIntList() {
  //    IN.zLine();
  //    ArrayList<Integer> a = new ArrayList<>();
  //    while(st.hasMoreTokens()) {
  //      a.add(Integer.parseInt(st.nextToken()));
  //    }
  //    return a;
  //  }



  public static ArrayList<Integer> nextIntList() {
    IN.tokens();
    ArrayList<Integer> a = new ArrayList<>();
    while(st.hasMoreTokens()) {
      a.add(Integer.parseInt(st.nextToken()));
    }
    return a;
  }



  public static IntList nextSpecialIntList() {
    IN.tokens();
    IntList a = new IntList();
    while(st.hasMoreTokens()) {
      a.add(Integer.parseInt(st.nextToken()));
    }
    return a;
  }



  // inintlist
  //
  // I haven't figured out properly if there is ever any advantage to using ArrayList over List
  // both classes (or interfaces) seem to do almost exactly the same thing
  public static ArrayList<Integer> nextIntList(int n) {
    ArrayList<Integer> a = new ArrayList<>();
    for (int i=0; i<n; i++) {
      a.add(IN.nextInt());
    }
    return a;
  }



  public static int[][] nextIntMatrix(int n, int m) {
    int[][] a = new int[n][m];
    for (int i=0; i<n; i++) {
      for (int j=0; j<m; j++) {
        a[i][j] = IN.nextInt();
      }
    }
    return a;
  }


  /**
   * boolean[][] read as n number of strings, each of m length
   * a given character indicates 'true'
   *
   */
  public static boolean[][] nextBoolMatrix(int n, int m, char trueChar) {
    boolean[][] b = new boolean[n][m];
    for (int i = 0; i < n; i++) {
      String str = IN.nextString();
      for (int j = 0; j < m; j++) {
        if (str.charAt(j)==trueChar) {
          b[i][j]=true;
        }
      }
    }
    return b;
  }



  // inlongarray
  public static long[] nextLongArray(int n) {
    long[] a = new long[n];
    for (int i=0; i<n; i++) {
      a[i] = IN.nextLong();
    }
    return a;
  }

  // inlongarray_offset1
  public static long[] nextLongArrayOffset1(int n) {
    long[] a = new long[n+1];
    for (int i=1; i<=n; i++) {
      a[i] = IN.nextLong();
    }
    return a;
  }


  // NOTE - we can't always force the use of long because sometimes we
  // need to use the array values as indexes!
  // inlongarray_offset1
  public static int[] nextIntArrayOffset1(int n) {
    int[] a = new int[n+1];
    for (int i=1; i<=n; i++) {
      a[i] = IN.nextInt();
    }
    return a;
  }



  // inlonglist
  public static ArrayList<Long> nextLongList(int n) {
    ArrayList<Long> a = new ArrayList<>();
    for (int i=0; i<n; i++) {
      a.add(IN.nextLong());
    }
    return a;
  }







  /*
  public static List<Integer> toList(int[] a) {
    List<Integer> list1 = new ArrayList<>();
    for (int i = 0; i < a.length; i++) {
      list1.add(a[i]);
    }
    return list1;
  }
   */



  /*
   *
   * Thinking about classes like these but like this I'm not sure there is enough gain.
   * All the sum, sort, reverse-sort operations on the IntListExample I feel are better
   * done by supplying static methods
   *
   */
  public static class IntList extends ArrayList<Integer> {
    public int g(int i) {
      return super.get(i);
    }
    public int s(int i, int j) {
      return super.set(i, j);
    }
    public int length() {
      return size();
    }
  }



  static class IntListExample extends ArrayList<Integer> {
    //    public IntList(int[] a, int offset) {
    //      for (int v : a) {
    //        add(v);
    //      }
    //    }
    //    public IntList() {
    //      IN.zLine();
    //      while(IN.st.hasMoreTokens()) {
    //        int v = Integer.parseInt(IN.st.nextToken());
    //        add(v);
    //      }
    //    }

    //    @Override
    //    public Integer get(int i) {
    //      throw new RuntimeException("don't use this");
    //    }
    public int g(int i) {
      return super.get(i);
    }
    //    @Override
    //    public Integer set(int i, Integer j) {
    //      throw new RuntimeException("don't use this");
    //    }
    public int s(int i, int j) {
      return super.set(i, j);
    }

    public int length() {
      return size();
    }
    //    public long sum(int start_index, int end_index) {
    //      long sum = 0;
    //      for (int i = start_index; i < end_index; i++) {
    //        sum += super.get(i);
    //      }
    //      return sum;
    //    }
    //    public long sum() {
    //      return sum(0,size());
    //    }
    //    public void sort() {
    //      Collections.sort(this);
    //    }
    //    public void rev_sort() {
    //      Collections.sort(this, Collections.reverseOrder());
    //    }
  }








}








