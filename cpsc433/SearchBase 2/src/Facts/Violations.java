package Facts;

/**
 * Created by Brandon on 11/24/2016.
 */

import SetBased.Tuple;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * This will be used by the set based implementation
 * so we can assume that we have a complete solution (assignments)
 */

public abstract class Violations implements ConstraintID {


    /**
     * Checks to make sure that a manager, group head or project do not share
     * a room
     * @param assignment
     * @return
     */
    protected boolean HardContraint4(Assignment assignment){
        Person[] persons = assignment.getPerson();
        if (persons.length == 2){
            Person person1 = persons[0];
            Person person2 = persons[1];
            if(person1.isGroupHead || person2.isGroupHead || person1.isManager || person2.isManager || person1.projectHead || person2.projectHead)
                return false;
        }
        return true;

    }


    /**
     * Return a tuple with the soft contraint violated and the overall value
     * @param fact is the fact we are evaluation this is our Fwert!!!
     * @return
     */
    protected Tuple AllSoftConstraints(Fact fact){
        int overallViolation = 0;
        Tuple[] softConstraintsViolated = new Tuple[16];
        initSoftContraintViolated(softConstraintsViolated);
        ArrayList<Assignment> assignmentsArraylist = fact.getAssignments();
        fact.clearPriorityQueue();
        for(Assignment assignment: assignmentsArraylist){
            Tuple[] scores = new Tuple[16];
            scores[0] = new Tuple(SOFTCONSTRAINT1, SoftConstraint1(assignment));
            scores[1] = new Tuple(SOFTCONSTRAINT2 ,SoftConstraint2(assignment, fact));
            scores[2] = new Tuple(SOFTCONSTRAINT3, SoftConstraint3(assignment, fact));
            scores[3] = new Tuple(SOFTCONSTRAINT4, SoftConstraint4(assignment));
            scores[4] = new Tuple(SOFTCONSTRAINT5, SoftConstraint5(assignment, fact));
            scores[5] = new Tuple(SOFTCONSTRAINT6, SoftConstraint6(assignment,fact));
            scores[6] = new Tuple(SOFTCONSTRAINT7, SoftConstraint7(assignment,fact));
            scores[7] = new Tuple(SOFTCONSTRAINT8, SoftConstraint8(assignment,fact));
            scores[8] = new Tuple(SOFTCONSTRAINT9, SoftConstraint9(assignment,fact));
            scores[9] = new Tuple(SOFTCONSTRAINT10, SoftConstraint10(assignment,fact));
            scores[10] = new Tuple(SOFTCONSTRAINT11, SoftConstraint11(assignment));
            scores[11] = new Tuple(SOFTCONSTRAINT12, SoftConstraint12(assignment));
            scores[12] = new Tuple(SOFTCONSTRAINT13, SoftConstraint13(assignment));
            scores[13] = new Tuple(SOFTCONSTRAINT14, SoftConstraint14(assignment));
            scores[14] = new Tuple(SOFTCONSTRAINT15, SoftConstraint15(assignment));
            scores[15] = new Tuple(SOFTCONSTRAINT16, SoftConstraint16(assignment));

            int i = 0;
            for(Tuple score : scores){
                softConstraintsViolated[i].y += score.y; //calculating which soft constraint caused the most damage!
                i++;
            }
            Arrays.sort(scores);
            int violation = 0;
            for (i = 0; i < 16; i++){
                if(scores[i].y == 0)
                    break;
                violation += scores[i].y;
                //System.out.println("Violation[" + (i+1) + "]: " + scores[i].y + " Soft Constraint Violated: " + (scores[i].x + 1));
            }
            assignment.setScore(violation); //overall value of the assignment in question
            assignment.setSoftContraint(scores[0].x);
            int value = fact.getSoftConstraintValue();
            if (value > scores[0].y){
                fact.setSoftConstraint(scores[0].x);
                fact.setSoftConstraintValue(scores[0].y);
            }
            fact.addToPriorityQueue(assignment);
            overallViolation += violation;
        }
        Arrays.sort(softConstraintsViolated);
        fact.setScore(overallViolation);
        return new Tuple(softConstraintsViolated[0].x, softConstraintsViolated[1].x);
    }
    private void initSoftContraintViolated(Tuple[] softContraintsViolated ){
        //length of tuples is 16
        softContraintsViolated[0] = new Tuple(SOFTCONSTRAINT1, 0);
        softContraintsViolated[1] = new Tuple(SOFTCONSTRAINT2 ,0);
        softContraintsViolated[2] = new Tuple(SOFTCONSTRAINT3, 0);
        softContraintsViolated[3] = new Tuple(SOFTCONSTRAINT4, 0);
        softContraintsViolated[4] = new Tuple(SOFTCONSTRAINT5, 0);
        softContraintsViolated[5] = new Tuple(SOFTCONSTRAINT6, 0);
        softContraintsViolated[6] = new Tuple(SOFTCONSTRAINT7, 0);
        softContraintsViolated[7] = new Tuple(SOFTCONSTRAINT8, 0);
        softContraintsViolated[8] = new Tuple(SOFTCONSTRAINT9, 0);
        softContraintsViolated[9] = new Tuple(SOFTCONSTRAINT10, 0);
        softContraintsViolated[10] = new Tuple(SOFTCONSTRAINT11, 0);
        softContraintsViolated[11] = new Tuple(SOFTCONSTRAINT12, 0);
        softContraintsViolated[12] = new Tuple(SOFTCONSTRAINT13, 0);
        softContraintsViolated[13] = new Tuple(SOFTCONSTRAINT14, 0);
        softContraintsViolated[14] = new Tuple(SOFTCONSTRAINT15, 0);
        softContraintsViolated[15] = new Tuple(SOFTCONSTRAINT16, 0);

    }
    /**
     * Returns -40 if group head does not have large room
     * Will not look at hard contraints!!!!
     * @param assignment of person(s) and room
     * @return penalty or 0
     */
    protected int SoftConstraint1(Assignment assignment){
        Person person = assignment.getPerson()[0];
        Room room = assignment.getRoom();

        if(person.isGroupHead && room.getSize() != 3){
            return -40;
        }
        return 0;
    }

