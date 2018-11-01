package main;

import java.io.*;
import java.util.List;

import ontologyStuff.*;

public class ClassNameEval {
	
	public static void main(String[] args) {
		Ontology onto = new Ontology("OWLs/ComputationalEnvironment_modified_.owl");
		OntologyParser parser = new OntologyParser(onto);
		List<List<String>> s;
		do {
			s = parser.getStringsFromOneClass();
			System.out.println(s.toString());
		}while(s != null);
			
		
	}
	
}
