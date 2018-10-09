package main;

import java.io.*;
import ontologyStuff.*;

public class ClassNameEval {
	
	public static void main(String[] args) {
		Ontology onto = new Ontology("OWLs/ComputationalEnvironment_modified.owl");
		System.out.println(onto.getOntology().toString());
	}
	
}