    /**
     * Groups heads should be close to all members of their group
     * @param assignment
     * @return penalty of -2 or 0
     */
    protected int SoftConstraint2(Assignment assignment, Fact fact){
        Environment environment = Environment.get();
        Person person = assignment.getPerson()[0];
        String personID = person.getName();
        Room room = assignment.getRoom();
        String roomID = assignment.getRoom().getName();
        ArrayList<String> closeRooms = room.getCloseTo();
        int closePeople;
        if(!person.isGroupHead || assignment.getPerson().length != 1)
            return 0;
        else{
            int penalty = 0;
            //Get the group that person belongs to
            //for that group check if each memeber of group is "close"
            //iterate through the groups a person is a head of
            for (String grp : person.getGroupsHeaded()){
                closePeople = 0;
                ArrayList<Person> group = environment.getGroups().get(grp);
                //System.out.println(group.get(0));
                for (String closeRoom : closeRooms){
                    Person[] plebs = fact.getOccupants().get(closeRoom);
                    //if (plebs != null)
                    //{
                    //System.out.println(plebs[0]);
                    if (plebs != null) {
                        if (group.contains(plebs[0]))
                            closePeople++;
                        if (plebs.length == 2)
                            if (group.contains(plebs[1]))
                                closePeople++;

                        //}
                    }
                }
                penalty += -2 * (group.size() - closePeople - 1);
            }
            return penalty;
        }
    }

    /**
     * Group heads should be located close to at least one secretary in the group
     * @param assignment
     * @param fact
     * @return
     */
    protected int SoftConstraint3(Assignment assignment, Fact fact){
        Environment environment = Environment.get();
        Person person = assignment.getPerson()[0];
        Room room = assignment.getRoom();
        if(assignment.getPerson().length == 2 || !person.isGroupHead)
            return 0;
        boolean closeTo = false;
        int penalty = 0;
        for(String grp : person.getGroupsHeaded()){
            //iterate through members of group
            ArrayList<Person> members = environment.getGroups().get(grp);
            members.remove(person); //remove itself
            for (Person member : members){
                if(member.isSecretary){
                    //we need the rooms of the person and memeber, in fact
                    if (environment.e_close(room.getName(), fact.getHousing().get(member).getName())){
                        closeTo = true;
                        break;
                    }
                }
            }
            if (!closeTo)
                penalty -= 30;
            closeTo = false;
        }
        return penalty;
    }

