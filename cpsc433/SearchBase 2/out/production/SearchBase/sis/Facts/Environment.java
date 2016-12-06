/**
 * 
 */
package Facts;

import cpsc433.*;
import cpsc433.Predicate.ParamType;

import java.util.*;
//import sun.java2d.windows.GDIRenderer;

/**
 * This is class extends {@link PredicateReader} just as required to
 * in the assignment. You can extend this class to include your predicate definitions
 * or you can create another class that extends {@link PredicateReader} and
 * use that one.
 * <p>
 * I have defined this class as a singleton.
 * 
 * <p>Copyright: Copyright (c) 2003-16, Department of Computer Science, University 
 * of Calgary.  Permission to use, copy, modify, distribute and sell this 
 * software and its documentation for any purpose is hereby granted without 
 * fee, provided that the above copyright notice appear in all copies and that
 * both that copyright notice and this permission notice appear in supporting 
 * documentation.  The Department of Computer Science makes no representations
 * about the suitability of this software for any purpose.  It is provided
 * "as is" without express or implied warranty.</p>
 *
 * @author <a href="http://www.cpsc.ucalgary.ca/~kremer/">Rob Kremer</a>
 *
 */
public class Environment extends PredicateReader implements SisyphusPredicates {

	private static Environment instance=null;
	protected boolean fixedAssignments=false;


	private ArrayList<Assignment> Assignments = new ArrayList<Assignment>();

	private Map<String, Person> Persons = new HashMap<String, Person>();
	private Map<String, Room> Rooms = new HashMap<String, Room>();

	private ArrayDeque<String> SmallRooms = new ArrayDeque<String>();
	private ArrayDeque<String> MediumRooms = new ArrayDeque<String>();
	private ArrayDeque<String> LargeRooms = new ArrayDeque<String>();


	private Map<String, ArrayList<Person>> Groups = new HashMap<>();


	private ArrayList<String> Projects = new ArrayList<String>();

	private Map<String, ArrayList<Person>> ProjectsTemp = new HashMap<>();

	private ArrayList<String> LargeProjects = new ArrayList<String>();


	public ArrayDeque<String> getSmallRooms(){
		return SmallRooms;
	}

	public ArrayDeque<String> getMediumRooms(){
		return MediumRooms;
	}

	public ArrayDeque<String> getLargeRooms(){
		return LargeRooms;
	}

	public void setAssignment(Assignment assignment){
		Assignments.add(assignment);
	}

	public ArrayList<Assignment> getAssignments(){
		return Assignments;
	}

	public Map<String, Person> getPersons(){
		return Persons;
	}
	public Person getPerson(String personName){
		return Persons.get(personName);

	}
	public Room getRoom(String roomID){
		return Rooms.get(roomID);

	}
	public ArrayList<Person> getGroup(String grp){
		return Groups.get(grp);
	}
	public Map<String, Room> getRooms(){
		return Rooms;
	}

	public Map<String, ArrayList<Person>> getGroups(){

		return new HashMap<>(Groups);
	}


	public Map<String, ArrayList<Person>> getProjects(){
		return new HashMap<>(ProjectsTemp);
	}

	public ArrayList<Person> getProject(String proj){
		return ProjectsTemp.get(proj);
	}

	public ArrayList<String> getLargeProjects(){
		return LargeProjects;
	}

	protected Environment(String name) {
		super(name==null?"theEnvironment":name);
	}
	
