package main;

import java.io.*;
import java.util.*;

import ontologyStuff.*;
import util.TextFileOntologySplicer;

public class ClassNameEval {
	
	public static void main(String[] args) {
		TextFileOntologySplicer fileOnt = new TextFileOntologySplicer("OWLs/answers.txt","OWLs/ComputationalEnvironment_modified.owl");
		Ontology onto = fileOnt.getOntology();
		OntologyParser parser = new OntologyParser(onto);
		
//		List<List<String>> s;
//		do {
//			s = parser.getStringsFromOneClass();
//			System.out.println(s.toString());
//		}while(s != null);
//			
		
	}
	
}
