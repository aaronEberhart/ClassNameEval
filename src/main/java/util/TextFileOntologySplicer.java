package util;

import java.io.*;
import java.util.*;

import ontologyStuff.*;

public class TextFileOntologySplicer {
	
	private Ontology ontology;
	private ArrayList<ArrayList<String[]>> fileData;
	private ArrayList<ArrayList<Integer>> stats;
	
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
		fileData = new ArrayList<ArrayList<String[]>>();
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			
			ArrayList<String[]> lines = new ArrayList<String[]>();
			String[] tmp;
			
			while(reader.ready()) {				
				
				String line = reader.readLine();
								
				if(line.matches("^-.*")) {
					tmp = new String[2];
					String id = line.split("-")[1];
					tmp[0] = id.substring(0,3);
					tmp[1] = id.substring(3);
					lines.add(tmp);
				}else if(line.matches("")) {
					fileData.add(lines);
					lines = new ArrayList<String[]>();
				}else {
					tmp = new String[2];
					tmp = line.split(": ");
					lines.add(tmp);
				}
				
			}
			
			reader.close();
			
		} catch (Exception e) {e.printStackTrace();}
	}
	
	private void addDataToOntology() {
		System.out.println(fileData.toString());
	}
	

	private void loadStats() {
		
	}
	
	public Ontology getOntology() {
		return ontology;
	}
	
}
