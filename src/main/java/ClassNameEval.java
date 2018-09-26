

import java.io.File;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class ClassNameEval {
	
	public static void main(String[] args) {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		try {
			OWLOntology ontology = manager.loadOntologyFromOntologyDocument(new File("OWLs/ComputationalEnvironment_modified.owl"));
			System.out.println(ontology.toString());
		}catch(Exception e) {}
	}
	
}
