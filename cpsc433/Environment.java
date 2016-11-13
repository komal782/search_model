/**
 * 
 */
package cpsc433;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map;
import java.text.ParseException;
import java.util.Iterator;
import java.util.Vector;

import java.util.ArrayList;

import cpsc433.Predicate.ParamType;

/**
 * This is class extends {@link cpsc433.PredicateReader} just as required to 
 * in the assignment. You can extend this class to include your predicate definitions
 * or you can create another class that extends {@link cpsc433.PredicateReader} and
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

	ArrayList<Person> people = new ArrayList<Person>();

	protected Environment(String name) {
		super(name==null?"theEnvironment":name);
	}
	
	/**
	 * A getter for the global instance of this class.  If an instance of this class
	 * does not already exist, it will be created.
	 * @return The singleton (global) instance.
	 */
	public static Environment get() {
		if (instance==null) instance = new Environment(null);
		return instance;
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
		if (!people.contains(new Person(p))){
			people.add(new Person(p));
		}
	} 
	public boolean e_person(String p){
		if (people.contains(new Person(p)))
			return true;
		return false;
	}
	
	public static String h_secretary = "query or assert person <id> is a secretary";
	public void a_secretary(String p) {}
	public boolean e_secretary(String p) {return true;}
	
	public static String h_researcher = "query or assert person <id> is a researcher";
	public void a_researcher(String p){}
	public boolean e_researcher(String p) {return true;}
	
	public static String h_manager = "query or assert person <id> is a manager";
	public void a_manager(String p) {}
	public boolean e_manager(String p) {return true;}
	
	public static String h_smoker = "query or assert person <id> is a smoker";
	public void a_smoker(String p) {}
	public boolean e_smoker(String p) {return true;}
	
	public static String h_hacker = "query or assert person <id> is a hacker";
	public void a_hacker(String p) {}
	public boolean e_hacker(String p) {return true;}
	
	public void a_group(String p, String grp) {}
	public boolean e_group(String p, String grp) {return true;}
	
	public void a_project(String p, String prj) {}
	public boolean e_project(String p, String prj) {return true;}
	
	public static String h_heads_group = "query or assert person <id1> heads group <id2>";
	public void a_heads_group(String p, String grp){}
	public boolean e_heads_group(String p, String grp) {return true;}
	
	public static String h_heads_project = "query or assert person <id1> heads project <id2>";
	public void a_heads_project(String p, String prj) {}
	public boolean e_heads_project(String p, String prj) {return true;}
	
	public static String h_works_with = "query or assert person <id> works with [the person <id2>/all the people in <set>] (reflexive)";
	public void a_works_with(String p, TreeSet<Pair<ParamType,Object>> p2s) {}
	public boolean e_works_with(String p, TreeSet<Pair<ParamType,Object>> p2s) {return true;}
	
	public void a_works_with(String p, String p2) {}
	public boolean e_works_with(String p, String p2) {return true;}
	
	public static String h_assign_to = "query or assert person <id1> is assigned to <id2>";
	public void a_assign_to(String p, String room) throws Exception {}
	public boolean e_assign_to(String p, String room) {return true;}

	// ROOMS
	public static String h_room = "query or assert <id> is a room";
	public void a_room(String r) {}
	public boolean e_room(String r) {return true;}
	
	public static String h_close = "query or assert room <id> is close to [the room <id2>/all the rooms in <set>] (reflexive)";
	public void a_close(String room, String room2) {}	
	public boolean e_close(String room, String room2) {return true;}

	public void a_close(String room, TreeSet<Pair<ParamType,Object>> set) {}
	public boolean e_close(String room, TreeSet<Pair<ParamType,Object>> set) {return true;}
	
	public static String h_large_room = "query or assert <id> is a large-sized room";
	public void a_large_room(String r) {}
	public boolean e_large_room(String r) {return true;}
	
	public static String h_medium_room = "query or assert <id> is a medium-sized room";
	public void a_medium_room(String r) {}
	public boolean e_medium_room(String r) {return true;}
	
	public static String h_small_room = "query or assert <id> is a small-sized room";
	public void a_small_room(String r) {}
	public boolean e_small_room(String r) {return true;}
	
	// GROUPS
	public static String h_group = "query or assert (one-argument) <id> is a group, or (two-argument) person <id1> is a member of group <id2>";
	public void a_group(String g) {}
	public boolean e_group(String g) {return true;}
	
	// PROJECTS
	public static String h_project = "query or assert (one-argument) <id> is a project, or (two-argument) person <id1> is a member of project <id2>";
	public void a_project(String p) {}
	public boolean e_project(String p) {return true;}
	
	public static String h_large_project = "query or assert <id> is a large project";
	public void a_large_project(String prj) {}
	public boolean e_large_project(String prj) {return true;}
}