	/**
	 * A getter for the global instance of this class.  If an instance of this class
	 * does not already exist, it will be created.
	 * @return The singleton (global) instance.
	 */
	public String toString(){
		String temp = "";
		for (String p : Persons.keySet()){
			Person person = Persons.get(p);
			temp += person.toString() + "\n";
		}

		for (String prj : LargeProjects){
			temp += "large-project(" + prj +")\n";
		}

		temp += "\n";

		for (String prj : Projects){
			temp += "project(" + prj + ")\n";
		}

		temp += "\n";

		for (String r : Rooms.keySet()){
			Room room = Rooms.get(r);
			if(room.getSize() == 1){
				temp += "small-room(" + r + ")\n";
			}
		}

		for (String r : Rooms.keySet()) {
			Room room = Rooms.get(r);
			if (room.getSize() == 2) {
				temp += "medium-room(" + r + ")\n";
			}
		}

		for (String r : Rooms.keySet()){
			Room room = Rooms.get(r);
			if(room.getSize() == 3){
				temp += "large-room(" + r + ")\n";
			}
		}
		temp += "\n";

		for (String r : Rooms.keySet()){
			Room room = Rooms.get(r);
			for(String c : room.getCloseTo()){
				temp += "close(" + r + ", " + c + ")\n";
			}
		}

		temp += "\n";

		for (String g : Groups.keySet()){
			temp += "group(" + g + ")\n";
		}

		temp += "\n";
		//assignments.removeAll(null);
		for (Assignment assignment : Assignments){
			Person[] people = assignment.getPerson();
			String room = assignment.getRoom().getName();
			if (people.length == 2){
				temp += "assign-to(" + people[0].getName() + ", "  + room + ")\n";
				temp += "assign-to(" + people[1].getName() + ", "  + room + ")\n";
			}
			else {
				temp += "assign-to(" + assignment.toString() + ")\n";
			}
		}


		int i = 0;


		return temp;
	}

	public static Environment get() {
		if (instance==null) instance = new Environment(null);
		return instance;
	}

	public String AddQuotes(String s){
		if(s.contains(" "))
			return "\"" + s + "\"";
		return s;

	}
	// UTILITY PREDICATES
	
	/**
	 * The help text for the exit() predicate.
	 */
	public static String h_exit = "quit the program";
	/**
	 * The definition of the exit() assertion predicate.  It will exit the program abruptly.
	 */
	public void a_exit() {
		System.exit(0);
	}

	public static String h_person = "query or assert <id> is a person";
	public void a_person(String p){
		Person temp;

		temp = Persons.get(p);
		if (temp == null){
			temp = new Person(p);
			temp.isFounder = false;
		}
		Persons.put(p, new Person(p));
	} 
	public boolean e_person(String p){
		Person temp;

		temp = Persons.get(p);
		if (temp != null)
			return true;
		return false;
	}
	
	public static String h_secretary = "query or assert person <id> is a secretary";
	public void a_secretary(String p) {
		Person temp = Persons.get(p);

		if(temp != null){
			temp.isSecretary = true;
			Persons.replace(p, temp);
		}
		//not in ennvronment
		else{
			temp = new Person(p);
			temp.isSecretary = true;
			temp.isFounder = false;
			Persons.put(p, temp);
		}
	}
	public boolean e_secretary(String p) {
		Person temp = Persons.get(p);

		if(temp != null){
			return temp.isSecretary;
		}
		return  false;
	}
	
	public static String h_researcher = "query or assert person <id> is a researcher";
	public void a_researcher(String p){

		Person temp = Persons.get(p);
		if(temp != null){
			temp.isResearcher = true;
			Persons.replace(p, temp);
		}
		else{
			temp = new Person(p);
			temp.isResearcher = true;
			temp.isFounder = false;
			Persons.put(p, temp);
		}
	}
	public boolean e_researcher(String p) {

		Person temp = Persons.get(p);

		if(temp != null){
			return temp.isResearcher;
		}
		return false;
	}
	
	public static String h_manager = "query or assert person <id> is a manager";
	public void a_manager(String p) {

		Person temp = Persons.get(p);
		if(temp != null){
			temp.isManager = true;
			Persons.replace(p, temp);
		}
		else {
			temp = new Person(p);
			temp.isManager = true;
			temp.isFounder = false;
			Persons.replace(p, temp);
		}

	}
	public boolean e_manager(String p) {

		Person temp = Persons.get(p);
		if(temp != null){
			return temp.isManager;
		}
		return false;

	}
	
	public static String h_smoker = "query or assert person <id> is a smoker";
	public void a_smoker(String p) {

		Person temp = Persons.get(p);
		if(temp != null){
			temp.isSmoker = true;
			Persons.replace(p, temp);
		}
		else{
			temp = new Person(p);
			temp.isSmoker = true;
			temp.isFounder = false;
			Persons.put(p, temp);
		}
	}
	public boolean e_smoker(String p) {

		Person temp = Persons.get(p);
		if(temp != null){
			return  temp.isSmoker;
		}
		return false;

	}
	
