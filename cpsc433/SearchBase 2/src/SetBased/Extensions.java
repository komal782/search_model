package SetBased;

import Facts.*;
import com.sun.tools.doclint.Env;

import java.lang.reflect.Array;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.StringJoiner;
import java.lang.Math;

/**
 * Created by shado on 11/30/2016.
 */
public class Extensions {

    //extension rules here
    static public Fact Rule1(Fact fact) {
        boolean solved = false;
        Environment environment = Environment.get();
        ArrayList<Assignment> assignments = new ArrayList<>();
        ArrayDeque<String> largeRooms = environment.getLargeRooms();
        ArrayList<Room> spareRooms = fact.getSpareRooms();
        PriorityQueue<Assignment> priorityQueue = fact.getAssignmentPriorityQueue();
        while (!priorityQueue.isEmpty()) {
            Assignment temp = priorityQueue.remove();
            if (temp.getSoftContraint() == ConstraintID.SOFTCONSTRAINT1) {
                for (Room room : spareRooms) {
                    if (room.getSize() == 3) {
                        spareRooms.remove(room);
                        assignments.add(new Assignment(room, temp.getPerson()[0]));
                        solved = true;
                        break;
                    }
                }
                if (solved) {
                    for (String room : largeRooms) {
                        Room tempRoom = environment.getRoom(room);
                        Person[] persons = fact.getOccupants().get(tempRoom);
                        if (persons != null && persons.length == 2) {
                            swapRoom(temp, fact.getAssignment(tempRoom));
                            solved = true;
                            break;
                        }
                    }
                }
            }
            if (!solved)
                assignments.add(temp);
        }
        Fact f = new Fact(assignments, spareRooms);
        f.CalculateViolations();
        return f;
    }

    /**
     * group heads should be close to their members
     * @param fact
     * @return
     */
    static public Fact Rule2(Fact fact){
        boolean solved = false;
        Environment environment = Environment.get();
        ArrayList<Assignment> assignments = new ArrayList<>();
        PriorityQueue<Assignment> priorityQueue = fact.getAssignmentPriorityQueue();
        while (!priorityQueue.isEmpty()) {
            Assignment temp = priorityQueue.remove();
            //if (temp.getSoftContraint() == ConstraintID.SOFTCONSTRAINT2) {
                Person groupHead = temp.getPerson()[0];
                Room groupHeadRoom = temp.getRoom();
                if (!temp.getPerson()[0].isGroupHead)
                    break;
                for (String group : groupHead.getGroupsHeaded()){
                    ArrayList<Person> groupMembers = environment.getGroup(group);
                    for (Person member : groupMembers){
                        String memberRoom = member.getRoomName();
                        if (!groupHeadRoom.hasClose(memberRoom)){
                            break;
                        }
                    }
                }

           // }
        }
        return null;
    }

    static public Fact Rule3(Fact fact){
        boolean solved = false;
        Environment environment = Environment.get();
        ArrayList<Assignment> assignments = new ArrayList<>();
        PriorityQueue<Assignment> priorityQueue = fact.getAssignmentPriorityQueue();
        while (!priorityQueue.isEmpty()) {
            Assignment temp = priorityQueue.remove();

        }
        return null;
    }

    /**
     * secretaries share a room with each other
     * @param fact
     * @return
     */
    static public Fact Rule4(Fact fact){
        Boolean solved = false;
        ArrayList<Assignment> assignments = new ArrayList<>();
        PriorityQueue<Assignment> priorityQueue = fact.getAssignmentPriorityQueue();
        ArrayList<Room> spareRooms = fact.getSpareRooms();
        ArrayList<Assignment> mismatchSecretary = new ArrayList<Assignment>();
        ArrayList<Integer> mismatchSecretaryLocation = new ArrayList<Integer>();
        while (!priorityQueue.isEmpty()) {
            Assignment temp = priorityQueue.remove();
            //if (fact.getSoftConstraint() == ConstraintID.SOFTCONSTRAINT4) {
                //System.out.println(temp);
                if (!temp.getRoom().founderRoom){
                    Person[] secretary = temp.getPerson();
                    if (secretary.length == 1){
                        if (secretary[0].isSecretary) {
                            mismatchSecretary.add(temp);
                            mismatchSecretaryLocation.add(0);
                        }
                    }
                    else {
                        if (secretary[0].isSecretary) {
                            if (!secretary[1].isSecretary){
                                mismatchSecretary.add(temp);
                                mismatchSecretaryLocation.add(0);
                            }
                        }
                        else if (secretary[1].isSecretary){
                            mismatchSecretary.add(temp);
                            mismatchSecretaryLocation.add(1);
                        }
                    }
                }
            //}
            assignments.add(temp);
        }
        boolean matchedSecretaries = false;
        int i = 0;
        int x = (int) Math.floor(Math.random() * mismatchSecretary.size());
        while(!matchedSecretaries && (i < x)){
              if (i + 1 < mismatchSecretary.size()){
                  Assignment a1 = mismatchSecretary.get(i);
                  Assignment a2 = mismatchSecretary.get(i+1);
                  if (a1.getPerson().length == 1 && a2.getPerson().length == 1){
                      a1.addSecondPerson(a2.getPerson()[0]);
                      spareRooms.add(a2.getRoom());
                      assignments.remove(a2);
                  }
                  else if (a1.getPerson().length == 1){
                      a1.addSecondPerson(a2.getPerson()[mismatchSecretaryLocation.get(i+1)]);
                      a2.removePerson(a2.getPerson()[mismatchSecretaryLocation.get(i+1)]);
                  }
                  else if (a2.getPerson().length == 1){
                      a2.addSecondPerson(a1.getPerson()[mismatchSecretaryLocation.get(i)]);
                      a1.removePerson(a1.getPerson()[mismatchSecretaryLocation.get(i)]);
                  }
                  else if (mismatchSecretaryLocation.get(i).equals(1)){
                      //System.out.println("Assigment: " + a1 + " Person: " + mismatchSecretaryLocation.get(i));
                      swapPeople(mismatchSecretary.get(i), mismatchSecretary.get(i+1), mismatchSecretaryLocation.get(i) - 1, mismatchSecretaryLocation.get(i+1));
                  }
                  else if (mismatchSecretaryLocation.get(i+1).equals(1)){
                      swapPeople(mismatchSecretary.get(i), mismatchSecretary.get(i+1), mismatchSecretaryLocation.get(i), mismatchSecretaryLocation.get(i+1)-1);
                  }
              }
              else {
                  matchedSecretaries = true;
              }
              i += 2;
        }
        return new Fact(assignments, spareRooms);
    }

