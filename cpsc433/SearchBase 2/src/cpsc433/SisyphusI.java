package cpsc433;

import Facts.*;
import SetBased.SetBased;

import java.io.PrintWriter;
import java.util.*;

/**
 * This is the main class for the SysiphusI assignment.  It's main function is to
 * interpret the command line.
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
public class SisyphusI {

	/**
	 * Merely create a new SisypyusI object, and let the constructor run the program.
	 * @param args The command line argument list
	 */
	public static void main(String[] args) {

		new SisyphusI(args);

	}
	
	protected final String[] args;
	protected String out;
	protected Environment env;
	
	
	public SisyphusI(String[] args) {
		this.args = args;
		run();
	}

	protected void run() {


		long startTime = System.currentTimeMillis();
		env = getEnvironment();

		String fromFile = null;

		if (args.length>0) {
			fromFile = args[0];
			env.fromFile(fromFile);
			ArrayDeque<Fact> Facts = new ArrayDeque<>();
			int factsToMake = 1;
			Fact bestFact = null;
			int bestScore = Integer.MIN_VALUE;
			for (int i = 0; i < factsToMake; i++){
				ArrayDeque<Person> personQ = new ArrayDeque<>(env.getPersons().values()); //initial shit
				ArrayDeque<Room> roomQ = new ArrayDeque<>(env.getRooms().values()); //initial shit
				Fact fact = new Fact(env.getAssignments(), new ArrayList<>(roomQ));
				roomQ = Tree.buildTree(roomQ, personQ, fact);
				fact.setSpareRooms(roomQ);
				fact.setAll();
				Facts.push(fact);
				ArrayList<Assignment> assignment = fact.getAssignments();
				for (Assignment ass : assignment){
					Person[] p = ass.getPerson();
					if(p.length > 2){
						continue;
					}
					if (p.length == 2) {
						if ((p[0].isManager || p[1].isManager) && p.length == 2){
							//build tree
							continue;
						}
						else if ((p[0].isGroupHead || p[1].isGroupHead) && p.length == 2){
							//build tree
							continue;
						}
					}

				}
				fact.CalculateViolations();
				int score = fact.getScore();
				Room andy = new Room("Andy");
				andy.founderRoom = true;
				System.out.println(fact);
				if (score > bestScore){
					bestFact = fact;
					bestScore = score;
				}
			}
			long currentTime   = System.currentTimeMillis();
			long runTime = 100;
			//System.out.println(fact.getScore());
			int score = -1;
			while (((currentTime - startTime) < runTime) && (score != 0)){
				Facts.push(SetBased.Extension(Facts.pop()));
				Facts.peekLast().setAll();
				Facts.peekLast().CalculateViolations();
				score = Facts.peekLast().getScore();
				System.out.println(Facts.peekLast().getScore());
				if (score > bestScore){
					bestFact = Facts.peekLast();
					bestScore = score;
				}
				//
				currentTime = System.currentTimeMillis();
			}
			//fact.CalculateViolations();
			//System.out.println(fact.getScore());
//			PriorityQueue<Assignment> test = fact.getAssignmentPriorityQueue();
//			Map<Room, Person[]> t = fact.getOccupants();
//			Person[] d = t.get(test.remove().getRoom());
			env.parseAssignments(bestFact);
//			fact.getScore();
			//Fact fact2 = SetBased.ExtentionTest(fact);


			int i =0;
		}
		else {
			printSynopsis();
		}

		out = fromFile+".out";

		createShutdownHook();

		if (args.length>1) { // using command-line arguments
			runCommandLineMode();
			killShutdownHook();
		}
		else { // using interactive mode
			runInteractiveMode();
			killShutdownHook();
		}
	}
	
	/**
	 * Return the environment object.  One should return an environment object that 
	 * makes sense for YOUR solution to the problem: the environment could contain 
	 * all the object instances required for the domain (like people, rooms, etc),
	 * as well as potential solutions and partial solutions.
	 * @return The global environment object.
	 */
	protected Environment getEnvironment() {
		return Environment.get();
	}
	
	protected void printSynopsis() {
		System.out.println("Synopsis: SisyphusI [<env-file> [<time-in-ms>]]");
	}

	/**
	 * If you want to install a shutdown hook, you can do that here.  A shutdown
	 * hook is completely optional, but can be useful if you search doesn't exit
	 * in a timely manner.
	 */
	protected void createShutdownHook() {}
	protected void killShutdownHook() {}
	
	/**
	 * Run in "Command line mode", that is, batch mode.
	 */
	protected void runCommandLineMode() {
		try {
			long timeLimit = new Long(args[1]).longValue(); 
			//timeLimit -= (System.currentTimeMillis()-startTime);
				System.out.println("Performing search for "+timeLimit+"ms");
				try {
					doSearch(env, timeLimit);
				} catch (Throwable e) {
					e.printStackTrace();
				}
		}
		catch (NumberFormatException ex) {
			System.out.println("Error: The 2nd argument must be a long integer.");
			printSynopsis();
			System.exit(-1);
		}
		printResults();
	}
	
	/**
	 * Perform the actual search
	 * @param env An Environment object.
	 * @param timeLimit A time limit in milliseconds.
	 */
	protected void doSearch(Environment env, long timeLimit) {
		System.out.println("Would do a search for "+timeLimit+" milliseconds here, but it's not defined yet.");
	}
	
	protected void printResults() {
		String output;
		output = getEnvironment().allAssigned();
		System.out.println(output);
		try{
			PrintWriter write = new PrintWriter(out, "UTF-8");
			write.write(output);
			write.close();
		}catch (Exception e){

		}


	}
	
	protected void runInteractiveMode() {
		final int maxBuf = 200;
		byte[] buf = new byte[maxBuf];
		int length;
		try {
			System.out.print("\nSisyphus I: query using predicates, assert using \"!\" prefixing predicates;\n !exit() to quit; !help() for help.\n\n> ");
			while ((length=System.in.read(buf))!=-1) {
				String s = new String(buf,0,length);
				s = s.trim();
				if (s.equals("exit")) break;
				if (s.equals("?")||s.equals("help")) {
					s = "!help()";
					System.out.println("> !help()");
				}
				if (s.length()>0) {
					if (s.charAt(0)=='!') 
						env.assert_(s.substring(1));
					else 
						System.out.print(" --> "+env.eval(s));
				}
				System.out.print("\n> ");
			}
		} catch (Exception e) {
			System.err.println("exiting: "+e.toString());
		}
	}
}