	public static String h_hacker = "query or assert person <id> is a hacker";
	public void a_hacker(String p) {

		Person temp = Persons.get(p);
		if(temp != null){
			temp.isHacker = true;
			Persons.replace(p, temp);
		}
		else{
			temp = new Person(p);
			temp.isHacker = true;
			temp.isFounder = false;
			Persons.put(p,temp);
		}
	}
	public boolean e_hacker(String p) {

		Person temp = Persons.get(p);
		if(temp != null){
			return temp.isHacker;
		}
		return false;
	}
	
	public void a_group(String p, String grp) {

		if(!Groups.containsKey(grp)){
			Groups.put(grp, null);
		}
		ArrayList<Person> groupMembers = Groups.get(grp);

		Person temp = Persons.get(p);
		if(temp != null){
			temp.group(grp);
			Persons.replace(p, temp);

			if(groupMembers == null){
				groupMembers = new ArrayList<>();
				groupMembers.add(temp);
				Groups.replace(grp, groupMembers);
			}else{
				groupMembers.add(temp);
				Groups.replace(grp,groupMembers);
			}

		}
		else{
			temp = new Person(p);
			temp.group(grp);
			temp.isFounder = false;
			Persons.put(p,temp);

			if(groupMembers == null){
				groupMembers = new ArrayList<>();
				groupMembers.add(temp);
				Groups.replace(grp, groupMembers);
			}else{
				groupMembers.add(temp);
				Groups.replace(grp,groupMembers);
			}

		}
	}
	public boolean e_group(String p, String grp) {

		Person temp = Persons.get(p);
		if(temp != null){
			return temp.isInGroup(grp);
		}
		return false;
	}
	
	public void a_project(String p, String prj) {


		if(!ProjectsTemp.containsKey(prj)){
			ProjectsTemp.put(prj, null);
		}

		ArrayList<Person> projectMembers = ProjectsTemp.get(prj);

		Person temp = Persons.get(p);
		if(temp != null){
			temp.project(prj);
			Persons.replace(p, temp);
		}
		else{
			temp = new Person(p);
			temp.project(prj);
			temp.isFounder = false;
			Persons.put(p,temp);
		}

		if(projectMembers == null){
			projectMembers = new ArrayList<>();
			projectMembers.add(temp);
			ProjectsTemp.replace(prj,projectMembers);
		}
		else{
			projectMembers.add(temp);
			ProjectsTemp.replace(prj,projectMembers);
		}



		//if(!Projects.contains(prj))
		//	Projects.add(prj);

	}
	public boolean e_project(String p, String prj) {

		Person temp = Persons.get(p);
		if(temp != null){
			return temp.isInProject(prj);
		}
		return false;

	}
	
	public static String h_heads_group = "query or assert person <id1> heads group <id2>";
	public void a_heads_group(String p, String grp) {

		if(!Groups.containsKey(grp)){
			Groups.put(grp, null);  //create group in Persons if not created already
		}
		ArrayList<Person> groupMembers = Groups.get(grp);

		/*
		if(!Groups.contains(grp)){
			Groups.add(grp);
		}*/
		Person temp = Persons.get(p);
		if(temp != null){
			temp.isGroupHead = true;
			temp.groupHead(grp);
			Persons.replace(p, temp);

			if(groupMembers == null){
				groupMembers = new ArrayList<>();
				groupMembers.add(temp);
				Groups.replace(grp,groupMembers);
			}else {
				if(!groupMembers.contains(temp)){
					groupMembers.add(temp);
					Groups.replace(grp, groupMembers);
				}
			}

		}
		else{
			temp = new Person(p);
			temp.groupHead(grp);
			temp.isFounder = false;
			Persons.put(p,temp);
			if(groupMembers == null){
				groupMembers = new ArrayList<>();
				groupMembers.add(temp);
				Groups.put(grp,groupMembers);
			}else {
				if(!groupMembers.contains(temp)){
					groupMembers.add(temp);
					Groups.replace(grp, groupMembers);
				}
			}
		}
	}
	public boolean e_heads_group(String p, String grp) {

		Person temp = Persons.get(p);
		if(temp != null){
			return Groups.containsKey(grp) && temp.isGroupHead && temp.isGroupHead(grp);
		}
		return  false;
	}
	
