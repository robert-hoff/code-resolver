package extmethods;
import java.util.ArrayList;
import java.util.List;


public class STR {

  public static String reverse(String s) {
    return new StringBuilder(s).reverse().toString();
  }

  public static int[] getCharSummations(String s) {
    int N = 250;
    int[] char_summations = new int[N];
    for (char c : s.toCharArray()) {
      char_summations[c]++;
    }
    return char_summations;
  }

  public static List<Integer> getInteriorSegments(String s, char seg_char) {
    List<Integer> segs = new ArrayList<>();
    int n = s.length();
    int i1 = 0;
    int i2 = n-1;
    char[] chars = s.toCharArray();
    while(i1<n && chars[i1] == seg_char) {
      i1++;
    }
    if (i1==n) return segs;
    while(chars[i2] == seg_char) {
      i2--;
    }
    int current_seg = 0;
    for (int i = i1; i <= i2; i++) {
      if (chars[i]==seg_char) {
        current_seg++;
      } else {
        if(current_seg>0) {
          segs.add(current_seg);
          current_seg=0;
        }
      }
    }
    return segs;
  }



  /*
   *
   * counts the number of occurences of a character
   * at the beginning and end of a string
   *
   * E.g. "__**_____**__***___"
   * returns 5 (2 at the beginning and 3 at the end)
   *
   *
   */
  public static int getTrail(String s, char trail_char) {
    int n = s.length();
    char[] c = s.toCharArray();
    int trail=0, i1=0, i2=0;
    while(i1<n && c[i1]==trail_char) {
      trail++;
      i1++;
    }
    if (i1==n) return trail;
    i2 = n-1;
    while(c[i2]==trail_char) {
      trail++;
      i2--;
    }
    return trail;
  }


  // infer the offsets as either
  // '0','1'
  // 'a','b',...'z'
  public static int[] toIntArray(String s) {
    char offset_char = '0';
    if (s.charAt(0) >= 'a' && s.charAt(0) <= 'z') offset_char = 'a';
    if (s.charAt(0) >= 'A' && s.charAt(0) <= 'Z') offset_char = 'A';

    // NOTE can do this, but will get two methods in the output (a question of style)
    // return STR.toIntArray(s, offset_char);

    int[] int_array = new int[s.length()];
    int ind = 0;
    for (char c : s.toCharArray()) {
      int_array[ind++] = c-offset_char;
    }
    return int_array;
  }




}






