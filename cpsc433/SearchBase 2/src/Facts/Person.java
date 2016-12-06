package Facts;

import java.util.ArrayList;

public class Person{
	private String name;
	private ArrayList<String> coworkers;
	private ArrayList<String> groups;
	private ArrayList<String> projects;
	private  ArrayList<String> groupHead;
	private ArrayList<String> projectHeads;

	public boolean isResearcher, isSecretary, isSmoker, isHacker, isManager, projectHead, isGroupHead, isFounder;



	public Person(String name){
		this.name = name;
		coworkers = new ArrayList<String>();
		groups = new ArrayList<String>();
		projects = new ArrayList<String>();
		groupHead = new ArrayList<String>();
		projectHeads = new ArrayList<String>();
	}

	public void worksWith(String person){
		if (!coworkers.contains(person))
			coworkers.add(person);
	}

	public ArrayList<String > getGroupsPartOf(){
		return new ArrayList<>(groups);
	}

	public void group(String group){
		if (!groups.contains(group))
			groups.add(group);
	}

	public void project(String project){
		if (!projects.contains(project))
			projects.add(project);
	}

	public ArrayList<String> getGroupsHeaded(){
		return groupHead;
	}

	public void groupHead(String group){
		isGroupHead = true;
		if (!groupHead.contains(group)) {
			groupHead.add(group);
		}
	}

	public boolean isGroupHead(String group){
		if (groupHead.contains(group))
				return true;
		return false;
	}

	public boolean isProjectHead(String prj){
		if (projectHeads.contains(prj))
			return true;
		return false;
	}

	public void projectHeads(String project){
		projectHead = true;
		if(!projectHeads.contains(project)){
			projectHeads.add(project);
		}
	}

	public ArrayList<String> getProjectsHeaded(){return new ArrayList<>(projectHeads);}

	public ArrayList<String> getProjects(){return new ArrayList<>(projects);}

	public ArrayList<String> getCoworkers(){
		return new ArrayList<>(coworkers);
	}

	public boolean hasCoworker(String p){
		return coworkers.contains(p);
	}
	public boolean isInGroup(String grp){
		return groups.contains(grp);
	}

	public String toString(){
		String temp = "person(" + name + ")\n";
		if ( isResearcher)
			temp += "researcher(" + name + ")\n";
		if (isSecretary)
			temp += "secretary(" + name + ")\n";
		if(isSmoker)
			temp += "smoker(" + name + ")\n";
		if(isHacker)
			temp += "hacker(" + name + ")\n";
		if(isManager)
			temp += "manager(" + name + ")\n";
		for(int i = 0; i < projectHeads.size(); i++) {
				temp += "heads-project(" + name + ", " + projectHeads.get(i) + ")\n";
		}
		for(int i = 0; i < groupHead.size(); i++) {
			temp += "heads-group(" + name + ", " + groupHead.get(i) + ")\n";
		}

		for(int i = 0; i < coworkers.size(); i++) {
			temp += "works-with(" + name + ", " + coworkers.get(i) + ")\n";
		}
		for(int i = 0; i < projects.size(); i++){
			temp += "project(" + name + ", " + projects.get(i) +")\n";
		}

		for(int i = 0; i < groups.size(); i++){
			temp += "group(" + name + ", " + groups.get(i) + ")\n";
		}

		return temp;
	}

	public boolean isInProject(String prj){
		return projects.contains(prj);
	}
	public boolean isProjectHeads(String prj){ return projectHeads.contains(prj); }
	public String getName(){
		return name;
	}


	public boolean equals(Person p){
		return (p.getName().equals(name));
	}
}