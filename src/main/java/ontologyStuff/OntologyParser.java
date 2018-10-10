package ontologyStuff;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.*;

public class OntologyParser{
	
	private ShortFormProvider shortFormProvider = new SimpleShortFormProvider();
	private Ontology ontology;
	
	public OntologyParser(Ontology o) {
		ontology = o;
	}
	
	public OntologyParser(String filename) {
		ontology = new Ontology(filename);
	}
	
	public void switchOntologyFile(Ontology o) {
		ontology = o;
	}
	
	public void switchOntologyFile(String filename) {
		ontology = new Ontology(filename);
	}

	
}
