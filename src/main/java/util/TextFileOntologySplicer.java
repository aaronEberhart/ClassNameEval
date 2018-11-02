package util;

import java.io.*;
import java.util.*;

import ontologyStuff.*;

public class TextFileOntologySplicer {
	
	private Ontology ontology;
	
	private ArrayList<Duple<String,ArrayList<ArrayList<String[]>>>> fileData;
	private ArrayList<Duple<String,ArrayList<Duple<String,Integer>>>> stats;
	
	@SuppressWarnings("unused")
	private class Duple<X,Y>{
		public X x;
		public Y y;
		public Duple() {}
		public Duple(X x, Y y) {
			this.x = x;
			this.y = y;
		}
	}
	
	public TextFileOntologySplicer(String textFilename, String ontologyFilename) {
		
		ontology = new Ontology(ontologyFilename);
		
		readFile(textFilename);
		
		loadStats();
		
		outputStats();
		
		addDataToOntology();
	}


	public TextFileOntologySplicer(String filename, Ontology o) {
		
		ontology = o;
		
		readFile(filename);
		
		loadStats();
		
		outputStats();
		
		addDataToOntology();
	}
	
	private void readFile(String filename) {
		fileData = new ArrayList<Duple<String,ArrayList<ArrayList<String[]>>>>();
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			
			Duple<String,ArrayList<ArrayList<String[]>>> type = new Duple<String,ArrayList<ArrayList<String[]>>>();
			ArrayList<ArrayList<String[]>> tuples = new ArrayList<ArrayList<String[]>>();
			ArrayList<String[]> entry = new ArrayList<String[]>();
			String[] tmp;
			String current = "";
			
			while(reader.ready()) {				
				
				String line = reader.readLine();
								
				if(line.matches("^-.*")) {
					tmp = new String[1];
					String id = (line.split("-")[1]).substring(0,3);
					if(current.equals("")) {
						current = id;
						type = new Duple<String,ArrayList<ArrayList<String[]>>>();
						type.x = id;
					}else if(!current.equals(id)){
						type.y = tuples;
						fileData.add(type);
						type = new Duple<String,ArrayList<ArrayList<String[]>>>();
						tuples = new ArrayList<ArrayList<String[]>>();
						type.x = id;
						current = id;
					}
				}else if(line.matches("")) {
					tuples.add(entry);
					entry = new ArrayList<String[]>();
				}else {
					tmp = new String[2];
					tmp = line.split(": ");
					entry.add(tmp);
				}
				
			}
			
			type.y = tuples;
			fileData.add(type);
			
			reader.close();
			
		} catch (Exception e) {e.printStackTrace();}
	}
	
	private void addDataToOntology() {
		//System.out.println(fileData.toString());
	}
	

	private void loadStats() {
		stats = new ArrayList<Duple<String,ArrayList<Duple<String,Integer>>>>();
		
		Duple<String,ArrayList<Duple<String,Integer>>> all = new Duple<String,ArrayList<Duple<String,Integer>>>();
		Duple<String,ArrayList<Duple<String,Integer>>> typeCounts;
		ArrayList<Duple<String,Integer>> counts;
		int index,allIndex;
		all.x = "all";
		all.y = new ArrayList<Duple<String,Integer>>();
		stats.add(all);
		
		for(Duple<String,ArrayList<ArrayList<String[]>>> type : fileData) {
			typeCounts = new Duple<String,ArrayList<Duple<String,Integer>>>();
			typeCounts.y = new ArrayList<Duple<String,Integer>>();
			typeCounts.x = type.x;
			
			for(ArrayList<String[]> entries : type.y) {
				for(String[] pair : entries) {
					index = index(pair[0],typeCounts.y);
					if(index == -1) {
						Duple<String,Integer> add = new Duple<String,Integer>();
						add.x = pair[0];
						add.y = 1;
						typeCounts.y.add(add);
					}else {
						typeCounts.y.get(index).y++;
					}
					allIndex = index(pair[0],all.y);
					if(allIndex == -1) {
						Duple<String,Integer> add = new Duple<String,Integer>();
						add.x = pair[0];
						add.y = 1;
						all.y.add(add);
					}else {
						all.y.get(allIndex).y++;
					}
				}
			}
			
			stats.add(typeCounts);
		}
		Duple<String,Integer> add;
		for(Duple<String,ArrayList<Duple<String,Integer>>> type : stats) {
			if(type.x.equals("all"))continue;
			for(Duple<String,Integer> allStatEntry : stats.get(0).y) {
				if(index(allStatEntry.x,type.y)==-1) {
					add = new Duple<String,Integer>();
					add.x = allStatEntry.x;
					add.y = 0;
					type.y.add(add);
				}
			}
		}
	}
	
	private int index(String val, ArrayList<Duple<String,Integer>> list) {
		if(list.isEmpty())return -1;
		int index = 0;
		for(Duple<String,Integer> pair : list) {
			if(pair.x.equals(val))
				return index;
			index++;
		}
		return -1;
	}
	
	private void outputStats() {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter("stats.txt"));
			
			for(Duple<String,ArrayList<Duple<String,Integer>>> type : stats) {
				writer.write(type.x+"\n\n");
				for(Duple<String,Integer> entry : type.y) {
					writer.write(String.format("%-40s=%25d\n",entry.x,entry.y));
				}
				writer.write("\n\n");
			}
			
			writer.close();
		}catch(Exception e) {e.printStackTrace();}
	}
	
	public Ontology getOntology() {
		return ontology;
	}
	
}
