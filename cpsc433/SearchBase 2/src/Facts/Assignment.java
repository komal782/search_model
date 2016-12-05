package Facts;

/**
 * Created by Brandon on 11/24/2016.
 */
public class Assignment implements ConstraintID {

    private Room room;
    private Person person1;
    private Person person2;
    private int score;
    private int softConstraintViolated = -1;
    private boolean[] softContraintsViolated = new boolean[16];

    public Assignment(Room room, Person person){
        this.room = room;
        this.person1 = person;

    }

    public Room getRoom(){
        return room;
    }
    public void setRoom(Room room){
        this.room = room;
    }
    public Person[] getPerson(){
        if(person2 != null)
            return new Person[]{person1,person2};
        else
            return new Person[]{person1};
    }
    public void setPerson(Person previousOccupant, Person newOccupant){
        if (person1.equals(previousOccupant))
            person1 = newOccupant;
        else if (person2.equals(previousOccupant))
            person2 = newOccupant;
    }
    public void removePerson(Person person){
        if (person1.equals(person)){
            person1 = person2;
            person2 = null;
        }
        else if (person2.equals(person))
            person2 = null;
    }
    public void setPerson1(Person person){
        person1 = person;
    }
    public void setPerson2(Person person){
        person2 = person;
    }
    public int getScore(){
        return score;
    }


    public void setScore(int score){this.score = score;}
    public void addSecondPerson(Person person){
        this.person2 = person;
    }

    public int getSoftContraint(){
        return this.softConstraintViolated;
    }
    public void setSoftContraint(int softConstaintViolated){
        this.softConstraintViolated = softConstaintViolated;
    }
    public String toString(){
        String occupants = "";
        if (person1 != null && person2 != null)
            occupants = "[" + person1.getName() + ", " + person2.getName() + "], ";
        else if (person1 != null)
            occupants = person1.getName() + ", ";
        else if (person2 != null)
            occupants = person2.getName() + ", ";
        return occupants + room.getName();
    }
    public boolean equals(Assignment assignment){
        Person[] people = assignment.getPerson();
        if (!people[0].equals(person1))
            return false;
        if (people.length == 2)
            if (person2 == null)
                return false;
        if (!people[1].equals(person2))
            return false;
        if (!assignment.getRoom().equals(room))
            return false;
        return true;

    }

}