	public static String h_heads_project = "query or assert person <id1> heads project <id2>";
	public void a_heads_project(String p, String prj) {

		if(!ProjectsTemp.containsKey(prj)){
			ProjectsTemp.put(prj, null);
		}

		ArrayList<Person> projectMembers = ProjectsTemp.get(prj);

		Person temp = Persons.get(p);
		if(temp != null){
			temp.projectHead = true;
			temp.projectHeads(prj);
			Persons.replace(p, temp);
			if (!Projects.contains(prj)){
				Projects.add(prj);
			}
		}
		else{
			temp = new Person(p);
			temp.projectHeads(prj);
			temp.isFounder = false;
			Persons.put(p,temp);
			if (!Projects.contains(prj)){
				Projects.add(prj);
			}
		}

		if(projectMembers == null){
			projectMembers = new ArrayList<>();
			projectMembers.add(temp);
			ProjectsTemp.replace(prj, projectMembers);
		}
		else {
			projectMembers.add(temp);
			ProjectsTemp.replace(prj,projectMembers);
		}

	}
	public boolean e_heads_project(String p, String prj) {
		Person temp = Persons.get(p);
		if(temp != null){
			return temp.isProjectHead(prj);
		}
		return false;
	}
	
	public static String h_works_with = "query or assert person <id> works with [the person <id2>/all the people in <set>] (reflexive)";
	public void a_works_with(String p, TreeSet<Pair<ParamType,Object>> p2s) {
		Pair[] pairs = new Pair[p2s.size()];
		for (int i = 0; i < pairs.length; i++)
		{
			pairs[i] = p2s.pollFirst();
		}

		Person tempPerson = Persons.get(p);
		if(tempPerson != null){
			for (int i = 0; i < pairs.length; i++)
			{
				//check if person has been created
				String tempPartnerName = (String)pairs[i].getValue();
				Person tempPartner = Persons.get(tempPartnerName);
				if(tempPartner != null){
					tempPartner.worksWith(p);
					Persons.replace(tempPartnerName, tempPartner);
				}
				else{
					tempPartner = new Person(tempPartnerName);
					tempPartner.worksWith(p);
					tempPartner.isFounder = false;
					Persons.put(tempPartnerName, tempPartner);
				}
				tempPerson.worksWith((String)pairs[i].getValue());

			}
			Persons.replace(p, tempPerson);
		}
		else{
			tempPerson = new Person(p);
			tempPerson.isFounder = false;
			for (int i = 0; i < pairs.length; i++)
			{
				//check if person has been created
				String tempPartnerName = (String)pairs[i].getValue();
				Person tempPartner = Persons.get(tempPartnerName);
				if(tempPartner != null){
					tempPartner.worksWith(p);
					Persons.replace(tempPartnerName, tempPartner);
				}
				else{
					tempPartner = new Person(tempPartnerName);
					tempPartner.worksWith(p);
					tempPartner.isFounder = false;
					Persons.put(tempPartnerName, tempPartner);
				}
				tempPerson.worksWith((String)pairs[i].getValue());

			}
			Persons.put(p, tempPerson);
		}


	}
	public boolean e_works_with(String p, TreeSet<Pair<ParamType,Object>> p2s) {
		Pair[] pairs = new Pair[p2s.size()];
		for (int i = 0; i < pairs.length; i++){
			pairs[i] = p2s.pollFirst();
		}

		Person tempPerson = Persons.get(p);
		if(tempPerson != null){
			for (int i = 0; i < pairs.length; i++){
				Person tempPartner = Persons.get((String)pairs[i].getValue());
				if(tempPartner == null || !tempPerson.hasCoworker((String)pairs[i].getValue())){
					return false;
				}
			}
			return true;
		}
		return false;


	}
	
	/*public void a_works_with(String p, String p2) {
		Person temp = Persons.get(p);
		Person partner = Persons.get(p2);
		if(temp != null){
			temp.worksWith(p2);
			Persons.replace(p, temp);
		}
		else{
			temp = new Person(p);
			temp.worksWith(p2);
			Persons.put(p,temp);
		}

		if(partner != null){
			partner.worksWith(p);
			Persons.replace(p2, partner);
		}
		else{
			partner = new Person(p2);
			partner.worksWith(p);
			Persons.put(p2, partner);
		}

	}*/
	public void a_works_with(String p, String p2) {
		Person temp;
		if (Persons.get(p) != null){
			temp = Persons.get(p);
		}
		else {
			temp = new Person(p);
			temp.isFounder = false;
			Persons.put(p, temp);
		}
		Person partner;
		if (Persons.get(p2) != null){
			partner = Persons.get(p2);
		}
		else {
			partner = new Person(p2);
			partner.isFounder = false;
			Persons.put(p2, partner);
		}
		temp.worksWith(p2);
		partner.worksWith(p);
	}
	public boolean e_works_with(String p, String p2) {
		Person temp = Persons.get(p);
		Person partner = Persons.get(p2);
		if(temp == null || partner == null){
			return false;
		}
		return temp.hasCoworker(p2) && partner.hasCoworker(p);
	}
	