    /**
     * swaps the person to be closer to their manager or group
     * @param fact
     * @return
     */
    static public Fact Rule7(Fact fact){
        PriorityQueue<Assignment> priorityQueue = fact.getAssignmentPriorityQueue();
        while (!priorityQueue.isEmpty()) {
            Assignment temp = priorityQueue.remove();
            Room managerRoom = temp.getRoom();
            if (temp.getSoftContraint() == ConstraintID.SOFTCONSTRAINT7) {
                Person[] smoker = temp.getPerson();
                for (Person person : temp.getPerson()) {
                    if (person.isManager){
                        ArrayList<String> closeMembers = managerRoom.getCloseTo();
                    }
                }
            }
        }
        return null;
    }

    /**
     * Swaps the smokers if they share a room with a non smoker
     * @param fact
     * @return
     */
    static public Fact Rule11(Fact fact) {
        ArrayList<Assignment> assignments = new ArrayList<>();
        PriorityQueue<Assignment> priorityQueue = fact.getAssignmentPriorityQueue();
        ArrayList<Room> spareRooms = fact.getSpareRooms();
        ArrayList<Assignment> mismatchSmoker = new ArrayList<Assignment>();
        ArrayList<Integer> mismatchSmokerLocation = new ArrayList<Integer>();
        while (!priorityQueue.isEmpty()) {
            Assignment temp = priorityQueue.remove();
            //if (fact.getSoftConstraint() == ConstraintID.SOFTCONSTRAINT11) {
            //System.out.println(temp);
            if (!temp.getRoom().founderRoom){
                Person[] secretary = temp.getPerson();
                if (secretary.length == 1){
                    if (secretary[0].isSmoker) {
                        mismatchSmoker.add(temp);
                        mismatchSmokerLocation.add(0);
                    }
                }
                else {
                    if (secretary[0].isSmoker) {
                        if (!secretary[1].isSmoker){
                            mismatchSmoker.add(temp);
                            mismatchSmokerLocation.add(0);
                        }
                    }
                    else if (secretary[1].isSmoker){
                        mismatchSmoker.add(temp);
                        mismatchSmokerLocation.add(1);
                    }
                }
            }
            //}
            assignments.add(temp);
        }
        boolean matchedSmokers = false;
        int i = 0;
        int x = (int) Math.floor(Math.random() * mismatchSmoker.size());
        while(!matchedSmokers && (i < x)){
            if (i + 1 < mismatchSmoker.size()){
                Assignment a1 = mismatchSmoker.get(i);
                Assignment a2 = mismatchSmoker.get(i+1);
                if (a1.getPerson().length == 1 && a2.getPerson().length == 1){
                    a1.addSecondPerson(a2.getPerson()[0]);
                    spareRooms.add(a2.getRoom());
                    assignments.remove(a2);
                }
                else if (a1.getPerson().length == 1){
                    a1.addSecondPerson(a2.getPerson()[mismatchSmokerLocation.get(i+1)]);
                    a2.removePerson(a2.getPerson()[mismatchSmokerLocation.get(i+1)]);
                }
                else if (a2.getPerson().length == 1){
                    a2.addSecondPerson(a1.getPerson()[mismatchSmokerLocation.get(i)]);
                    a1.removePerson(a1.getPerson()[mismatchSmokerLocation.get(i)]);
                }
                else if (mismatchSmokerLocation.get(i).equals(1)){
                    swapPeople(mismatchSmoker.get(i), mismatchSmoker.get(i+1), mismatchSmokerLocation.get(i) - 1, mismatchSmokerLocation.get(i+1));
                }
                else if (mismatchSmokerLocation.get(i+1).equals(1)){
                    swapPeople(mismatchSmoker.get(i), mismatchSmoker.get(i+1), mismatchSmokerLocation.get(i), mismatchSmokerLocation.get(i+1)-1);
                }
            }
            else {
                matchedSmokers = true;
            }
            i += 2;
        }
        return new Fact(assignments, spareRooms);
    }

