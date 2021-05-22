package extmethods;
import java.util.*;
import extclasses.*;



public class MAT {

  // passing an object as an interface
  public static Collection<Pair> getPairs(int[][] mat, BooleanCheck b) {
    ArrayList<Pair> pairs = new ArrayList<>();
    for (int i = 0; i < mat.length; i++) {
      for (int j = 0; j < mat[0].length; j++) {
        if (b.call(i, j)) {
          pairs.add(new Pair(i,j));
        }
      }
    }
    return pairs;
  }




}





