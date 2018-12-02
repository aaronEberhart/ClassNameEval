package main;

import java.util.*;

import ontologyStuff.*;

public class ClassNameEval {
	
	public static void main(String[] args) throws Exception {
		TextFileOntologySplicer fileOnt = new TextFileOntologySplicer("OWLs/answers.txt","OWLs/ComputationalEnvironment_modified.owl");
		Ontology onto = fileOnt.getOntology();
		OntologyParser parser = new OntologyParser(onto);
		
		List<List<List<String>>> s = parser.getAllStringsFromClasses();
		
		for(List<List<String>> stringLists : s) {
			System.out.println(Arrays.toString(stringLists.toArray()));
		}
		
	}
	
}
