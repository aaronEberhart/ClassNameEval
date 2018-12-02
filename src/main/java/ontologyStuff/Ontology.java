package ontologyStuff;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import org.semanticweb.owlapi.apibinding.*;
import org.semanticweb.owlapi.model.*;

public class Ontology {

	private OWLOntologyManager manager;
	private OWLDataFactory dataFactory;
	private OWLOntology ontology;
	private File file;
	private IRI iri;
	
	public Ontology(String filename) {
		
		manager = OWLManager.createOWLOntologyManager();
		dataFactory = manager.getOWLDataFactory();
		
		manager.setOntologyLoaderConfiguration(manager.getOntologyLoaderConfiguration().setMissingImportHandlingStrategy(MissingImportHandlingStrategy.SILENT));
			
		file = new File(filename);
		iri = IRI.create(file.toURI());
			
		try{	
			ontology = manager.loadOntologyFromOntologyDocument(iri);			
		}catch(Exception e) {System.out.println(e);}
	}
	
	public OWLOntology getOntology() {
		return ontology;
	}

	public OWLOntologyManager getManager() {
		return manager;
	}

	public OWLDataFactory getDataFactory() {
		return dataFactory;
	}
	
	public IRI getIRI() {
		return iri;
	}

	public void newOntologyFromFile(String filename) {
		file = new File(filename);
		iri = IRI.create(file.toURI());		
		try{	
			ontology = manager.loadOntologyFromOntologyDocument(iri);			
		}catch(Exception e) {System.out.println(e);}
	}
	
	protected List<OWLClass> getClasses(){
		return asList(this.ontology.classesInSignature());
	}
	
	protected List<OWLDatatype> getDatatypes(){
		return asList(this.ontology.datatypesInSignature());
	}
	
	protected List<OWLObjectProperty> getObjectProperties(){
		return asList(this.ontology.objectPropertiesInSignature());
	}
	
	protected List<OWLDataProperty> getDataProperties(){
		return asList(this.ontology.dataPropertiesInSignature());
	}
	
	protected List<OWLAxiom> getAxioms(){
		return asList(this.ontology.axioms());
	}
	
	protected List<OWLClassAssertionAxiom> getClassAssertionAxioms(OWLIndividual in){
		return asList(this.ontology.classAssertionAxioms(in));
	}
	
	protected List<OWLNamedIndividual> getIndividuals(){
		return asList(this.ontology.individualsInSignature());
	}
	
	protected List<OWLSubClassOfAxiom> getSubClassAxioms(){
		return asList(this.ontology.axioms(AxiomType.SUBCLASS_OF));
	}
	
	protected List<OWLDataPropertyAssertionAxiom> getDataPropertyAxioms(OWLIndividual individual){
		return asList(this.ontology.dataPropertyAssertionAxioms(individual));
	}
	
	protected List<OWLObjectPropertyAxiom> getAxiomsRelatedToObjProp(OWLObjectProperty op){
		return asList(this.ontology.axioms(op));
	}
	
	protected List<OWLDataPropertyAxiom> getAxiomsRelatedToDataProp(OWLDataProperty dp){
		
		return asList(this.ontology.axioms(dp));
	}
	
	@SuppressWarnings("unchecked")
	private static <T> List<T> asList(Stream<T> s){
		return (List<T>) s.collect(Collectors.toList());
	}
}
