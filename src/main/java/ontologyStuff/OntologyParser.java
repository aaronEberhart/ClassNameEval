package ontologyStuff;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.*;

public class OntologyParser{
	
	private ShortFormProvider shortFormProvider = new SimpleShortFormProvider();
	private Ontology ontology;
	
	public OntologyParser(Ontology o) {
		ontology = o;
	}
	
	
	
}
