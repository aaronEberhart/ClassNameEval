package ontologyStuff;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.*;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class OntologyParser{
	
	private Ontology ontology;
	private Pair pairs;	
	
	private class Pair{
		private List<OWLClass> classes;
		private List<List<OWLNamedIndividual>> individuals;
		
		public Pair(List<OWLClass> c,List<OWLNamedIndividual> in) {
			classes = c;
			matchAxioms(in);
		}
		
		private void matchAxioms(List<OWLNamedIndividual> in) {			
			for(OWLClass cla : classes) {
				List<OWLNamedIndividual> temp = new ArrayList();
				for(OWLNamedIndividual thing : in) {
					List t  = ontology.getClassAssertionAxioms(thing);
					if(t == null) break;
					Object[] c = ((OWLClassAssertionAxiom)t.get(0)).classesInSignature().collect(Collectors.toList()).toArray();
					if(c != null) System.out.println(c[0]);
					
				}
			}
		}		
		
		private List popClass() {
			if(classes.isEmpty())
				return null;
			OWLClass cl = classes.get(0);
			classes.remove(0);
			
			List<OWLNamedIndividual> ax = individuals.get(0);
			individuals.remove(0);
			
			List ret = new ArrayList();
			ret.add(cl);
			ret.add(ax);
			
			return ret;
		}
	}
	
	public OntologyParser(Ontology o) {
		switchOntologyFile(o);
	}
	
	public OntologyParser(String filename) {
		switchOntologyFile(filename);
	}
	
	public void switchOntologyFile(Ontology o) {
		ontology = o;
		pairs = new Pair(ontology.getClasses(),ontology.getIndividuals());
	}
	
	public void switchOntologyFile(String filename) {
		ontology = new Ontology(filename);
		pairs = new Pair(ontology.getClasses(),ontology.getIndividuals());
	}
	
	

	public List<List<String>> getStringsFromOneClass() {
		List list = pairs.popClass();
		if(list == null) return null;
		
		OWLClass cl = (OWLClass)list.get(0);
		List<OWLNamedIndividual> axioms = (List<OWLNamedIndividual>)list.get(1);
		
		List<List<String>> ret = new ArrayList<List<String>>();
		List<String> title = new ArrayList<String>();
		List<String> content = new ArrayList<String>();
				
		for(OWLNamedIndividual ax : axioms) {
			content.add(ax.toString());
		}
		
		title.add(cl.getIRI().getShortForm());
		ret.add(title);
		ret.add(content);
		
		return ret;
	}
	
}
