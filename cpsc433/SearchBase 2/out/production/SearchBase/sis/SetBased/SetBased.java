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

    static public Fact Extension(Fact fact){
        //System.out.println("An extension is being applied.");
        int x = (int) Math.floor(Math.random() * 10);

        int extend = (int) Math.floor(Math.random() * 16);
        if (x > 2) {
            extend = fact.getSoftConstraint();
        }
        System.out.println("Running extension: " + extend);
        //extend = 7; //(extend > 8) ? 16 : 4;
        if (extend == 0){
            return Extensions.Rule1(fact);  //
        }
        else if (extend == 1){
            return Extensions.Rule2(fact);  //
        }
        else if (extend == 2){
            return Extensions.Rule3(fact);  //
        }
        else if (extend == 3){
            return Extensions.Rule4(fact);  //
        }
        else if (extend == 4){
            return Extensions.Rule5(fact);  //
        }
        else if (extend == 5){
            return Extensions.Rule4(fact);
        }
        else if (extend == 6){
            return Extensions.Rule7(fact);  //
        }
        else if (extend == 7){
            return Extensions.Rule8(fact);  //
        }
        else if (extend == 8){
            return Extensions.Rule9(fact);  //
        }
        else if (extend == 9){
            return Extensions.Rule4(fact);
        }
        else if (extend == 10){
            return Extensions.Rule11(fact); //
        }
        else if (extend == 11){
            return Extensions.Rule4(fact);
        }
        else if (extend == 12){
            return Extensions.Rule4(fact);
        }
        else if (extend == 13){
            return Extensions.Rule4(fact);
        }
        else if (extend == 14){
            return Extensions.Rule15(fact); //
        }
        else {
            return Extensions.Rule4(fact); //
        }
    }

}
