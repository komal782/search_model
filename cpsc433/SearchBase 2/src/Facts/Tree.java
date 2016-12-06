package Facts;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Function;

import Facts.*;
/**
 * Created by shado on 11/24/2016.
 */
public class Tree extends Violations {

    public Tree(){

    }
    /**
     * for noe the or Tree is build randomly, while avoiding violating hard contraints.
     * Note we are assigning persons rooms (rooms to persons)
     *
     * will try to satisfy manager having large room soft contraint
     */

    public static ArrayDeque<Room> buildTree(ArrayDeque<Room> rooms, ArrayDeque<Person> persons, Fact fact){
        //Take a person
        //Take a room
        while (!persons.isEmpty()){
            Room room;
            int randomNumber = (int) Math.floor(Math.random() * 3);
            if (rooms.isEmpty())
                return null;
            room = rooms.pop();
            for (int i = 0; i < randomNumber; i++){
                rooms.add(room);
                room = rooms.pop();
            }
            Person person = persons.remove();
            //System.out.println((true) ? " \tPerson: " + person.getName() : "");
            /*if (person.isFounder){
                rooms.add(room);
                Room founderRoom = fact.getHousing().get(person);
                System.out.println(person.getName() + "  " + founderRoom.getName());
                rooms.remove(founderRoom);
            }*/
            if (room.founderRoom || person.isFounder){
                if (room.founderRoom){
                    if (!person.isFounder)
                        persons.add(person);
                    else{
                        Assignment a1 = fact.getAssignment(person);
                        Person[] people = a1.getPerson();
                        persons.remove(people[0]);
                        if (people.length == 2)
                            persons.remove(people[1]);
                        rooms.remove(a1.getRoom());
                    }
                    Person[] founders = fact.getOccupants().get(room);
                    persons.remove(founders[0]);
                    if (founders.length == 2)
                        persons.remove(founders[1]);
                }
                if (person.isFounder){
                    if (!room.founderRoom)
                        rooms.add(room);
                    else{
                        Assignment a1 = fact.getAssignment(room);
                        Person[] people = a1.getPerson();
                        persons.remove(people[0]);
                        if (people.length == 2)
                            persons.remove(people[1]);
                        rooms.remove(room);
                    }
                    Room founderRoom = fact.getHousing().get(person);
                    Person[] founders = fact.getOccupants().get(founderRoom);
                    if (founders.length == 2){
                        if (person.equals(founders[0]))
                            persons.remove(founders[1]);
                        else
                            persons.remove(founders[0]);
                    }
                    rooms.remove(founderRoom);
                }
            }
            else if(persons.isEmpty() || person.isGroupHead || person.isManager || person.projectHead){
                person.setRoomName(room.getName());
                Assignment temp = new Assignment(room,person);
                fact.setAssignment(temp);
                //fact.addToPriorityQueue(temp);
            }
            else {
                person.setRoomName(room.getName());
                Person person2 = persons.element();
                //System.out.println(" Person: " + person2.getName());
                if (!person2.isFounder) {
                    if (!person2.isGroupHead || !person2.isManager || !person2.projectHead) {
                        persons.remove();
                        person2.setRoomName(room.getName());
                        Assignment temp = new Assignment(room, person);
                        temp.setPerson2(person2);
                        fact.setAssignment(temp);
                        //fact.addToPriorityQueue(temp);
                    }
                } else {
                    persons.add(person);
                    rooms.add(room);
                    persons.remove();
                    Room founderRoom = fact.getHousing().get(person.getName());
                    rooms.remove(founderRoom);
                }
            }




           // buildTree(rooms, persons, fact);


        }
        return rooms;
    }



}
