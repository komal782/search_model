package Facts;

import java.util.ArrayList;

/**
 * Created by shado on 11/14/2016.
 */
public class Room {

    private ArrayList<String> closeTo;
    private int size;
    private String name;
    public boolean founderRoom;
    public Room(String r){
        name = r;
        closeTo = new ArrayList<String>();
    }

    public void addCloseRoom(String r){
        if(!closeTo.contains(r)){
            closeTo.add(r);
        }
    }

    public String getName(){
        return name;
    }

    public ArrayList<String> getCloseTo(){
        return new ArrayList<String>(closeTo);
    }
    public void setSize(int i){
        size = i;
    }

    public  int getSize(){
        return size;
    }

    public boolean hasClose(String r){
        return closeTo.contains(r);
    }

    public String toString(){
        String temp = "";
        if(size == 1)
            temp += "small-room(" + name + ")\n";
        else if(size == 2)
            temp += "medium-room(" + name + ")\n";
        else if(size == 3)
            temp += "large-room(" + name + ")\n";

        if(!closeTo.isEmpty()){
            for (String s : closeTo){
                temp += "close-to(" + name + ", " + s + ")\n";
            }
        }
        return temp;
    }
    public boolean equals(Room room){
        return name.equals(room.getName());
    }

}
