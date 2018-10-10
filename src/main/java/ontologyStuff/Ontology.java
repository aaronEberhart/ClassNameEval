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
			
		file = new File("OWLs/ComputationalEnvironment_modified.owl");
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

	public void newOntologyFromFile(String filename) {
		file = new File(filename);
		iri = IRI.create(file.toURI());		
		try{	
			ontology = manager.loadOntologyFromOntologyDocument(iri);			
		}catch(Exception e) {System.out.println(e);}
	}
	
	public List<OWLClass> getClasses(){
		return asList(this.ontology.classesInSignature());
	}
	
	public List<OWLDatatype> getDatatypes(){
		return asList(this.ontology.datatypesInSignature());
	}
	
	public List<OWLObjectProperty> getObjectProperties(){
		return asList(this.ontology.objectPropertiesInSignature());
	}
	
	public List<OWLDataProperty> getDataProperties(){
		return asList(this.ontology.dataPropertiesInSignature());
	}
	
	public List<OWLAxiom> getAxioms(){
		return asList(this.ontology.axioms());
	}
	
	public List<OWLSubClassOfAxiom> getSubClassAxioms(){
		return asList(this.ontology.axioms(AxiomType.SUBCLASS_OF));
	}
	
	public List<OWLObjectPropertyAxiom> getAxiomsRelatedToObjProp(OWLObjectProperty op){
		return asList(this.ontology.axioms(op));
	}
	
	public List<OWLDataPropertyAxiom> getAxiomsRelatedToDataProp(OWLDataProperty dp){
		return asList(this.ontology.axioms(dp));
	}
	
	@SuppressWarnings("unchecked")
	private static <T> List<T> asList(Stream<T> s){
		return (List<T>) s.collect(Collectors.toList());
	}
}
