package cpsc433;

import java.util.ArrayList;

public class Person{
	String name;
	ArrayList<String> coworkers;
	ArrayList<String> jobs;
	ArrayList<String> groups;
	ArrayList<String> projects;
	ArrayList<String> attributes;


	Person(String name){
		this.name = name;
		coworkers = new ArrayList<String>();
		jobs = new ArrayList<String>();
		groups = new ArrayList<String>();
		projects = new ArrayList<String>();
		attributes = new ArrayList<String>();
	}

	public void worksWith(String person){
		if (!coworkers.contains(person))
			coworkers.add(person);
	}

	public void hasJob(String job){
		if (!jobs.contains(job))
			jobs.add(job);
	}

	public void inGroup(String group){
		if (!groups.contains(group))
			groups.add(group);
	}

	public void onProject(String project){
		if (!projects.contains(project))
			projects.add(project);
	}

	public void hasAttribute(String attribute){
		if (!attributes.contains(attribute))
			attributes.add(attribute);
	}

	public String getName(){
		return name;
	}

	public boolean equals(Person p){
		return (p.getName().equals(name));
	}
}