    protected int SoftConstraint4(Assignment assignment){
        Person[] persons = assignment.getPerson();
        if (persons.length == 1)
            return (persons[0].isSecretary) ? -5 : 0;
        if (!persons[0].isSecretary && !persons[1].isSecretary)
            return 0;
        if (persons[0].isSecretary && persons[1].isSecretary)
            return 0;
        return -5;
    }

    /**
     *
     * @param assignment
     * @param allFact
     * @return
     */
    protected int SoftConstraint5(Assignment assignment, Fact allFact){
        Environment environment = Environment.get();
        Person person = assignment.getPerson()[0];
        Room room = assignment.getRoom();
        if(assignment.getPerson().length == 2 || !person.isManager)
            return 0;
        boolean closeTo = false;
        int penalty = 0;
        int secretaryCount =0;
        for(String grp : person.getGroupsPartOf()){//-------> //person.getGroupsManaged()){ <----- im not sure we implemented this

            //iterate through members of group
            ArrayList<Person> members = environment.getGroups().get(grp);
            members.remove(person); //remove itself
            for (Person member : members){
                if (member == null){
                    break;
                }
                if(member.isSecretary){
                    String roomName = allFact.getHousing().get(member).getName();
                    if (environment.e_close(room.getName(), roomName)){
                        closeTo = true;
                        secretaryCount++;
                        break;
                    }
                }
            }
            if (!closeTo && secretaryCount > 0)
                penalty -= 20;
            secretaryCount = 0;
            closeTo = false;
        }
        return penalty;
    }

    /**
     * Managers must be close to their groups head.
     * @param assignment
     * @param fact
     * @return
     */
    protected int SoftConstraint6(Assignment assignment, Fact fact){

        int penalty = 0;
        Environment environment = Environment.get();
        if(assignment.getPerson().length == 2)
            return 0;
        Person person = assignment.getPerson()[0];
        Room room = assignment.getRoom();

        if(person.isManager){

            for(String grps : person.getGroupsPartOf()){

                ArrayList<Person> groupMembers = environment.getGroups().get(grps);
                for(Person groupHead: groupMembers){
                    if(groupHead.isGroupHead){
                        if(!(environment.e_close(fact.getHousing().get(groupHead).getName(), room.getName()))){
                            penalty -= 20;
                        }
                    }
                }
            }

            assignment.setScore(penalty + assignment.getScore());
            return penalty;
        }
        return 0;

        //iterate through


    }

    /**
     * Managers should be close to all group members
     * @param assignment
     * @return
     */
    protected int SoftConstraint7(Assignment assignment, Fact fact){

        int penalty = 0;
        if(assignment.getPerson().length == 2){
            return 0;
        }
        Environment environment = Environment.get();
        Person person = assignment.getPerson()[0];
        Room room = assignment.getRoom();

        if(person.isManager){
            for (String grps: person.getGroupsPartOf()){
                ArrayList<Person> groupMembers = environment.getGroups().get(grps);
                for (Person member : groupMembers){
                    if(!environment.e_close(fact.getHousing().get(member).getName(), room.getName())){
                        if (!person.equals(member))
                            penalty -= 2;
                    }
                }
            }
            return penalty;
        }
        return 0;

    }

