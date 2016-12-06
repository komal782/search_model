package SetBased;

import Facts.*;
import com.sun.tools.doclint.Env;
import cpsc433.SisyphusPredicates;

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
        boolean dontAdd = false;
        while (!priorityQueue.isEmpty()) {
            Assignment temp = priorityQueue.remove();
            //if (temp.getSoftContraint() == ConstraintID.SOFTCONSTRAINT1) {
                Person groupHead = temp.getPerson()[0];
                if (groupHead.isGroupHead && !groupHead.isFounder) {
                    for (Room room : spareRooms) {
                        if (room.getSize() == 3) {
                            spareRooms.remove(room);
                            assignments.add(new Assignment(room, groupHead));
                            spareRooms.add(temp.getRoom());
                            dontAdd = true;
                            solved = true;
                            break;
                        }
                    }
                    if (!solved) {
                        for (String room : largeRooms) {
                            Room tempRoom = environment.getRoom(room);
                            if (!tempRoom.founderRoom) {
                                Person[] persons = fact.getOccupants().get(tempRoom);
                                if (persons != null && persons.length == 2) {
                                    swapRoom(temp, fact.getAssignment(tempRoom));
                                    solved = true;
                                    break;
                                }
                            }
                        }
                    }
                }
            //}
            if (!dontAdd)
                assignments.add(temp);
            solved = false;
            dontAdd = false;
        }
        Fact f = new Fact(assignments, spareRooms);
        return f;
    }

    static public Fact Rule2(Fact fact){
        boolean solved = false;
        Environment environment = Environment.get();
        ArrayList<Room> spareRooms = fact.getSpareRooms();
        ArrayList<Assignment> assignments = new ArrayList<>();
        PriorityQueue<Assignment> priorityQueue = fact.getAssignmentPriorityQueue();
        while (!priorityQueue.isEmpty()) {
            Assignment temp = priorityQueue.remove();
            //if (!temp.getRoom().founderRoom) {
                Person groupHead = temp.getPerson()[0];
                Room groupHeadRoom = temp.getRoom();
                if (temp.getPerson()[0].isGroupHead) {
                    // every group headed by this group head
                    ArrayList<String> groups = groupHead.getGroupsHeaded();

                    // for every room close to this group head
                    for (String room : groupHeadRoom.getCloseTo()){
                        //the close room
                        Room closeRoom = environment.getRoom(room);
                        //if the room has no assignment
                        if (fact.isSpare(closeRoom)){
                            // for every group headed by this group head
                            for (String thisGroup : groups) {

                                // group members
                                ArrayList<Person> maybeFarGroupMembers = environment.getGroup(thisGroup);
                                // for every group member in this group
                                for (Person maybeFarGroupMember : maybeFarGroupMembers) {
                                    if (!groupHead.equals(maybeFarGroupMember) && !maybeFarGroupMember.isFounder) {
                                        Assignment maybeFarAssignment = fact.getAssignment(maybeFarGroupMember);
                                        Room maybeFarRoom = maybeFarAssignment.getRoom();

                                        Person[] maybeFarPeople = maybeFarAssignment.getPerson();
                                        if (maybeFarPeople.length == 2){
                                            maybeFarAssignment.removePerson(maybeFarGroupMember);
                                            assignments.add(new Assignment(closeRoom, maybeFarGroupMember));
                                        }
                                        else {
                                            assignments.add(new Assignment(closeRoom, maybeFarGroupMember));
                                            spareRooms.add(maybeFarAssignment.getRoom());
                                            assignments.remove(maybeFarAssignment);
                                            priorityQueue.remove(maybeFarAssignment);
                                        }
                                        solved = true;
                                    }
                                    if (solved)
                                        break;
                                }
                                if (solved)
                                    break;
                            }
                        }
                        else if (!closeRoom.founderRoom){
                            Assignment closeAssignment = fact.getAssignment(closeRoom);
                            Person[] closePeople = closeAssignment.getPerson();
                            boolean personOneInGroup = false;
                            boolean personTwoInGroup = false;

                            // for each group headed by the group head
                            for (String grp : groups){
                                if (closePeople[0].isInGroup(grp)){
                                    personOneInGroup = true;
                                }
                                if (closePeople.length == 2){
                                    if (closePeople[1].isInGroup(grp)){
                                        personTwoInGroup = true;
                                    }
                                }
                                if (personOneInGroup && personTwoInGroup)
                                    break;
                            }
                            Person personToSwap = null;
                            boolean swap = false;
                            if (!personOneInGroup && !personTwoInGroup){
                                swap = true;
                                int randomPerson = 0;
                                if (closePeople.length == 2){
                                    randomPerson = (int) Math.floor(Math.random() * 10);
                                    randomPerson = (randomPerson > 5) ? 1: 0;
                                }
                                personToSwap = closePeople[randomPerson];
                            }
                            else if (!personOneInGroup){
                                swap = true;
                                personToSwap = closePeople[0];
                            }
                            else if (!personTwoInGroup){
                                if (closePeople.length == 2) {
                                    swap = true;
                                    personToSwap = closePeople[1];
                                }
                            }
                            if (swap){
                                for (String group : groups) {
                                    ArrayList<Person> maybeFarGroupMembers = environment.getGroup(group);
                                    for (Person maybeFarGroupMember : maybeFarGroupMembers){
                                        if (!groupHead.equals(maybeFarGroupMember) && !maybeFarGroupMember.isFounder){
                                            Assignment maybeFarAssignment = fact.getAssignment(maybeFarGroupMember);
                                            if ((personToSwap.isManager || personToSwap.isGroupHead || personToSwap.projectHead) && (maybeFarGroupMember.isManager || maybeFarGroupMember.isGroupHead || maybeFarGroupMember.projectHead)){
                                                swapByPeople(closeAssignment, maybeFarAssignment, personToSwap, maybeFarGroupMember);
                                                solved = true;
                                            }
                                            else if (!(personToSwap.isManager || personToSwap.isGroupHead || personToSwap.projectHead) && !(maybeFarGroupMember.isManager || maybeFarGroupMember.isGroupHead || maybeFarGroupMember.projectHead)){
                                                swapByPeople(closeAssignment, maybeFarAssignment, personToSwap, maybeFarGroupMember);
                                                solved = true;
                                            }
                                            else {
                                                swapRoom(closeAssignment, maybeFarAssignment);
                                                solved = true;
                                            }
                                        }
                                        if (solved)
                                            break;
                                    }
                                    if (solved)
                                        break;
                                }
                            }
                        }
                        if (solved)
                            break;
                    }
                }
            //}
            assignments.add(temp);
        }
        return new Fact(assignments, spareRooms);
    }

    /**
     * group heads should be close to their members
     * @param fact
     * @return
     */
    static public Fact Rule3(Fact fact){
        boolean solved = false;
        Environment environment = Environment.get();
        ArrayList<Room> spareRooms = fact.getSpareRooms();
        ArrayList<Assignment> assignments = new ArrayList<>();
        PriorityQueue<Assignment> priorityQueue = fact.getAssignmentPriorityQueue();
        while (!priorityQueue.isEmpty()) {
            Assignment temp = priorityQueue.remove();
            //if (!temp.getRoom().founderRoom) {
            Person groupHead = temp.getPerson()[0];
            Room groupHeadRoom = temp.getRoom();
            if (temp.getPerson()[0].isGroupHead) {
                // every group headed by this group head
                ArrayList<String> groups = groupHead.getGroupsHeaded();

                // for every room close to this group head
                for (String room : groupHeadRoom.getCloseTo()){
                    //the close room
                    Room closeRoom = environment.getRoom(room);
                    //if the room has no assignment
                    if (fact.isSpare(closeRoom)){
                        // for every group headed by this group head
                        for (String thisGroup : groups) {

                            // group members
                            ArrayList<Person> maybeFarGroupMembers = environment.getGroup(thisGroup);
                            // for every group member in this group
                            for (Person maybeFarGroupMember : maybeFarGroupMembers) {
                                if (!groupHead.equals(maybeFarGroupMember) && !maybeFarGroupMember.isFounder) {
                                    if (maybeFarGroupMember.isSecretary){
                                        Assignment maybeFarAssignment = fact.getAssignment(maybeFarGroupMember);

                                        Person[] maybeFarPeople = maybeFarAssignment.getPerson();
                                        if (maybeFarPeople.length == 2){
                                            maybeFarAssignment.removePerson(maybeFarGroupMember);
                                            assignments.add(new Assignment(closeRoom, maybeFarGroupMember));
                                        }
                                        else {
                                            assignments.add(new Assignment(closeRoom, maybeFarGroupMember));
                                            spareRooms.add(maybeFarAssignment.getRoom());
                                            assignments.remove(maybeFarAssignment);
                                            priorityQueue.remove(maybeFarAssignment);
                                        }
                                        solved = true;
                                    }
                                }
                                if (solved)
                                    break;
                            }
                            if (solved)
                                break;
                        }
                    }
                    else if (!closeRoom.founderRoom){
                        Assignment closeAssignment = fact.getAssignment(closeRoom);
                        Person[] closePeople = closeAssignment.getPerson();
                        boolean personOneInGroup = false;
                        boolean personTwoInGroup = false;

                        // for each group headed by the group head
                        for (String grp : groups){
                            if (closePeople[0].isInGroup(grp)){
                                personOneInGroup = true;
                            }
                            if (closePeople.length == 2){
                                if (closePeople[1].isInGroup(grp)){
                                    personTwoInGroup = true;
                                }
                            }
                            if (personOneInGroup && personTwoInGroup)
                                break;
                        }
                        Person personToSwap = null;
                        boolean swap = false;
                        if (!personOneInGroup && !personTwoInGroup){
                            swap = true;
                            int randomPerson = 0;
                            if (closePeople.length == 2){
                                randomPerson = (int) Math.floor(Math.random() * 10);
                                randomPerson = (randomPerson > 5) ? 1: 0;
                            }
                            personToSwap = closePeople[randomPerson];
                        }
                        else if (!personOneInGroup){
                            swap = true;
                            personToSwap = closePeople[0];
                        }
                        else if (!personTwoInGroup){
                            if (closePeople.length == 2) {
                                swap = true;
                                personToSwap = closePeople[1];
                            }
                        }
                        if (swap){
                            for (String group : groups) {
                                ArrayList<Person> maybeFarGroupMembers = environment.getGroup(group);
                                for (Person maybeFarGroupMember : maybeFarGroupMembers){
                                    if (!groupHead.equals(maybeFarGroupMember) && !maybeFarGroupMember.isFounder && maybeFarGroupMember.isSecretary){
                                        Assignment maybeFarAssignment = fact.getAssignment(maybeFarGroupMember);
                                        if ((personToSwap.isManager || personToSwap.isGroupHead || personToSwap.projectHead) && (maybeFarGroupMember.isManager || maybeFarGroupMember.isGroupHead || maybeFarGroupMember.projectHead)){
                                            swapByPeople(closeAssignment, maybeFarAssignment, personToSwap, maybeFarGroupMember);
                                            solved = true;
                                        }
                                        else if (!(personToSwap.isManager || personToSwap.isGroupHead || personToSwap.projectHead) && !(maybeFarGroupMember.isManager || maybeFarGroupMember.isGroupHead || maybeFarGroupMember.projectHead)){
                                            swapByPeople(closeAssignment, maybeFarAssignment, personToSwap, maybeFarGroupMember);
                                            solved = true;
                                        }
                                        else {
                                            swapRoom(closeAssignment, maybeFarAssignment);
                                            solved = true;
                                        }
                                    }
                                    if (solved)
                                        break;
                                }
                                if (solved)
                                    break;
                            }
                        }
                    }
                    if (solved)
                        break;
                }
            }
            //}
            assignments.add(temp);
        }
        return new Fact(assignments, spareRooms);
    }


    /**
     * secretaries share a room with each other
     * @param fact
     * @return
     */
    static public Fact Rule4(Fact fact){
        Boolean solved = false;
     //   System.out.print("Rule 4: ");
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
                  if (!(a1.getPerson()[0].isManager || a1.getPerson()[0].isGroupHead || a1.getPerson()[0].projectHead) && !a1.getRoom().founderRoom && !(a2.getPerson()[0].isManager || a2.getPerson()[0].isGroupHead || a2.getPerson()[0].projectHead) && !a2.getRoom().founderRoom) {
                      if (a1.getPerson().length == 1 && a2.getPerson().length == 1) {
                          a1.addSecondPerson(a2.getPerson()[0]);
                          spareRooms.add(a2.getRoom());
                          priorityQueue.remove(a2);
                          assignments.remove(a2);
                      } else if (a1.getPerson().length == 1) {
                          a1.addSecondPerson(a2.getPerson()[mismatchSecretaryLocation.get(i + 1)]);
                          a2.removePerson(a2.getPerson()[mismatchSecretaryLocation.get(i + 1)]);
                      } else if (a2.getPerson().length == 1) {
                          a2.addSecondPerson(a1.getPerson()[mismatchSecretaryLocation.get(i)]);
                          a1.removePerson(a1.getPerson()[mismatchSecretaryLocation.get(i)]);
                      } else if (mismatchSecretaryLocation.get(i).equals(1)) {
                          //System.out.println("Assigment: " + a1 + " Person: " + mismatchSecretaryLocation.get(i));
                          swapPeople(mismatchSecretary.get(i), mismatchSecretary.get(i + 1), mismatchSecretaryLocation.get(i) - 1, mismatchSecretaryLocation.get(i + 1));
                      } else if (mismatchSecretaryLocation.get(i + 1).equals(1)) {
                          swapPeople(mismatchSecretary.get(i), mismatchSecretary.get(i + 1), mismatchSecretaryLocation.get(i), mismatchSecretaryLocation.get(i + 1) - 1);
                      }
                  }
              }
              else {
                  matchedSecretaries = true;
              }
              i += 2;
        }
        return new Fact(assignments, spareRooms);
    }

    static public Fact Rule5(Fact fact){
        boolean solved = false;
        Environment environment = Environment.get();
        ArrayList<Room> spareRooms = fact.getSpareRooms();
        ArrayList<Assignment> assignments = new ArrayList<>();
        PriorityQueue<Assignment> priorityQueue = fact.getAssignmentPriorityQueue();
        while (!priorityQueue.isEmpty()) {
            Assignment temp = priorityQueue.remove();
            //if (!temp.getRoom().founderRoom) {
            Person manager = temp.getPerson()[0];
            Room managerRoom = temp.getRoom();
            if (temp.getPerson()[0].isManager) {
                // every group headed by this group head
                ArrayList<String> groups = manager.getGroupsPartOf();

                // for every room close to this group head
                for (String room : managerRoom.getCloseTo()){
                    //the close room
                    Room closeRoom = environment.getRoom(room);
                    //if the room has no assignment
                    if (fact.isSpare(closeRoom)){
                        // for every group headed by this group head
                        for (String thisGroup : groups) {

                            // group members
                            ArrayList<Person> maybeFarGroupMembers = environment.getGroup(thisGroup);
                            // for every group member in this group
                            for (Person maybeFarGroupMember : maybeFarGroupMembers) {
                                if (!manager.equals(maybeFarGroupMember) && !maybeFarGroupMember.isFounder) {
                                    if (maybeFarGroupMember.isSecretary){
                                        Assignment maybeFarAssignment = fact.getAssignment(maybeFarGroupMember);

                                        Person[] maybeFarPeople = maybeFarAssignment.getPerson();
                                        if (maybeFarPeople.length == 2){
                                            maybeFarAssignment.removePerson(maybeFarGroupMember);
                                            assignments.add(new Assignment(closeRoom, maybeFarGroupMember));
                                        }
                                        else {
                                            assignments.add(new Assignment(closeRoom, maybeFarGroupMember));
                                            spareRooms.add(maybeFarAssignment.getRoom());
                                            assignments.remove(maybeFarAssignment);
                                            priorityQueue.remove(maybeFarAssignment);
                                        }
                                        solved = true;
                                    }
                                }
                                if (solved)
                                    break;
                            }
                            if (solved)
                                break;
                        }
                    }
                    else if (!closeRoom.founderRoom){
                        Assignment closeAssignment = fact.getAssignment(closeRoom);
                        Person[] closePeople = closeAssignment.getPerson();
                        boolean personOneInGroup = false;
                        boolean personTwoInGroup = false;

                        // for each group headed by the group head
                        for (String grp : groups){
                            if (closePeople[0].isInGroup(grp)){
                                personOneInGroup = true;
                            }
                            if (closePeople.length == 2){
                                if (closePeople[1].isInGroup(grp)){
                                    personTwoInGroup = true;
                                }
                            }
                            if (personOneInGroup && personTwoInGroup)
                                break;
                        }
                        Person personToSwap = null;
                        boolean swap = false;
                        if (!personOneInGroup && !personTwoInGroup){
                            swap = true;
                            int randomPerson = 0;
                            if (closePeople.length == 2){
                                randomPerson = (int) Math.floor(Math.random() * 10);
                                randomPerson = (randomPerson > 5) ? 1: 0;
                            }
                            personToSwap = closePeople[randomPerson];
                        }
                        else if (!personOneInGroup){
                            swap = true;
                            personToSwap = closePeople[0];
                        }
                        else if (!personTwoInGroup){
                            if (closePeople.length == 2) {
                                swap = true;
                                personToSwap = closePeople[1];
                            }
                        }
                        if (swap){
                            for (String group : groups) {
                                ArrayList<Person> maybeFarGroupMembers = environment.getGroup(group);
                                for (Person maybeFarGroupMember : maybeFarGroupMembers){
                                    if (!manager.equals(maybeFarGroupMember) && !maybeFarGroupMember.isFounder && maybeFarGroupMember.isSecretary){
                                        Assignment maybeFarAssignment = fact.getAssignment(maybeFarGroupMember);
                                        if ((personToSwap.isManager || personToSwap.isGroupHead || personToSwap.projectHead) && (maybeFarGroupMember.isManager || maybeFarGroupMember.isGroupHead || maybeFarGroupMember.projectHead)){
                                            swapByPeople(closeAssignment, maybeFarAssignment, personToSwap, maybeFarGroupMember);
                                            solved = true;
                                        }
                                        else if (!(personToSwap.isManager || personToSwap.isGroupHead || personToSwap.projectHead) && !(maybeFarGroupMember.isManager || maybeFarGroupMember.isGroupHead || maybeFarGroupMember.projectHead)){
                                            swapByPeople(closeAssignment, maybeFarAssignment, personToSwap, maybeFarGroupMember);
                                            solved = true;
                                        }
                                        else {
                                            swapRoom(closeAssignment, maybeFarAssignment);
                                            solved = true;
                                        }
                                    }
                                    if (solved)
                                        break;
                                }
                                if (solved)
                                    break;
                            }
                        }
                    }
                    if (solved)
                        break;
                }
            }
            //}
            assignments.add(temp);
        }
        return new Fact(assignments, spareRooms);
    }

    static public Fact Rule6(Fact fact){
        boolean solved = false;
        Environment environment = Environment.get();
        ArrayList<Room> spareRooms = fact.getSpareRooms();
        ArrayList<Assignment> assignments = new ArrayList<>();
        PriorityQueue<Assignment> priorityQueue = fact.getAssignmentPriorityQueue();
        while (!priorityQueue.isEmpty()) {
            Assignment temp = priorityQueue.remove();
            //if (!temp.getRoom().founderRoom) {
            Person manager = temp.getPerson()[0];
            Room managerRoom = temp.getRoom();
            if (temp.getPerson()[0].isManager) {
                // every group headed by this group head
                ArrayList<String> groups = manager.getGroupsPartOf();

                // for every room close to this group head
                for (String room : managerRoom.getCloseTo()){
                    //the close room
                    Room closeRoom = environment.getRoom(room);
                    //if the room has no assignment
                    if (fact.isSpare(closeRoom)){
                        // for every group headed by this group head
                        for (String thisGroup : groups) {

                            // group members
                            ArrayList<Person> maybeFarGroupMembers = environment.getGroup(thisGroup);
                            // for every group member in this group
                            for (Person maybeFarGroupMember : maybeFarGroupMembers) {
                                if (!manager.equals(maybeFarGroupMember) && !maybeFarGroupMember.isFounder) {
                                    if (maybeFarGroupMember.isSecretary){
                                        Assignment maybeFarAssignment = fact.getAssignment(maybeFarGroupMember);

                                        Person[] maybeFarPeople = maybeFarAssignment.getPerson();
                                        if (maybeFarPeople.length == 2){
                                            maybeFarAssignment.removePerson(maybeFarGroupMember);
                                            assignments.add(new Assignment(closeRoom, maybeFarGroupMember));
                                        }
                                        else {
                                            assignments.add(new Assignment(closeRoom, maybeFarGroupMember));
                                            spareRooms.add(maybeFarAssignment.getRoom());
                                            assignments.remove(maybeFarAssignment);
                                            priorityQueue.remove(maybeFarAssignment);
                                        }
                                        solved = true;
                                    }
                                }
                                if (solved)
                                    break;
                            }
                            if (solved)
                                break;
                        }
                    }
                    else if (!closeRoom.founderRoom){
                        Assignment closeAssignment = fact.getAssignment(closeRoom);
                        Person[] closePeople = closeAssignment.getPerson();
                        boolean personOneInGroup = false;
                        boolean personTwoInGroup = false;

                        // for each group headed by the group head
                        for (String grp : groups){
                            if (closePeople[0].isInGroup(grp)){
                                personOneInGroup = true;
                            }
                            if (closePeople.length == 2){
                                if (closePeople[1].isInGroup(grp)){
                                    personTwoInGroup = true;
                                }
                            }
                            if (personOneInGroup && personTwoInGroup)
                                break;
                        }
                        Person personToSwap = null;
                        boolean swap = false;
                        if (!personOneInGroup && !personTwoInGroup){
                            swap = true;
                            int randomPerson = 0;
                            if (closePeople.length == 2){
                                randomPerson = (int) Math.floor(Math.random() * 10);
                                randomPerson = (randomPerson > 5) ? 1: 0;
                            }
                            personToSwap = closePeople[randomPerson];
                        }
                        else if (!personOneInGroup){
                            swap = true;
                            personToSwap = closePeople[0];
                        }
                        else if (!personTwoInGroup){
                            if (closePeople.length == 2) {
                                swap = true;
                                personToSwap = closePeople[1];
                            }
                        }
                        if (swap){
                            for (String group : groups) {
                                ArrayList<Person> maybeFarGroupMembers = environment.getGroup(group);
                                for (Person maybeFarGroupMember : maybeFarGroupMembers){
                                    if (!manager.equals(maybeFarGroupMember) && !maybeFarGroupMember.isFounder && maybeFarGroupMember.isSecretary){
                                        Assignment maybeFarAssignment = fact.getAssignment(maybeFarGroupMember);
                                        if ((personToSwap.isManager || personToSwap.isGroupHead || personToSwap.projectHead) && (maybeFarGroupMember.isManager || maybeFarGroupMember.isGroupHead || maybeFarGroupMember.projectHead)){
                                            swapByPeople(closeAssignment, maybeFarAssignment, personToSwap, maybeFarGroupMember);
                                            solved = true;
                                        }
                                        else if (!(personToSwap.isManager || personToSwap.isGroupHead || personToSwap.projectHead) && !(maybeFarGroupMember.isManager || maybeFarGroupMember.isGroupHead || maybeFarGroupMember.projectHead)){
                                            swapByPeople(closeAssignment, maybeFarAssignment, personToSwap, maybeFarGroupMember);
                                            solved = true;
                                        }
                                        else {
                                            swapRoom(closeAssignment, maybeFarAssignment);
                                            solved = true;
                                        }
                                    }
                                    if (solved)
                                        break;
                                }
                                if (solved)
                                    break;
                            }
                        }
                    }
                    if (solved)
                        break;
                }
            }
            //}
            assignments.add(temp);
        }
        return new Fact(assignments, spareRooms);
    }

    /**
     * Swaps the person to be closer to their manager or group
     * @param fact
     * @return
     */
    static public Fact Rule7(Fact fact){
        boolean solved = false;
        Environment environment = Environment.get();
        ArrayList<Room> spareRooms = fact.getSpareRooms();
        ArrayList<Assignment> assignments = new ArrayList<>();
        PriorityQueue<Assignment> priorityQueue = fact.getAssignmentPriorityQueue();
        while (!priorityQueue.isEmpty()) {
            Assignment temp = priorityQueue.remove();
            //if (!temp.getRoom().founderRoom) {
            Person manager = temp.getPerson()[0];
            Room managerRoom = temp.getRoom();
            if (temp.getPerson()[0].isManager) {
                ArrayList<String> groups = manager.getGroupsPartOf();

                // for every room close to this manager
                for (String room : managerRoom.getCloseTo()){
                    //the close room
                    Room closeRoom = environment.getRoom(room);
                    //if the room has no assignment
                    if (fact.isSpare(closeRoom)){

                        for (String thisGroup : groups) {

                            // group members
                            ArrayList<Person> maybeFarGroupMembers = environment.getGroup(thisGroup);
                            // for every group member in this group
                            for (Person maybeFarGroupMember : maybeFarGroupMembers) {
                                if (!manager.equals(maybeFarGroupMember) && !maybeFarGroupMember.isFounder) {
                                    Assignment maybeFarAssignment = fact.getAssignment(maybeFarGroupMember);

                                    Person[] maybeFarPeople = maybeFarAssignment.getPerson();
                                    if (maybeFarPeople.length == 2){
                                        maybeFarAssignment.removePerson(maybeFarGroupMember);
                                        assignments.add(new Assignment(closeRoom, maybeFarGroupMember));
                                    }
                                    else {
                                        assignments.add(new Assignment(closeRoom, maybeFarGroupMember));
                                        spareRooms.add(maybeFarAssignment.getRoom());
                                        assignments.remove(maybeFarAssignment);
                                        priorityQueue.remove(maybeFarAssignment);
                                    }
                                    solved = true;
                                }
                                if (solved)
                                    break;
                            }
                            if (solved)
                                break;
                        }
                    }
                    else if (!closeRoom.founderRoom){
                        Assignment closeAssignment = fact.getAssignment(closeRoom);
                        Person[] closePeople = closeAssignment.getPerson();
                        boolean personOneInGroup = false;
                        boolean personTwoInGroup = false;

                        for (String grp : groups){
                            if (closePeople[0].isInGroup(grp)){
                                personOneInGroup = true;
                            }
                            if (closePeople.length == 2){
                                if (closePeople[1].isInGroup(grp)){
                                    personTwoInGroup = true;
                                }
                            }
                            if (personOneInGroup && personTwoInGroup)
                                break;
                        }
                        Person personToSwap = null;
                        boolean swap = false;
                        if (!personOneInGroup && !personTwoInGroup){
                            swap = true;
                            int randomPerson = 0;
                            if (closePeople.length == 2){
                                randomPerson = (int) Math.floor(Math.random() * 10);
                                randomPerson = (randomPerson > 5) ? 1: 0;
                            }
                            personToSwap = closePeople[randomPerson];
                        }
                        else if (!personOneInGroup){
                            swap = true;
                            personToSwap = closePeople[0];
                        }
                        else if (!personTwoInGroup){
                            if (closePeople.length == 2) {
                                swap = true;
                                personToSwap = closePeople[1];
                            }
                        }
                        if (swap){
                            for (String group : groups) {
                                ArrayList<Person> maybeFarGroupMembers = environment.getGroup(group);
                                for (Person maybeFarGroupMember : maybeFarGroupMembers){
                                    if (!manager.equals(maybeFarGroupMember) && !maybeFarGroupMember.isFounder){
                                        Assignment maybeFarAssignment = fact.getAssignment(maybeFarGroupMember);
                                        Room maybeFarRoom = maybeFarAssignment.getRoom();
                                        if ((personToSwap.isManager || personToSwap.isGroupHead || personToSwap.projectHead) && (maybeFarGroupMember.isManager || maybeFarGroupMember.isGroupHead || maybeFarGroupMember.projectHead)){
                                            swapByPeople(closeAssignment, maybeFarAssignment, personToSwap, maybeFarGroupMember);
                                            solved = true;
                                        }
                                        else if (!(personToSwap.isManager || personToSwap.isGroupHead || personToSwap.projectHead) && !(maybeFarGroupMember.isManager || maybeFarGroupMember.isGroupHead || maybeFarGroupMember.projectHead)){
                                            swapByPeople(closeAssignment, maybeFarAssignment, personToSwap, maybeFarGroupMember);
                                            solved = true;
                                        }
                                        else {
                                            swapRoom(closeAssignment, maybeFarAssignment);
                                            solved = true;
                                        }
                                    }
                                    if (solved)
                                        break;
                                }
                                if (solved)
                                    break;
                            }
                        }
                    }
                    if (solved)
                        break;
                }
            }
            //}
            assignments.add(temp);
        }
        return new Fact(assignments, spareRooms);
    }

    static public Fact Rule8(Fact fact){
        boolean solved = false;
        Environment environment = Environment.get();
        ArrayList<Room> spareRooms = fact.getSpareRooms();
        ArrayList<Assignment> assignments = new ArrayList<>();
        PriorityQueue<Assignment> priorityQueue = fact.getAssignmentPriorityQueue();
        while (!priorityQueue.isEmpty()) {
            Assignment temp = priorityQueue.remove();
            //if (!temp.getRoom().founderRoom) {
            Person projectHead = temp.getPerson()[0];
            Room projectHeadRoom = temp.getRoom();
            if (temp.getPerson()[0].projectHead) {
                ArrayList<String> projects = projectHead.getProjectsHeaded();

                // for every room close to this project head
                for (String room : projectHeadRoom.getCloseTo()){
                    //the close room
                    Room closeRoom = environment.getRoom(room);
                    //if the room has no assignment
                    if (fact.isSpare(closeRoom)){

                        for (String thisProject : projects) {

                            // project members
                            ArrayList<Person> maybeFarProjectMembers = environment.getProject(thisProject);
                            // for every project member in this project
                            for (Person maybeFarProjectMember : maybeFarProjectMembers) {
                                if (!projectHead.equals(maybeFarProjectMember) && !maybeFarProjectMember.isFounder) {
                                    Assignment maybeFarAssignment = fact.getAssignment(maybeFarProjectMember);
                                    if (!maybeFarProjectMember.isManager && !maybeFarProjectMember.isGroupHead && !maybeFarProjectMember.projectHead){
                                        Person[] maybeFarPeople = maybeFarAssignment.getPerson();
                                        if (maybeFarPeople.length == 2){
                                            maybeFarAssignment.removePerson(maybeFarProjectMember);
                                            assignments.add(new Assignment(closeRoom, maybeFarProjectMember));
                                        }
                                        else {
                                            assignments.add(new Assignment(closeRoom, maybeFarProjectMember));
                                            spareRooms.add(maybeFarAssignment.getRoom());
                                            assignments.remove(maybeFarAssignment);
                                            priorityQueue.remove(maybeFarAssignment);
                                        }
                                        solved = true;
                                    }
                                }
                                if (solved)
                                    break;
                            }
                            if (solved)
                                break;
                        }
                    }
                    else if (!closeRoom.founderRoom){
                        Assignment closeAssignment = fact.getAssignment(closeRoom);
                        Person[] closePeople = closeAssignment.getPerson();
                        boolean personOneInProject = false;
                        boolean personTwoInProject = false;

                        for (String proj : projects){
                            if (closePeople[0].isInProject(proj)){
                                personOneInProject = true;
                            }
                            if (closePeople.length == 2){
                                if (closePeople[1].isInProject(proj)){
                                    personTwoInProject = true;
                                }
                            }
                            if (personOneInProject && personTwoInProject)
                                break;
                        }
                        Person personToSwap = null;
                        boolean swap = false;
                        if (!personOneInProject && !personTwoInProject){
                            swap = true;
                            int randomPerson = 0;
                            if (closePeople.length == 2){
                                randomPerson = (int) Math.floor(Math.random() * 10);
                                randomPerson = (randomPerson > 5) ? 1: 0;
                            }
                            personToSwap = closePeople[randomPerson];
                        }
                        else if (!personOneInProject){
                            swap = true;
                            personToSwap = closePeople[0];
                        }
                        else if (!personTwoInProject){
                            if (closePeople.length == 2) {
                                swap = true;
                                personToSwap = closePeople[1];
                            }
                        }
                        if (swap){
                            for (String project : projects) {
                                ArrayList<Person> maybeFarProjectMembers = environment.getProject(project);
                                for (Person maybeFarProjectMember : maybeFarProjectMembers){
                                    if (!projectHead.equals(maybeFarProjectMember) && !maybeFarProjectMember.isFounder){
                                        Assignment maybeFarAssignment = fact.getAssignment(maybeFarProjectMember);
                                        if ((personToSwap.isManager || personToSwap.isGroupHead || personToSwap.projectHead) && (maybeFarProjectMember.isManager || maybeFarProjectMember.isGroupHead || maybeFarProjectMember.projectHead)){
                                            swapByPeople(closeAssignment, maybeFarAssignment, personToSwap, maybeFarProjectMember);
                                            solved = true;
                                        }
                                        else if (!(personToSwap.isManager || personToSwap.isGroupHead || personToSwap.projectHead) && !(maybeFarProjectMember.isManager || maybeFarProjectMember.isGroupHead || maybeFarProjectMember.projectHead)){
                                            swapByPeople(closeAssignment, maybeFarAssignment, personToSwap, maybeFarProjectMember);
                                            solved = true;
                                        }
                                        else {
                                            swapRoom(closeAssignment, maybeFarAssignment);
                                            solved = true;
                                        }
                                    }
                                    if (solved)
                                        break;
                                }
                                if (solved)
                                    break;
                            }
                        }
                    }
                    if (solved)
                        break;
                }
            }
            //}
            assignments.add(temp);
        }
        return new Fact(assignments, spareRooms);
    }

    static public Fact Rule9(Fact fact){
        boolean solved = false;
        Environment environment = Environment.get();
        ArrayList<Room> spareRooms = fact.getSpareRooms();
        ArrayList<Assignment> assignments = new ArrayList<>();
        PriorityQueue<Assignment> priorityQueue = fact.getAssignmentPriorityQueue();
        while (!priorityQueue.isEmpty()) {
            Assignment temp = priorityQueue.remove();
            //if (!temp.getRoom().founderRoom) {
            Person projectHead = temp.getPerson()[0];
            Room projectHeadRoom = temp.getRoom();
            if (temp.getPerson()[0].projectHead) {
                // every group headed by this group head
                ArrayList<String> projects = projectHead.getProjectsHeaded();

                // for every room close to this group head
                for (String room : projectHeadRoom.getCloseTo()){
                    //the close room
                    Room closeRoom = environment.getRoom(room);
                    //if the room has no assignment
                    if (fact.isSpare(closeRoom)){
                        // for every group headed by this group head
                        for (String thisProject : projects) {
                            if (environment.e_large_project(thisProject)) {
                                // group members
                                ArrayList<Person> maybeFarProjectMembers = environment.getProject(thisProject);
                                // for every group member in this group
                                for (Person maybeFarProjectMember : maybeFarProjectMembers) {
                                    if (!projectHead.equals(maybeFarProjectMember) && !maybeFarProjectMember.isFounder) {
                                        if (maybeFarProjectMember.isSecretary) {
                                            Assignment maybeFarAssignment = fact.getAssignment(maybeFarProjectMember);

                                            Person[] maybeFarPeople = maybeFarAssignment.getPerson();
                                            if (maybeFarPeople.length == 2) {
                                                maybeFarAssignment.removePerson(maybeFarProjectMember);
                                                assignments.add(new Assignment(closeRoom, maybeFarProjectMember));
                                            } else {
                                                assignments.add(new Assignment(closeRoom, maybeFarProjectMember));
                                                spareRooms.add(maybeFarAssignment.getRoom());
                                                assignments.remove(maybeFarAssignment);
                                                priorityQueue.remove(maybeFarAssignment);
                                            }
                                            solved = true;
                                        }
                                    }
                                    if (solved)
                                        break;
                                }
                                if (solved)
                                    break;
                            }
                        }
                    }
                    else if (!closeRoom.founderRoom){
                        Assignment closeAssignment = fact.getAssignment(closeRoom);
                        Person[] closePeople = closeAssignment.getPerson();
                        boolean personOneInProject = false;
                        boolean personTwoInProject = false;

                        // for each group headed by the group head
                        for (String proj : projects){
                            if (closePeople[0].isInProject(proj)){
                                personOneInProject = true;
                            }
                            if (closePeople.length == 2){
                                if (closePeople[1].isInProject(proj)){
                                    personTwoInProject = true;
                                }
                            }
                            if (personOneInProject && personTwoInProject)
                                break;
                        }
                        Person personToSwap = null;
                        boolean swap = false;
                        if (!personOneInProject && !personTwoInProject){
                            swap = true;
                            int randomPerson = 0;
                            if (closePeople.length == 2){
                                randomPerson = (int) Math.floor(Math.random() * 10);
                                randomPerson = (randomPerson > 5) ? 1: 0;
                            }
                            personToSwap = closePeople[randomPerson];
                        }
                        else if (!personOneInProject){
                            swap = true;
                            personToSwap = closePeople[0];
                        }
                        else if (!personTwoInProject){
                            if (closePeople.length == 2) {
                                swap = true;
                                personToSwap = closePeople[1];
                            }
                        }
                        if (swap){
                            for (String project : projects) {
                                if (environment.e_large_project(project)){
                                    ArrayList<Person> maybeFarGroupMembers = environment.getProject(project);
                                    for (Person maybeFarGroupMember : maybeFarGroupMembers){
                                        if (!projectHead.equals(maybeFarGroupMember) && !maybeFarGroupMember.isFounder && maybeFarGroupMember.isSecretary){
                                            Assignment maybeFarAssignment = fact.getAssignment(maybeFarGroupMember);
                                            if ((personToSwap.isManager || personToSwap.isGroupHead || personToSwap.projectHead) && (maybeFarGroupMember.isManager || maybeFarGroupMember.isGroupHead || maybeFarGroupMember.projectHead)){
                                                swapByPeople(closeAssignment, maybeFarAssignment, personToSwap, maybeFarGroupMember);
                                                solved = true;
                                            }
                                            else if (!(personToSwap.isManager || personToSwap.isGroupHead || personToSwap.projectHead) && !(maybeFarGroupMember.isManager || maybeFarGroupMember.isGroupHead || maybeFarGroupMember.projectHead)){
                                                swapByPeople(closeAssignment, maybeFarAssignment, personToSwap, maybeFarGroupMember);
                                                solved = true;
                                            }
                                            else {
                                                swapRoom(closeAssignment, maybeFarAssignment);
                                                solved = true;
                                            }
                                        }
                                        if (solved)
                                            break;
                                    }
                                    if (solved)
                                        break;
                                }
                            }
                        }
                    }
                    if (solved)
                        break;
                }
            }
            //}
            assignments.add(temp);
        }
        return new Fact(assignments, spareRooms);
    }

    static public Fact Rule10(Fact fact){
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
                Person[] smoker = temp.getPerson();
                if (smoker.length == 1){
                    if (smoker[0].isSmoker) {
                        mismatchSmoker.add(temp);
                        mismatchSmokerLocation.add(0);
                    }
                }
                else {
                    if (smoker[0].isSmoker) {
                        if (!smoker[1].isSmoker){
                            mismatchSmoker.add(temp);
                            mismatchSmokerLocation.add(0);
                        }
                    }
                    else if (smoker[1].isSmoker){
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
                if (!(a1.getPerson()[0].isManager || a1.getPerson()[0].isGroupHead || a1.getPerson()[0].projectHead) && !a1.getRoom().founderRoom && !(a2.getPerson()[0].isManager || a2.getPerson()[0].isGroupHead || a2.getPerson()[0].projectHead) && !a2.getRoom().founderRoom) {
                    if (a1.getPerson().length == 1 && a2.getPerson().length == 1) {
                        a1.addSecondPerson(a2.getPerson()[0]);
                        spareRooms.add(a2.getRoom());
                        priorityQueue.remove(a2);
                        assignments.remove(a2);
                    } else if (a1.getPerson().length == 1) {
                        a1.addSecondPerson(a2.getPerson()[mismatchSmokerLocation.get(i + 1)]);
                        a2.removePerson(a2.getPerson()[mismatchSmokerLocation.get(i + 1)]);
                    } else if (a2.getPerson().length == 1) {
                        a2.addSecondPerson(a1.getPerson()[mismatchSmokerLocation.get(i)]);
                        a1.removePerson(a1.getPerson()[mismatchSmokerLocation.get(i)]);
                    } else if (mismatchSmokerLocation.get(i).equals(1)) {
                        swapPeople(mismatchSmoker.get(i), mismatchSmoker.get(i + 1), mismatchSmokerLocation.get(i) - 1, mismatchSmokerLocation.get(i + 1));
                    } else if (mismatchSmokerLocation.get(i + 1).equals(1)) {
                        swapPeople(mismatchSmoker.get(i), mismatchSmoker.get(i + 1), mismatchSmokerLocation.get(i), mismatchSmokerLocation.get(i + 1) - 1);
                    }
                }
            }
            else {
                matchedSmokers = true;
            }
            i += 2;
        }
        return new Fact(assignments, spareRooms);
    }

    static public Fact Rule12(Fact fact) {
        return null;
    }

    /**
     * hacker is with hackers
     * @param fact
     * @return
     */
    static public Fact Rule13(Fact fact) {
        ArrayList<Assignment> assignments = new ArrayList<>();
        PriorityQueue<Assignment> priorityQueue = fact.getAssignmentPriorityQueue();
        ArrayList<Room> spareRooms = fact.getSpareRooms();
        ArrayList<Assignment> mismatchHacker = new ArrayList<Assignment>();
        ArrayList<Integer> mismatchHackerLocation = new ArrayList<Integer>();
        while (!priorityQueue.isEmpty()) {
            Assignment temp = priorityQueue.remove();
            //if (fact.getSoftConstraint() == ConstraintID.SOFTCONSTRAINT11) {
            //System.out.println(temp);
            if (!temp.getRoom().founderRoom){
                Person[] hacker = temp.getPerson();
                if (hacker.length == 1){
                    if (hacker[0].isHacker) {
                        mismatchHacker.add(temp);
                        mismatchHackerLocation.add(0);
                    }
                }
                else {
                    if (hacker[0].isHacker) {
                        if (!hacker[1].isHacker){
                            mismatchHacker.add(temp);
                            mismatchHackerLocation.add(0);
                        }
                    }
                    else if (hacker[1].isHacker){
                        mismatchHacker.add(temp);
                        mismatchHackerLocation.add(1);
                    }
                }
            }
            //}
            assignments.add(temp);
        }
        boolean matchedHacker = false;
        int i = 0;
        int x = (int) Math.floor(Math.random() * mismatchHacker.size());
        while(!matchedHacker && (i < x)){
            if (i + 1 < mismatchHacker.size()){
                Assignment a1 = mismatchHacker.get(i);
                Assignment a2 = mismatchHacker.get(i+1);
                if (a1.getPerson().length == 1 && a2.getPerson().length == 1){
                    a1.addSecondPerson(a2.getPerson()[0]);
                    spareRooms.add(a2.getRoom());
                    assignments.remove(a2);
                }
                else if (a1.getPerson().length == 1){
                    a1.addSecondPerson(a2.getPerson()[mismatchHackerLocation.get(i+1)]);
                    a2.removePerson(a2.getPerson()[mismatchHackerLocation.get(i+1)]);
                }
                else if (a2.getPerson().length == 1){
                    a2.addSecondPerson(a1.getPerson()[mismatchHackerLocation.get(i)]);
                    a1.removePerson(a1.getPerson()[mismatchHackerLocation.get(i)]);
                }
                else if (mismatchHackerLocation.get(i).equals(1)){
                    swapPeople(mismatchHacker.get(i), mismatchHacker.get(i+1), mismatchHackerLocation.get(i) - 1, mismatchHackerLocation.get(i+1));
                }
                else if (mismatchHackerLocation.get(i+1).equals(1)){
                    swapPeople(mismatchHacker.get(i), mismatchHacker.get(i+1), mismatchHackerLocation.get(i), mismatchHackerLocation.get(i+1)-1);
                }
            }
            else {
                matchedHacker = true;
            }
            i += 2;
        }
        return new Fact(assignments, spareRooms);
    }

    static public Fact Rule14(Fact fact) {
        ArrayList<Assignment> assignments = new ArrayList<>();
        PriorityQueue<Assignment> priorityQueue = fact.getAssignmentPriorityQueue();
        Assignment temp = priorityQueue.remove();
        ArrayList<Room> spareRooms = fact.getSpareRooms();
        Person[] person = temp.getPerson();

        if (person.length == 2){
            if (spareRooms.size() > 1){
                Room room = fact.getHousing().get(person[0]);
                //Room rm = assignments.remove(temp);
                assignments.add(fact.getAssignment(person[0]));

                //take one person put into a spare room
            }
            assignments.add(temp);
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
   //     System.out.print("Rule 16: ");
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

    private static void swapByPeople(Assignment a1, Assignment a2, Person person, Person person2){
        Person toSwap = person;
        a1.setPerson(person, person2);
        a2.setPerson(person2, toSwap);
    }
}
