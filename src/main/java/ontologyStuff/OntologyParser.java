package ontologyStuff;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.*;

import util.*;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class OntologyParser{
	
	private Ontology ontology;
	private Threeple pairs;	
		
	private class Threeple{
		private int index;
		private List<OWLClass> classes;
		private List<Duple<OWLClass,List<OWLNamedIndividual>>> individuals;
		private List<Duple<OWLClass,List<OWLDataPropertyAxiom>>> properties;
		
		public Threeple(List<OWLClass> c,List<OWLNamedIndividual> in) {
			classes = c;
			index = -1;
			matchClassIndividuals(in);
		}
		
		private void matchClassIndividuals(List<OWLNamedIndividual> in) {
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
			if(index == pairs.classes.size()) {
				index = -1;
				return null;
			}
			else if(index == -1)
				index = 0;
			
			OWLClass cl = classes.get(index);
			
			if(individuals == null) {
				individuals = new ArrayList<Duple<OWLClass,List<OWLNamedIndividual>>>();
				Duple<OWLClass,List<OWLNamedIndividual>> type = new Duple<OWLClass,List<OWLNamedIndividual>>();
				List<OWLNamedIndividual> temp = new ArrayList<OWLNamedIndividual>();
				type.x = cl;
				type.y = temp;
				individuals.add(type);
			}
			List<OWLNamedIndividual> ax = individuals.get(index++).y;
			//if(!ax.isEmpty())individuals.add(individuals.remove(0));
						
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
		pairs = new Threeple(ontology.getClasses(),ontology.getIndividuals());
	}
	
	public void switchOntologyFile(String filename) {
		ontology = new Ontology(filename);
		pairs = new Threeple(ontology.getClasses(),ontology.getIndividuals());
	}
	
	public List<List<List<String>>> getAllStringsFromClasses() {
		List<List<List<String>>> ret = new ArrayList<List<List<String>>>();
		List<List<String>> s = getStringsFromOneClass();
		
		
		for(; s != null; s = getStringsFromOneClass()) {ret.add(s);}
		
		return ret;
	}

	private List<List<String>> getStringsFromOneClass() {
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
