package extclasses;

import java.util.*;

/*
 *
 * An interesting idea, but note, may here just return
 * a List<> or Collection<> which already implements the Iterable<> interface
 *
 *
 */
public class DiagonalIterator implements Iterable<Integer>, Iterator<Integer> {
  int i=0,j=0;
  int[][] mat;
  public DiagonalIterator(int[][] mat) {
    this.mat = mat;
  }
  @Override
  public boolean hasNext() {
    return i < mat.length && j < mat[0].length;
  }
  @Override
  public Integer next() {
    return mat[i++][j++];
  }
  @Override
  public Iterator<Integer> iterator() {
    return this;
  }
}






