package extmethods;

import java.util.*;
import extclasses.*;


/*
 * Methods here are purely for testing JavaParser and JavaSymbolSolver
 *
 *
 */
public class TRIAL {


  public static Dog getNewDog() {
    return new Dog();
  }

  public static long addOne(long ll) {
    return ll+1;
  }
  public static int addOne(int ll) {
    return ll+1;
  }
  public static long addOne(String s) {
    return 1;
  }
  public static long findSegmentWidth(Seg s) {
    return s.r-s.l;
  }


  // -- Java does not have an obvious procedure for resolving closely related methods.
  //    Luckily the method signature matching is taken care of by JavaSymbolSolver
  //
  //
  // Methods with the same name and with AbstractList, List and Collection
  // arguments will work together, but
  // AbstractCollection with List causes a compiler error (ambiguous method signature), and
  // RandomAccess with List causes a compiler error (ambiguous method signature)

  public static void testMethod1(AbstractList<Long> my_list) {
    System.err.printf("AbstractList   size=%d \n", my_list.size());
  }
  //  public static void testMethod1(AbstractCollection<Long> my_list) {
  //    System.err.printf("AbstractCollection   size=%d \n", my_list.size());
  //  }
  public static void testMethod1(List<Long> my_list) {
    System.err.println("List");
  }
  public static void testMethod1(Collection<Long> my_list) {
    System.err.println("Collection");
  }
  //  public static void testMethod1(RandomAccess my_list) {
  //    System.err.println("List");
  //  }



  // If a class inherits from both multiple interfaces BooleanCheck and BooleanCheck2
  // an ambiguity will occur
  //  public static void testMethod2(BooleanCheck b) {
  //  }
  //  public static void testMethod2(BooleanCheck2 b) {
  //  }



  public static long sumListWithExternalCall(List<Long> a) {
    long sum = D1.sumList(a);
    return sum;
  }


  // NOTE If an object is instantiated like this, the resolver will fail
  //  public static long getPairValue() {
  //    Pair p = new Pair(10,10);
  //    return p.x;
  //  }



  // returns a different type of list (works)
  public static LinkedList<Long> convert(List<Long> a, int maxind) {
    LinkedList<Long> ret = new LinkedList<>();

    for (int j = 0; j < a.get(maxind); j++) {
      ret.add(maxind+1l);
    }
    a.set(maxind, 0l);
    for (int i = 0; i < a.size(); i++) {
      for (int j = 0; j < a.get(i); j++) {
        ret.add(i+1l);
      }
    }
    return ret;
  }



}








