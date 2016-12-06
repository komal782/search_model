package SetBased;

import java.util.Comparator;

/**
 * Created by brandon.goberdhansin on 11/30/16.
 */

//http://stackoverflow.com/questions/2670982/using-pairs-or-2-tuples-in-java
public class Tuple implements Comparable{
    public int x;
    public int y;

    @Override
    public int compareTo(Object o) {
        Tuple arg = (Tuple)o;
        int dif = this.y - arg.y;
        return (dif);
    }

    public Tuple(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