    protected int SoftConstraint8(Assignment assignment, Fact fact){

        int penalty = 0;
        if(assignment.getPerson().length == 2){
            return 0;
        }
        Environment environment = Environment.get();
        Person person = assignment.getPerson()[0];
        Room room = assignment.getRoom();

        if(person.projectHead){

            for(String prj : person.getProjectsHeaded()){
                ArrayList<Person> projectMembers = environment.getProjects().get(prj);
                for (Person member : projectMembers){
                    if(!environment.e_close(fact.getHousing().get(member).getName(), room.getName())){
                        penalty -= 5;
                    }
                }
            }
            return penalty;
        }
        return 0;



    }
    //CHECK THIS
    protected int SoftConstraint9(Assignment assignment, Fact allFact){
        Environment environment = Environment.get();
        Person person = assignment.getPerson()[0];
        Room room = assignment.getRoom();
        if(assignment.getPerson().length == 2 || !person.isGroupHead)
            return 0;
        boolean closeTo = false;
        int penalty = 0;
        int secretaryCount = 0;
        for(String grp : person.getGroupsHeaded()){//-------> //person.getGroupsManaged()){ <----- im not sure we implemented this

            //iterate through members of group
            ArrayList<Person> members = environment.getGroups().get(grp);
            members.remove(person); //remove itself
            for (Person member : members){
                if(member.isSecretary){
                    if (environment.e_close(room.getName(), allFact.getHousing().get(member).getName())){
                        closeTo = true;
                        secretaryCount++;
                        break;
                    }
                }
            }
            if (!closeTo && secretaryCount > 0)
                penalty -= 10;
            secretaryCount = 0;
            closeTo = false;
        }
        return penalty;

    }

    protected int SoftConstraint10(Assignment assignment, Fact fact){
        Environment environment = Environment.get();
        Person person = assignment.getPerson()[0];
        Room room = assignment.getRoom();
        int penalty = 0;
        ArrayList<String> groups = person.getGroupsPartOf();
        for (String prj : person.getProjectsHeaded()) {
            if (environment.e_large_project(prj)){
                for (String grp : groups) {
                    ArrayList<Person> groupMembers = environment.getGroups().get(grp);
                    for (Person member : groupMembers) {
                        if (member.getGroupsHeaded().contains(grp)) {
                            if (!environment.e_close(fact.getHousing().get(member).getName(), room.getName())){
                                penalty -= 10;
                            }
                            break;
                        }
                    }
                }
            }
        }
        return penalty;
    }

    protected int SoftConstraint11(Assignment assignment){
        int penalty = 0;
        if(assignment.getPerson().length != 2){
            return 0;
        }
        Environment environment = Environment.get();
        Person person = assignment.getPerson()[0];
        Person person1 = assignment.getPerson()[1];
        Room room = assignment.getRoom();

        if( (person.isSmoker && !person1.isSmoker) || (!person.isSmoker && person1.isSmoker)){
            return -50;
        }
        return 0;


    }

    protected int SoftConstraint12(Assignment assignment){
        int penalty = 0;
        if(assignment.getPerson().length != 2){
            return 0;
        }
        Environment environment = Environment.get();
        Person person = assignment.getPerson()[0];
        Person person1 = assignment.getPerson()[1];
        Room room = assignment.getRoom();

        ArrayList<String> personProjects = person.getProjects();
        ArrayList<String> person1Projects = person1.getProjects();

        for(String prj : personProjects){

            for(String prj1 : person1Projects){

                if(prj.equals(prj1))
                    penalty -= 7;

            }

        }
        return penalty;
    }

    protected int SoftConstraint13(Assignment assignment){
        Person[] person = assignment.getPerson();
        if (person.length != 2){
            return 0;
        }
        if (!person[0].isSecretary || !person[1].isSecretary){
            if (person[0].isHacker != person[1].isHacker){
                return -1;
            }
        }

        return 0;
    }
    protected int SoftConstraint14(Assignment assignment){

        if(assignment.getPerson().length != 2){
            return 0;
        }
        else{
            return -4;
        }

    }

    protected int SoftConstraint15(Assignment assignment){

        if(assignment.getPerson().length != 2){
            return 0;
        }
        Environment environment = Environment.get();
        Person[] person = assignment.getPerson();


        if(!environment.e_works_with(person[0].getName(), person[1].getName())){
            return -3;

        }
        return 0;
    }

    protected int SoftConstraint16(Assignment assignment){
        if(assignment.getPerson().length != 2){
            return 0;
        }
        Room room = assignment.getRoom();

        if(room.getSize() == 1){
            return -25;
        }
        return 0;
    }


}
