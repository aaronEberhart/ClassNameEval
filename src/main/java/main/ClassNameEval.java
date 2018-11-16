package main;

import java.io.*;
import java.util.*;

import ontologyStuff.*;
import util.*;

public class ClassNameEval {
	
	public static void main(String[] args) throws Exception {
		TextFileOntologySplicer fileOnt = new TextFileOntologySplicer("OWLs/answers.txt","OWLs/ComputationalEnvironment_modified.owl");
		Ontology onto = fileOnt.getOntology();
		OntologyParser parser = new OntologyParser(onto);
		
<<<<<<< HEAD
		char opt = 'l';
		List<List<String>> s = parser.getStringsFromOneClass(opt);
		for( ; s != null; s = parser.getStringsFromOneClass(opt)) {
			System.out.println(s.toString());
=======
		List<List<List<String>>> s = parser.getAllStringsFromClasses();
		
		for(List<List<String>> stringLists : s) {
			System.out.println(Arrays.toString(stringLists.toArray()));
>>>>>>> refs/remotes/origin/master
		}
		
	}
	
}
