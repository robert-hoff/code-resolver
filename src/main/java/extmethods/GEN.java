package extmethods;

import java.util.ArrayList;
import java.util.List;
import static java.lang.Math.random;

/*
 * "Generate"
 *
 */
public class GEN {

  public static List<List<Integer>> genTreeAdjacencies(int n, int connectedness) {
    List<List<Integer>> adjacencies = new ArrayList<>();
    for (int i = 0; i <= n; i++) {
      adjacencies.add(new ArrayList<>());
    }
    int[] edges = createEdges(n, connectedness);
    for (int i = 0; i < n-1; i++) {
      adjacencies.get(edges[i]).add(i+2);
    }

    return adjacencies;
  }


  // values e0, e1, e2 is the edge from node e[i] to i+2
  // the root node is 1
  private static int[] createEdges(int n, int connectedness) {
    int[] edges = new int[n-1];
    for (int i = 2; i <= n; i++) {
      int fromEdge = i-randNum(connectedness);
      if (fromEdge <= 0) fromEdge = 1;
      edges[i-2] = fromEdge;
    }
    return edges;
  }


  private static int randNum(int n) {
    return (int)(random()*n)+1;
  }






}







