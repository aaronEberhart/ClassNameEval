package main;

import java.io.*;
import java.util.*;

import ontologyStuff.*;

public class ClassNameEval {
	
	public static void main(String[] args) throws Exception {
		TextFileOntologySplicer fileOnt = new TextFileOntologySplicer("OWLs/answers.txt","OWLs/ComputationalEnvironment_modified.owl");
		Ontology onto = fileOnt.getOntology();
		OntologyParser parser = new OntologyParser(onto);
		
		char opt = 'l';
		List<List<String>> s = parser.getStringsFromOneClass(opt);
		for( ; s != null; s = parser.getStringsFromOneClass(opt)) {
			System.out.println(s.toString());
		}
			
		
	}
	
}
