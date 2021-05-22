package extclasses;

public class Seg implements Comparable<Seg> {

  public int l;
  public int r;
  public Seg(int l, int r) {
    this.l = l;
    this.r = r;
  }

  @Override
  public String toString() {
    return String.format("seg l,r=(%d,%d)", l, r);
  }
  @Override
  public int compareTo(Seg s2) {
    return this.r==s2.r ? this.l-s2.l : this.r-s2.r;
  }
}




