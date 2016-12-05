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
        int numberToFix = (int) Math.floor(Math.random() * (assignments.size()/2));
        int numberFixed = 0;
        while (!priorityQueue.isEmpty()) {
            Assignment temp = priorityQueue.remove();
            if (!temp.getRoom().founderRoom && (numberFixed <= numberToFix)){
                Person[] person = temp.getPerson();
                if (person.length == 2){
                    if (!environment.e_works_with(person[0].getName(), person[1].getName())){
                        int x = (int) Math.floor(Math.random() * 10);
                        Person personToFix = temp.getPerson()[(x < 5) ? 0 : 1];
                        boolean fixed = false;
                        for (String coworker : personToFix.getCoworkers()){
                            Assignment assignment = fact.getAssignment(environment.getPerson(coworker));
                            Person[] people = assignment.getPerson();
                            Room room = assignment.getRoom();
                            if (!room.founderRoom){
                                Person theCow = (people[0].getName().equals(coworker)) ? people[0]: people[1];
                                if (!(theCow.isManager || theCow.projectHead || theCow.isGroupHead)) {
                                    if (people.length == 2) {
                                        swapPeople(temp, assignment, (x < 5) ? 1 : 0, (people[0].getName().equals(coworker)) ? 0: 1);
                                    } else {
                                        assignment.addSecondPerson(personToFix);
                                        temp.removePerson(personToFix);
                                    }
                                    fixed = true;
                                }
                            }
                            if (fixed)
                                break;
                        }
                        if (!fixed){
                            personToFix = temp.getPerson()[(x < 5) ? 1 : 0];
                            for (String coworker : personToFix.getCoworkers()){
                                Assignment assignment = fact.getAssignment(environment.getPerson(coworker));
                                Person[] people = assignment.getPerson();
                                Room room = assignment.getRoom();
                                if (!room.founderRoom){
                                    Person theCow = (people[0].getName().equals(coworker)) ? people[0]: people[1];
                                    if (!(theCow.isManager || theCow.projectHead || theCow.isGroupHead)) {
                                        if (people.length == 2) {
                                            swapPeople(temp, assignment, (x < 5) ? 1 : 0, (people[0].getName().equals(coworker)) ? 0: 1);
                                        } else {
                                            assignment.addSecondPerson(personToFix);
                                            temp.removePerson(personToFix);
                                        }
                                        fixed = true;
                                    }
                                }
                                if (fixed)
                                    break;
                            }
                        }
                    }
                }
            }
            assignments.add(temp);
        }
        return new Fact(assignments, spareRooms);
    }

    static public Fact Rule16(Fact fact) {
        Environment environment = Environment.get();
        ArrayList<Assignment> assignments = new ArrayList<>();
        PriorityQueue<Assignment> priorityQueue = fact.getAssignmentPriorityQueue();
        ArrayList<Room> spareRooms = fact.getSpareRooms();
        int numberToFix = (int) Math.floor(Math.random() * (assignments.size()/2));
        int numberFixed = 0;
        while (!priorityQueue.isEmpty()) {
            Assignment temp = priorityQueue.remove();
            if (!temp.getRoom().founderRoom && (numberFixed <= numberToFix)){
                if (temp.getRoom().getSize() == 1 && temp.getPerson().length == 2){
                    if(!spareRooms.isEmpty()){
                        for (Room room : spareRooms){
                            if (room.getSize() > 1){
                                int x = (int) Math.floor(Math.random() * 10);
                                Person personToMove = temp.getPerson()[(x < 5) ? 0 : 1];
                                //System.out.println(x);
                                Assignment newAssignment = new Assignment(room, personToMove);
                                temp.removePerson(personToMove);
                                assignments.add(newAssignment);
                                spareRooms.remove(room);
                                numberFixed++;
                                break;
                            }
                        }
                    }
                    else {
                        for (String largeRoom : environment.getLargeRooms()){
                            Room room = environment.getRoom(largeRoom);
                            if (!room.founderRoom){
                                Assignment assignment = fact.getAssignment(room);
                                Person[] people = assignment.getPerson();
                                if (people.length == 1 && !(people[0].isGroupHead || people[0].isManager || people[0].projectHead)){
                                    int x = (int) Math.floor(Math.random() * 10);
                                    Person personToMove = temp.getPerson()[(x < 5) ? 0 : 1];
                                    temp.removePerson(personToMove);
                                    assignment.addSecondPerson(personToMove);
                                    numberFixed++;
                                }
                            }
                        }
                        for (String mediumRoom : environment.getMediumRooms()){
                            Room room = environment.getRoom(mediumRoom);
                            if (!room.founderRoom){
                                Assignment assignment = fact.getAssignment(room);
                                Person[] people = assignment.getPerson();
                                if (people.length == 1 && !(people[0].isGroupHead || people[0].isManager || people[0].projectHead)){
                                    int x = (int) Math.floor(Math.random() * 10);
                                    Person personToMove = temp.getPerson()[(x < 5) ? 0 : 1];
                                    temp.removePerson(personToMove);
                                    assignment.addSecondPerson(personToMove);
                                    numberFixed++;
                                }
                            }
                        }
                    }
                }
            }
            assignments.add(temp);
        }
        return new Fact(assignments, spareRooms);
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

}