    static public Fact Rule15(Fact fact) {
        Boolean solved = false;
        Environment environment = Environment.get();
        ArrayList<Assignment> assignments = new ArrayList<>();
        PriorityQueue<Assignment> priorityQueue = fact.getAssignmentPriorityQueue();
        ArrayList<Room> spareRooms = fact.getSpareRooms();
        while (!priorityQueue.isEmpty()) {
            Assignment temp = priorityQueue.remove();
            if (temp.getSoftContraint() == ConstraintID.SOFTCONSTRAINT4) {
                Person[] person = temp.getPerson();
                if (person.length == 2){
                    if (person[0].getProjects() != person[1].getProjects()){
                        //get another assignment
                        for (Person p : temp.getPerson()){
                            //get check if that person has the same project as the other person
                            if(person[0].getProjects() == p.getProjects()){
                                //put person p into the room
                                //remove that person[1] from the room
                            }
                            else if(person[1].getProjects() == p.getProjects()) {
                                //put person p into the room
                                //remove person[1] form the room
                            }
                        }
                    }
                }

            }
        }
        return null;
    }

    static public Fact Rule16(Fact fact) {
        Boolean solved = false;
        Environment environment = Environment.get();
        ArrayList<Assignment> assignments = new ArrayList<>();
        PriorityQueue<Assignment> priorityQueue = fact.getAssignmentPriorityQueue();
        ArrayList<Room> spareRooms = fact.getSpareRooms();
        while (!priorityQueue.isEmpty()) {
            Assignment temp = priorityQueue.remove();
            if (temp.getSoftContraint() == ConstraintID.SOFTCONSTRAINT4) {
                if (temp.getRoom().getSize() == 2 && environment.e_small_room(temp.getRoom().getName()) == true){
                    if(!spareRooms.isEmpty()){
                        for (Room room : spareRooms){
                            if (room.size()){

                            }
                        }
                        //get a large or medium room then add people
                        //remove the people
                        // and ad these people to the spare room
                    }
                    else if (spareRooms.isEmpty()){
                        //get one person move them into a small spare room
                        //if not a small room then the next medium room
                    }
                }

            }
        }
        return null;
    }


    private static void swapRoom(Assignment a1, Assignment a2){
        Room room = a1.getRoom();
        Room room2 = a2.getRoom();

        a1.setRoom(room2);
        a2.setRoom(room);
    }
    private static void swapPeople(Assignment a1, Assignment a2, int person, int person2){
        Person[] people = a1.getPerson();
        Person[] people1 = a2.getPerson();
        Person toSwap;
        if (people.length < person + 1 || people1.length < person2 + 1)
            return;
        if (people[person] == null || people1[person2] == null)
            return;
        toSwap = people[person];
        a1.setPerson(people[person], people1[person2]);
        a2.setPerson(people1[person2], toSwap);
    }
    /*private static void swapPeople(Assignment a1, Assignment a2){
        Person person1 = a1.getPerson()[0];
        Person person2 = a1.getPerson()[1];
        Person person3 = a2.getPerson()[0];
        Person person4 = a2.getPerson()[1];
        Person toSwap;
        if (person1.isSecretary){
            toSwap = person1;
            if (person3.isSecretary){
                a1.setPerson1(person3);
                a2.setPerson1(toSwap);
            }
            else {
                a1.setPerson1(person4);
                a2.setPerson2(toSwap);
            }
        }
        else {
            toSwap = person2;
            if (person3.isSecretary){
                a1.setPerson2(person3);
                a2.setPerson1(toSwap);
            }
            else {
                a1.setPerson2(person4);
                a2.setPerson2(toSwap);
            }
        }

    }*/

    private static void swapPeople2(Assignment a1, Assignment a2){
        Person person1 = a1.getPerson()[0];
        Person person2 = a1.getPerson()[1];
        Person person3 = a2.getPerson()[0];
        Person person4 = a2.getPerson()[1];
        Person toSwap;
        if (person1.isSmoker){
            toSwap = person1;
            if (person3.isSmoker){
                a1.setPerson1(person3);
                a2.setPerson1(toSwap);
            }
            else {
                a1.setPerson1(person4);
                a2.setPerson2(toSwap);
            }
        }
        else {
            toSwap = person2;
            if (person3.isSmoker){
                a1.setPerson2(person3);
                a2.setPerson1(toSwap);
            }
            else {
                a1.setPerson2(person4);
                a2.setPerson2(toSwap);
            }
        }

    }

}
