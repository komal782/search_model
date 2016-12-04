package SetBased;

import Facts.Assignment;
import Facts.Fact;
import Facts.Violations;

import java.util.ArrayList;

/**
 * Created by brandon.goberdhansin on 11/25/16.
 */
public class SetBased {
    //for now have it do randomized shit, start with a s0 which is a randomized fact which is a solution

    ArrayList<Fact> state;
    public SetBased(ArrayList<Assignment> orTreeAssignments){
        state = new ArrayList<>();
        Fact temp = new Fact(orTreeAssignments, null);
        state.add(temp);





    }

    static public Fact ExtentionTest(Fact fact){

        return Extensions.Rule4(fact);


    }

}
