package extmethods;

import java.util.List;


/*
 *
 * All methods on the ZZ class are for debugging purposes, they can
 * removed in the compilation target without any analysis (by regex)
 *
 *
 *
 */
public class ZZ {

  public static void main(String[] args) {

    int[][] a = new int[][] {{1,2,3},{1,2,3},{1,2,3}};
    showIntegerArray(a);
  }


  public static void showIntegerArray(long[] a) {
    if (a.length == 0) {
      System.err.println("[]");
    } else {
      System.err.printf("[");
      for (int i = 0; i < a.length; i++) {
        System.err.printf("%6d", a[i]);
      }
      System.err.println("]");
    }
  }


  public static void showIntegerArray(int[] a) {
    if (a.length == 0) {
      System.err.println("[]");
    } else {
      System.err.printf("[");
      for (int i = 0; i < a.length; i++) {
        System.err.printf("%6d", a[i]);
      }
      System.err.println("]");
    }
  }

  public static void showIntegerArray(int[] a, int gap) {
    if (a.length == 0) {
      System.err.println("[]");
    } else {
      System.err.printf("[");
      for (int i = 0; i < a.length; i++) {
        System.err.printf("%"+gap+"d", a[i]);
      }
      System.err.println("]");
    }
  }


  public static void showIntegerArrayWithLabel(int[] a, String label) {
    System.out.print(label + " ");
    if (a.length == 0) {
      System.err.println("[]");
    } else {
      System.err.printf("[");
      for (int i = 0; i < a.length; i++) {
        System.err.printf("%5d", a[i]);
      }
      System.err.println("]");
    }
  }

  public static void showIntegerList(List<Integer> a) {
    if (a.size() == 0) {
      System.err.println("[]");
    } else {
      System.err.printf("[");
      for (int i = 0; i < a.size(); i++) {
        System.err.printf("%4d", a.get(i));
      }
      System.err.println("]");
    }
  }


  public static void showIntegerArray(int[][] a) {
    int NR = a.length;
    int NC = a[0].length;
    if (NR>200 || NC>200) {
      System.err.printf("WARN ARRAY TO BIG (%d,%d)\n",NR,NC);
      if (NR>200) {
        NR=200;
      }
      if (NC>200) {
        NC=200;
      }
    }
    for (int i = 0; i < NR; i++) {
      if (NC == 0) {
        System.err.println("[]");
      } else if (NC == 1) {
        System.err.printf("[%4d]\n", a[i][0]);
      } else {
        String output = String.format("[%4d", a[i][0]);
        for (int j = 1; j < NC; j++) {
          output += String.format(" %4d", a[i][j]);
        }
        output += "]";
        System.err.println(output);
      }
    }
  }


  public static void showIntegerArray(long[][] a) {
    int NR = a.length;
    // int NC = a[0].length;
    for (int i = 0; i < NR; i++) {
      int col_len = a[i].length;
      if (col_len == 0) {
        System.err.println("[]");
      } else if (col_len == 1) {
        System.err.printf("[%4d]\n", a[i][0]);
      } else {
        String output = String.format("[%4d", a[i][0]);
        for (int j = 1; j < col_len; j++) {
          output += String.format(" %4d", a[i][j]);
        }
        output += "]";
        System.err.println(output);
      }
    }
  }

  public static void showDoubleArray(double[] a) {
    if (a.length == 0) {
      System.err.println("[]");
    } else {
      System.err.printf("[");
      for (int i = 0; i < a.length; i++) {
        System.err.printf("%7.3f", a[i]);
      }
      System.err.println("]");
    }
  }

  public static void showDoubleArray(double[][] a) {
    int NR = a.length;
    // int NC = a[0].length;
    for (int i = 0; i < NR; i++) {
      int col_len = a[i].length;
      if (col_len == 0) {
        System.err.println("[]");
      } else if (col_len == 1) {
        System.err.printf("[%8.3f]\n", a[i][0]);
      } else {
        String output = String.format("[%8.3f", a[i][0]);
        for (int j = 1; j < col_len; j++) {
          output += String.format(" %8.3f", a[i][j]);
        }
        output += "]";
        System.err.println(output);
      }
    }
  }



  public static void showBooleanArray(boolean[] a) {
    showBooleanArray(a, true);
  }

  public static void showBooleanArray(boolean[] a, boolean brk) {
    int NR = a.length;
    String output = "[";
    for (int i = 0; i < NR; i++) {
      output += String.format(" %s", a[i] ? "T" : "F");
    }
    output += "]";
    System.err.print(output);
    if (brk) {
      System.err.println();
    }
  }


  public static void showBooleanArray(boolean[][] a) {
    int NR = a.length;
    int NC = a[0].length;
    for (int i = 0; i < NR; i++) {
      if (NC == 0) {
        System.err.println("[]");
      } else if (NC == 1) {
        System.err.printf("[%s]\n", a[i][0] ? "T" : "F");
      } else {
        String output = String.format("[%s", a[i][0] ? "T" : "F");
        for (int j = 1; j < NC; j++) {
          output += String.format(" %s", a[i][j] ? "T" : "F");
        }
        output += "]";
        System.err.println(output);
      }
    }
  }



  public static void showStringArray(String[] S) {
    // System.err.println("-----------------------");
    for (String s : S) {
      System.err.println(s);
    }
    // System.err.println("-----------------------");
  }


  public static void showCharArray(char[] carr) {
    for (char c : carr) {
      System.out.print(c + " ");
    }
    System.err.println();
  }


  public static void showPrint(Object var) {
    System.err.println(var);
  }

  public static void showPrintf(String format, Object... arguments) {
    System.err.printf(format+"\n", arguments);
  }




  public static void showVar(String varName, long val) {
    System.err.printf("%-30s %s \n", varName, val);
  }

  public static void showVar(String varName, Object var) {
    System.err.printf("%-30s %s \n", varName, var.toString());
  }


}

















