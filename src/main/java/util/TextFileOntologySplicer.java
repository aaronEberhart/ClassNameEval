package util;

import java.io.*;
import java.util.*;

import ontologyStuff.*;

public class TextFileOntologySplicer {
	
	private Ontology ontology;
	
	private ArrayList<Duple<String,ArrayList<ArrayList<String[]>>>> fileData;
	private ArrayList<Duple<String,ArrayList<Duple<String,Integer>>>> stats;
	
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
		
		addDataToOntology();
	}
	

	public TextFileOntologySplicer(String filename, Ontology o) {
		
		ontology = o;
		
		readFile(filename);
		loadStats();
		
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
		System.out.println(fileData.toString());
	}
	

	private void loadStats() {
		Duple<String,ArrayList<Duple<String,Integer>>> all = new Duple<String,ArrayList<Duple<String,Integer>>>();
		all.x = "all";
		stats.add(all);
		
		for(Duple<String,ArrayList<ArrayList<String[]>>> type : fileData) {
			Duple<String,ArrayList<Duple<String,Integer>>> typeCounts = new Duple<String,ArrayList<Duple<String,Integer>>>();
			ArrayList<Duple<String,Integer>> counts = new ArrayList<Duple<String,Integer>>();
			typeCounts.x = type.x;
			for(ArrayList<String[]> entry : type.y) {
				
			}
		}
	}
	
	public Ontology getOntology() {
		return ontology;
	}
	
}
