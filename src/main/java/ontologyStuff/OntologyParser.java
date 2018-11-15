package ontologyStuff;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.*;

import util.*;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class OntologyParser{
	
	private Ontology ontology;
	private Pair pairs;	
		
	private class Pair{
		private List<OWLClass> classes;
		private List<Duple<OWLClass,List<OWLNamedIndividual>>> individuals;
		
		public Pair(List<OWLClass> c,List<OWLNamedIndividual> in) {
			classes = c;
			matchAxioms(in);
		}
		
		private void matchAxioms(List<OWLNamedIndividual> in) {
			individuals = new ArrayList<Duple<OWLClass,List<OWLNamedIndividual>>>();
			Duple<OWLClass,List<OWLNamedIndividual>> type;
			List<OWLNamedIndividual> temp;
			
			for(OWLClass cla : classes) {
				type = new Duple<OWLClass,List<OWLNamedIndividual>>();
				temp = new ArrayList<OWLNamedIndividual>();
				type.x = cla;
				type.y = temp;
				for(OWLNamedIndividual thing : in) {
					List t  = ontology.getClassAssertionAxioms(thing);
					if(t == null) break;
					OWLClass c = (OWLClass)((OWLClassAssertionAxiom)t.get(0)).classesInSignature().collect(Collectors.toList()).toArray()[0];
					if(c != null && c == cla) {
						(type.y).add(thing);
					}
				}
				individuals.add(type);
			}
		}		
		
		private List popClass() {
			if(classes.isEmpty())
				return null;
			OWLClass cl = classes.get(0);
			classes.remove(0);
			
			if(individuals == null) {
				individuals = new ArrayList<Duple<OWLClass,List<OWLNamedIndividual>>>();
				Duple<OWLClass,List<OWLNamedIndividual>> type = new Duple<OWLClass,List<OWLNamedIndividual>>();
				List<OWLNamedIndividual> temp = new ArrayList<OWLNamedIndividual>();
				type.x = cl;
				type.y = temp;
				individuals.add(type);
			}
			List<OWLNamedIndividual> ax = individuals.get(0).y;
			if(!ax.isEmpty())individuals.remove(0);
						
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
		List<OWLNamedIndividual> axioms = (ArrayList<OWLNamedIndividual>)list.get(1);
		
		List<List<String>> ret = new ArrayList<List<String>>();
		List<String> title = new ArrayList<String>();
		List<String> content = new ArrayList<String>();
				
		for(OWLNamedIndividual ax : axioms) {
			content.add(ax.getIRI().getShortForm());
		}
		
		title.add(cl.getIRI().getShortForm());
		ret.add(title);
		ret.add(content);
		
		return ret;
	}
	
}
