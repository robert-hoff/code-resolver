package extmethods;

import java.util.*;

public class PR {

  public static void print(int[] a) {
    for (int i = 0; i <= a.length-1; i++) {
      System.out.print(a[i] + " ");
    }
    System.out.println();
  }
  public static void print(int[] a, int i1) {
    for (int i = i1; i <= a.length-1; i++) {
      System.out.print(a[i] + " ");
    }
    System.out.println();
  }
  public static void print(int[] a, int i1, int i2) {
    for (int i = i1; i <= i2; i++) {
      System.out.print(a[i] + " ");
    }
    System.out.println();
  }
  public static void print(long[] a) {
    for (int i = 0; i <= a.length-1; i++) {
      System.out.print(a[i] + " ");
    }
    System.out.println();
  }
  public static void print(long[] a, int i1) {
    for (int i = i1; i <= a.length-1; i++) {
      System.out.print(a[i] + " ");
    }
    System.out.println();
  }
  public static void print(long[] a, int i1, int i2) {
    for (int i = i1; i <= i2; i++) {
      System.out.print(a[i] + " ");
    }
    System.out.println();
  }

  public static void print(long[][] a) {
    for (int i = 0; i < a.length; i++) {
      for (int j = 0; j < a[i].length; j++) {
        System.out.print(a[i][j] + " ");
      }
      System.out.println();
    }
  }


  public static void print(List<?> a) {
    PR.print(a, 0, a.size());
  }
  public static void print(List<?> a, int i1) {
    for (int i = i1; i < a.size(); i++) {
      System.out.print(a.get(i) + " ");
    }
    System.out.println();
  }
  public static void print(List<?> a, int i1, int i2) {
    for (int i = i1; i < i2; i++) {
      System.out.print(a.get(i) + " ");
    }
    System.out.println();
  }

  public static void print(List<?> a, String delim) {
    if (a.size() == 0) return;
    System.out.print(a.get(0));
    for (int i = 1; i <= a.size()-1; i++) {
      System.out.print(delim+""+a.get(i));
    }
    System.out.println();
  }


}