	public static String h_assign_to = "query or assert person <id1> is assigned to <id2>";
	public void a_assign_to(String p, String room) throws Exception {
		Person tempPerson = Persons.get(p);

		if (tempPerson == null){
			tempPerson = new Person(p);
			Persons.put(p, tempPerson);
			tempPerson.isFounder = false;
		}
		if (tempPerson.isFounder)
			throw new Exception("Person is already assigned a room.");
		tempPerson.isFounder = true;
		Room tempRoom = Rooms.get(room);
		if (tempRoom == null){
			throw new Exception("Room does not exist.");
		}
		Assignment temp = null;
		if (tempRoom.founderRoom){
			for (Assignment assignment : Assignments){
				if (assignment.getRoom().equals(tempRoom)){
					temp = assignment;
					if (assignment.getPerson().length == 2)
						throw new Exception("Kremer tried to break our hearts.");
					temp.addSecondPerson(tempPerson);
					break;
				}
			}
		}
		else {
			temp = new Assignment(tempRoom, tempPerson);
			tempRoom.founderRoom = true;
		}
		if (temp == null)
			throw new Exception("Something incredible happened.");
		if (!Assignments.contains(temp)){
			Assignments.add(temp);
		}
	}
	public boolean e_assign_to(String p, String room) {
		return Assignments.contains(new Assignment( new Room(room), new Person(p) ));
	}

	// ROOMS
	public static String h_room = "query or assert <id> is a room";
	public void a_room(String r) {
		Room temp = Rooms.get(r);
		if(temp == null){
			temp = new Room(r);
			Rooms.put(r, temp);
		}
		temp.founderRoom = false;
	}
	public boolean e_room(String r) {
		Room temp = Rooms.get(r);
		if(temp != null){
			return true;
		}
		return false;
	}
	public static String h_close = "query or assert room <id> is close to [the room <id2>/all the rooms in <set>] (reflexive)";
	public void a_close(String room, String room2) {
		Room temp = Rooms.get(room);
		Room temp2 = Rooms.get(room2);
		if(temp != null){
			temp.addCloseRoom(room2);
			Rooms.replace(room, temp);
		}
		else{
			temp = new Room(room);
			temp.addCloseRoom(room2);
			temp.founderRoom = false;
			Rooms.put(room, temp);
		}
		if(temp2 != null){
			temp2.addCloseRoom(room);
			Rooms.replace(room2, temp2);
		}
		else{
			temp2 = new Room(room2);
			temp2.addCloseRoom(room2);
			temp2.founderRoom = false;
			Rooms.put(room2, temp2);
		}

	}
	public boolean e_close(String room, String room2) {
		Room temp = Rooms.get(room);
		if(temp != null){
			return temp.hasClose(room2);
		}
		return false;
	}

