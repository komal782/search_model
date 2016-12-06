package Facts;

import SetBased.Tuple;

import java.util.*;
import java.util.Comparator;
/**
 * Created by shado on 11/30/2016.
 */
public class Fact extends Violations implements ConstraintID {

    private static Comparator<Assignment> comparator = new Comparator<Assignment>() {
        @Override
        public int compare(Assignment o1, Assignment o2)
        {
            if(o1.getScore() > o2.getScore()){
                return 1;
            }
            if(o1.getScore() < o2.getScore()){
                return -1;
            }
            return 0;
        }
    };
    private PriorityQueue<Assignment> assignmentPriorityQueue;
    private ArrayDeque<Room> spareRooms;
    private ArrayList<Assignment> unordered_assignments;
    private Tuple SoftContraintsViolated;
    private Map<Room, Person[]> occupants;
    private Map<Room, Assignment> roomAssignmentMap;
    private Map<Person, Assignment> personAssignmentMap;
    private Map<Person, Room> housing;
    private int softConstraintViolated = -1;
    private int softConstraintValue = 0;
    int numberOfPeople = 0;
    boolean resetNumberOfPeople = true;
    //fact.getPerson returns room
    private int score;
    public Fact(ArrayList<Assignment> assignments, ArrayList<Room> spareRooms){
        assignmentPriorityQueue = new PriorityQueue<>(comparator);
        unordered_assignments = new ArrayList<>(assignments);
        this.spareRooms = new ArrayDeque<>(spareRooms);
        initOccupantsHousing();
    }

    public void setAll(){
        for (Assignment assignment : unordered_assignments){

            if (!assignmentPriorityQueue.contains(assignment))
                assignmentPriorityQueue.add(assignment);
            if (occupants.get(assignment.getRoom()) == null){
                Room temp = assignment.getRoom();

                this.occupants.put(temp, assignment.getPerson());
                this.roomAssignmentMap.put(temp, assignment);

                this.personAssignmentMap.put(assignment.getPerson()[0], assignment);
                this.housing.put(assignment.getPerson()[0], temp);
                if(assignment.getPerson().length == 2){
                    this.personAssignmentMap.put(assignment.getPerson()[1], assignment);
                    this.housing.put(assignment.getPerson()[1], temp);
                }
            }
        }

    }
    public void setSpareRooms(ArrayDeque<Room> rooms){
        spareRooms = rooms;
    }

    private void initOccupantsHousing(){
        this.occupants = new HashMap<>();
        this.housing = new HashMap<>();
        this.roomAssignmentMap = new HashMap<>();
        this.personAssignmentMap = new HashMap<>();

        for (Assignment assignment: unordered_assignments){
            Room temp = assignment.getRoom();

            this.occupants.put(temp, assignment.getPerson());
            this.roomAssignmentMap.put(temp, assignment);

            this.personAssignmentMap.put(assignment.getPerson()[0], assignment);
            this.housing.put(assignment.getPerson()[0], temp);
            if(assignment.getPerson().length == 2){
                this.personAssignmentMap.put(assignment.getPerson()[1], assignment);
                this.housing.put(assignment.getPerson()[1], temp);
            }
        }
    }
    public Assignment getAssignment(Room room){
        return roomAssignmentMap.get(room);
    }

    public Assignment getAssignment(Person person){
        return personAssignmentMap.get(person);
    }

    public ArrayList<Room> getSpareRooms(){
        return new ArrayList<>(spareRooms);
    }

    public boolean isSpare(Room room){
        return spareRooms.contains(room);
    }

    public Map<Person, Room> getHousing(){
        return new HashMap<>(housing);
    }

    public Map<Room, Person[]> getOccupants(){
        return new HashMap<>(occupants);
    }

    public void CalculateViolations(){
       SoftContraintsViolated = AllSoftConstraints(this);
    }

    public void clearPriorityQueue(){
        assignmentPriorityQueue.clear();
    }

    public void addToPriorityQueue(Assignment assignment) {
        assignmentPriorityQueue.add(assignment);
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setAssignment(Assignment assignment){
        unordered_assignments.add(assignment);

    }

    public ArrayList<Assignment> getUnordered_assignments(){
        return new ArrayList<>(unordered_assignments);

    }

    public ArrayList<Assignment> getAssignments(){
       return new ArrayList<>(assignmentPriorityQueue);

    }

    public PriorityQueue<Assignment> getAssignmentPriorityQueue(){
        return new PriorityQueue<>(assignmentPriorityQueue);
    }

    public int getSoftConstraint(){
        return this.softConstraintViolated;

    }

    public void setSoftConstraint(int softConstaintViolated){
        this.softConstraintViolated = softConstaintViolated;

    }

    public int getSoftConstraintValue(){
        return this.softConstraintViolated;

    }

    public void setSoftConstraintValue(int softConstaintValue){
        this.softConstraintValue = softConstraintValue;
    }

    public String toString(){
        StringBuilder temp = new StringBuilder();
        for (Assignment assignment : unordered_assignments){
            Person[] people = assignment.getPerson();
            String room = assignment.getRoom().getName();
            if (people.length == 2){
                temp.append("assign-to(" + people[0].getName() + ", "  + room + ")\n");
                temp.append("assign-to(" + people[1].getName() + ", "  + room + ")\n");
            }
            else {
                temp.append("assign-to(" + assignment.toString() + ")\n");
            }
        }
        return temp.toString();
    }

    public boolean getHard(){
        boolean ret = false;
        for (Assignment ass : unordered_assignments){
            Person[] p = ass.getPerson();
            if(p.length > 2){
                System.out.println("\t\t\t\t\t TOO MANY PEOPLE IN ONE ROOM");
                ret = true;
            }
            else if (p.length == 2) {
                if ((p[0].isManager || p[1].isManager)){
                    System.out.println("\t\t\t\t\t MANAGERS CAN'T SHARE A ROOM");
                    ret = true;
                }
                else if ((p[0].isGroupHead || p[1].isGroupHead)){
                    System.out.println("\t\t\t\t\t GROUPHEADS CAN'T SHARE A ROOM >" + p[0].getName() + " " + p[1].getName());
                    //System.out.println(p[0].getName() + " " + p[1].getName());
                    ret = true;
                }
                else if ((p[0].projectHead || p[1].projectHead)){
                    System.out.println("\t\t\t\t\t PROJECTHEADS CAN'T SHARE A ROOM" + p[0].getName() + " " + p[1].getName());
                    ret = true;
                }
            }
        }

        return ret;
    }
}
