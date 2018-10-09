package ontologyStuff;

import java.io.*;
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
}