	public void a_close(String room, TreeSet<Pair<ParamType,Object>> set) {
		Pair[] temp = new Pair[set.size()];
		for(int i =0; i < temp.length; i++){
			temp[i] = set.pollFirst();
		}
		Room roomTemp = Rooms.get(room);
		if(roomTemp == null){
			roomTemp = new Room(room);
			roomTemp.founderRoom = false;
			for (int i = 0; i < temp.length; i++){
				String closeRoom = (String)temp[i].getValue();
				roomTemp.addCloseRoom(closeRoom);
			}
			Rooms.put(room, roomTemp);
		}
		else{
			for (int i = 0; i < temp.length; i++){
				roomTemp.addCloseRoom((String)temp[i].getValue());
			}
			Rooms.replace(room, roomTemp);
		}
		//move rooms
		for (int i = 0; i < temp.length; i++){
			roomTemp = Rooms.get((String)temp[i].getValue());
			if(roomTemp != null){
				roomTemp.addCloseRoom(room);
				Rooms.replace((String)temp[i].getValue(), roomTemp);
			}
			else {
				roomTemp = new Room((String)temp[i].getValue());
				roomTemp.addCloseRoom(room);
				roomTemp.founderRoom = false;
				Rooms.put((String)temp[i].getValue(), roomTemp);
			}
		}
	}
	public boolean e_close(String room, TreeSet<Pair<ParamType,Object>> set) {
		Pair[] pairs = new Pair[set.size()];
		for (int i = 0; i < pairs.length; i++){
			pairs[i] = set.pollFirst();
		}
		Room temp = Rooms.get(room);
		if(temp != null){
			for (int i = 0; i < pairs.length; i++){
				if(!temp.hasClose((String)pairs[i].getValue())){
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	public static String h_large_room = "query or assert <id> is a large-sized room";
	public void a_large_room(String r) {
		Room temp = Rooms.get(r);
		if(temp != null){
			temp.setSize(3);
			Rooms.replace(r, temp);
		}
		else{
			temp = new Room(r);
			temp.setSize(3);
			temp.founderRoom = false;
			Rooms.put(r,temp);
		}

		if(!LargeRooms.contains(r))
			LargeRooms.add(r);
	}
	public boolean e_large_room(String r) {
		Room temp = Rooms.get(r);
		if(temp != null){
			if(temp.getSize() == 3)
				return true;
			return false;
		}
		return false;
	}
	
	public static String h_medium_room = "query or assert <id> is a medium-sized room";
	public void a_medium_room(String r) {
		Room temp = Rooms.get(r);
		if(temp != null){
			temp.setSize(2);
			Rooms.replace(r, temp);
		}
		else{
			temp = new Room(r);
			temp.setSize(2);
			temp.founderRoom = false;
			Rooms.put(r,temp);
		}

		if(!MediumRooms.contains(r))
			MediumRooms.add(r);

	}
	public boolean e_medium_room(String r) {
		Room temp = Rooms.get(r);
		if(temp != null){
			if(temp.getSize() == 2)
				return true;
			return false;
		}
		return false;
	}
	
	public static String h_small_room = "query or assert <id> is a small-sized room";
	public void a_small_room(String r) {
		Room temp = Rooms.get(r);
		if(temp != null){
			temp.setSize(1);
			Rooms.replace(r, temp);
		}
		else{
			temp = new Room(r);
			temp.setSize(1);
			temp.founderRoom = false;
			Rooms.put(r,temp);
		}

		if(!SmallRooms.contains(r))
			SmallRooms.add(r);
	}
	public boolean e_small_room(String r) {
		Room temp = Rooms.get(r);
		if(temp != null){
			if(temp.getSize() == 2)
				return true;
			return false;
		}
		return false;
	}
	
	// GROUPS
	public static String h_group = "query or assert (one-argument) <id> is a group, or (two-argument) person <id1> is a member of group <id2>";
	public void a_group(String g) {

		if(Groups.containsKey(g)){
			Groups.put(g, null);
		}

	}
	public boolean e_group(String g)
	{
		//return Groups.contains(g);
		return  Groups.containsKey(g);

	}
	// PROJECTS
	public static String h_project = "query or assert (one-argument) <id> is a project, or (two-argument) person <id1> is a member of project <id2>";
	public void a_project(String p) {
		if(!Projects.contains(p)){
			Projects.add(p);
		}
	}
	public boolean e_project(String p) {
		return Projects.contains(p);
	}
	
	public static String h_large_project = "query or assert <id> is a large project";
	public void a_large_project(String prj) {
		if (!LargeProjects.contains(prj)){
			LargeProjects.add(prj);
		}
	}
	public boolean e_large_project(String prj) {
		return LargeProjects.contains(prj);
	}

	public void parseAssignments(Fact fact){
		if (fact == null)
			return;
		ArrayList<Assignment> factAssignments = fact.getUnordered_assignments();
		for (Assignment assignment : factAssignments){
			if (!Assignments.contains(assignment)){
				Assignments.add(assignment);
			}
		}
	}
	public String allAssigned(){
		StringBuilder temp = new StringBuilder();
		for (Assignment assignment : Assignments){
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

}
