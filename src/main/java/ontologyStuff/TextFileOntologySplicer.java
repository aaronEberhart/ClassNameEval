package ontologyStuff;

import java.io.*;
import java.util.*;

import org.semanticweb.owlapi.model.*;

import util.*;

public class TextFileOntologySplicer {
	
	private Ontology ontology;
	
	private ArrayList<Duple<String,ArrayList<ArrayList<String[]>>>> fileData;
	private ArrayList<Duple<String,ArrayList<Duple<String,Integer>>>> stats;
	private boolean doneStats = false;
	
	private class Assertion<X,Y> extends Duple<OWLClass,OWLIndividual>{
		public Assertion(OWLClass c,OWLIndividual i) {
			super(c,i);
			makeAssertion();
		}
		private void makeAssertion() {
			AddAxiom add = new AddAxiom(ontology.getOntology(),ontology.getDataFactory().getOWLClassAssertionAxiom(((OWLClassExpression)x),((org.semanticweb.owlapi.model.OWLIndividual)y)));
			ontology.getManager().applyChange(add);
		}
	}
	
	private class Property<X,Y,Z> extends Triple<OWLDataProperty,OWLIndividual,OWLLiteral>{
		public Property(OWLDataProperty d,OWLIndividual i1, OWLLiteral i2) {
			super(d,i1,i2);
			makeAssertion();
		}
		private void makeAssertion() {
			AddAxiom add = new AddAxiom(ontology.getOntology(),ontology.getDataFactory().getOWLDataPropertyAssertionAxiom(((OWLDataPropertyExpression)x),((org.semanticweb.owlapi.model.OWLIndividual)y),(org.semanticweb.owlapi.model.OWLLiteral) z));
			ontology.getManager().applyChange(add);
		}
	}
	
	public TextFileOntologySplicer(String textFilename, String ontologyFilename) throws Exception {
		
		ontology = new Ontology(ontologyFilename);
		
		readFile(textFilename);
		
		addDataToOntology();
		
	}

	public TextFileOntologySplicer(String filename, Ontology o) throws Exception {
		
		ontology = o;
		
		readFile(filename);
		
		addDataToOntology();
		
	}
	
	private void readFile(String filename) {
		fileData = new ArrayList<Duple<String,ArrayList<ArrayList<String[]>>>>();
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			
			Duple<String,ArrayList<ArrayList<String[]>>> type = new Duple<String,ArrayList<ArrayList<String[]>>>();
			ArrayList<ArrayList<String[]>> tuples = new ArrayList<ArrayList<String[]>>();
			ArrayList<String[]> entry = new ArrayList<String[]>();
			String[] tmp;
			String current = "";
			
			while(reader.ready()) {				
				
				String line = reader.readLine();
								
				if(line.matches("^-.*")) {
					tmp = new String[1];
					String id = (line.split("-")[1]).substring(0,3);
					if(current.equals("")) {
						current = id;
						type = new Duple<String,ArrayList<ArrayList<String[]>>>();
						type.x = id;
					}else if(!current.equals(id)){
						type.y = tuples;
						fileData.add(type);
						type = new Duple<String,ArrayList<ArrayList<String[]>>>();
						tuples = new ArrayList<ArrayList<String[]>>();
						type.x = id;
						current = id;
					}
				}else if(line.matches("")) {
					tuples.add(entry);
					entry = new ArrayList<String[]>();
				}else {
					tmp = new String[2];
					tmp = line.split(": ");
					entry.add(tmp);
				}
				
			}
			
			type.y = tuples;
			fileData.add(type);
			
			reader.close();
			
		} catch (Exception e) {e.printStackTrace();}
	}
	
	private void addDataToOntology() throws Exception {
		
		List<OWLClass> classes = ontology.getClasses();
		
		int count;
		String type;
		String[] iri;
		Duple<OWLClass,String> parts;
		OWLDataProperty propIRI;
		OWLIndividual indiv;
		OWLLiteral literal;
		
		
		//look through all known classes
		for(OWLClass classy : classes) {
			
			//and look through the data from the file
			for(Duple<String,ArrayList<ArrayList<String[]>>> typeEntries : fileData) {
				count = 1;
				type = typeEntries.x.toUpperCase();
				
				//for every entry in the data
				for(ArrayList<String[]> entry : typeEntries.y) {
					
					//assert a new individual of that entry type in the class
					iri = classy.getIRI().getIRIString().split("#");
					indiv = ontology.getDataFactory().getOWLNamedIndividual(IRI.create(String.format("%s#%s%02d%s",iri[0],type,count++,iri[1])));
					new Assertion<OWLClass,OWLIndividual>(classy,indiv);
					
					OWLClass mem = ontology.getDataFactory().getOWLClass(IRI.create(String.format("%s#%s",iri[0],"Memory")));
					
					//then look through the data entry
					for(int i = 0; i < entry.size(); i++) {
						
						String[] property = entry.get(i);
						
						//if there are any property assertions in the data 
						//that match this class and individual
						String[] matcher = property[0].split("_");
						String res = matcher.length == 1 ? matcher[0]:String.join("_",Arrays.asList(matcher).subList(1, matcher.length));
						//System.out.println(Arrays.toString(matcher)+"\t"+res);
						parts = matchClassType(matcher,res,property[1].trim());
						
						if(matcher[0].equals("memory") && res.equals("type") && mem.equals(classy)) {
							propIRI = ontology.getDataFactory().getOWLDataProperty(IRI.create(String.format("%s#type",iri[0])));
							literal = ontology.getDataFactory().getOWLLiteral(property[1]);
							
							new Property<OWLDataProperty,OWLIndividual,OWLLiteral>(propIRI,indiv,literal);
						}
						else if(matcher[0].equals("memory") && res.equals("type") && parts.x.equals(classy)){
							
							property = entry.get(++i);
							matcher = property[0].split("_");
							res = matcher.length == 1 ? matcher[0]:String.join("_",Arrays.asList(matcher).subList(1, matcher.length));
							parts = matchClassType(matcher,res,property[1].trim());
							
							//add the type to the ontology
							propIRI = ontology.getDataFactory().getOWLDataProperty(IRI.create(String.format("%s#%s",iri[0],parts.y)));
							
							if(Util.isInteger(property[1]))
								literal = ontology.getDataFactory().getOWLLiteral(Integer.parseInt(property[1]));
							else if(Util.isDouble(property[1]))
								literal = ontology.getDataFactory().getOWLLiteral(Double.parseDouble(property[1]));
							else
								literal = ontology.getDataFactory().getOWLLiteral(property[1]);
							
							new Property<OWLDataProperty,OWLIndividual,OWLLiteral>(propIRI,indiv,literal);//add them to the ontology
							
							property = entry.get(++i);
							matcher = property[0].split("_");
							res = matcher.length == 1 ? matcher[0]:String.join("_",Arrays.asList(matcher).subList(1, matcher.length));
							parts = matchClassType(matcher,res,property[1].trim());
							
							propIRI = ontology.getDataFactory().getOWLDataProperty(IRI.create(String.format("%s#%s",iri[0],parts.y)));
							
							if(Util.isInteger(property[1]))
								literal = ontology.getDataFactory().getOWLLiteral(Integer.parseInt(property[1]));
							else if(Util.isDouble(property[1]))
								literal = ontology.getDataFactory().getOWLLiteral(Double.parseDouble(property[1]));
							else
								literal = ontology.getDataFactory().getOWLLiteral(property[1]);
							
							new Property<OWLDataProperty,OWLIndividual,OWLLiteral>(propIRI,indiv,literal);//add them to the ontology
						}						
						else if(parts.x.equals(classy) && !mem.equals(classy)) {
							
							//add them to the ontology
							propIRI = ontology.getDataFactory().getOWLDataProperty(IRI.create(String.format("%s#%s",iri[0],parts.y)));
							
							if(Util.isInteger(property[1]))
								literal = ontology.getDataFactory().getOWLLiteral(Integer.parseInt(property[1]));
							else if(Util.isDouble(property[1]))
								literal = ontology.getDataFactory().getOWLLiteral(Double.parseDouble(property[1]));
							else
								literal = ontology.getDataFactory().getOWLLiteral(property[1]);
							
							new Property<OWLDataProperty,OWLIndividual,OWLLiteral>(propIRI,indiv,literal);
							
						}
					}
				}
			}
		}
		//save the ontology to a new file
		ontology.getManager().saveOntology(ontology.getOntology(), IRI.create(ontology.getIRI().getIRIString().split(".owl")[0] + "_appended.owl"));
	}
	
	public Duple<OWLClass,String> matchClassType(String[] parts, String res,String val) throws Exception {

		String cla;
		OWLClass cl = null;
		if(parts[0].equals("cpu")) {
			cla = "CPU";
		}else if(parts[0].equals("software")) {
			cla = "Software";
		}else if(parts[0].equals("memory")) {
			if(parts[1].equals("type")) {
				cla = findMemType(val);
			}
			else cla = "Memory";
		}else if(parts[0].equals("operating-system")) {
			if(res.equals("distribution_name") || res.equals("distribution_version")) {
				cla = "Distribution";
				if(res.equals("distribution_name"))res = "osDistributionName";
				else res = "osDistributionVersion";
			}
			else {
				cla = "Kernel";
				if(res.equals("distribution_name"))res = "osKernelName";
				else res = "osKernelVersion";
			}
		}else if(parts[0].equals("computer")) {
			cla = "Computer";
		}else if(parts[0].equals("multicomputer")) {
			cla = "Multicomputer";
		}else if(parts[0].equals("gpu")) {
			if(res.equals("kernel_version")){
				cla = "Kernel";
				res = "gpuKernelVersion";
			}
			else cla = "GPU";
		}else {
			if(res.equals("interconnection-network")) {
				cla = "Multicomputer";
			}else if(res.equals("programming-language")) {
				cla = "What";
				//TODO
			}else if(res.equals("num-processors")) {
				cla = "Multicomputer";
			}else if(res.equals("num-nodes")) {
				cla = "Multicomputer";
			}else {
				throw new Exception("UH OH");
			}
		}
		cl = ontology.getDataFactory().getOWLClass(IRI.create(ontology.getOntology().getOntologyID().getOntologyIRI().get()+"#"+cla));
		return new Duple<OWLClass,String>(cl,res);
	}

	private String findMemType(String res) {
		if(res.equals("RAM") || res.equals("random access memory (RAM)"))return "RAM";
		if(res.equals("heap"))return "Memory";
		if(res.equals("DDRII")|| res.equals("DDR2 RAM")|| res.equals("DDRII RAM"))return "DDRII";
		if(res.equals("DDRIII") || res.equals("DDRIII-1333 RAM") || res.equals("DDR3") || res.equals("DDR3-SDRAM"))return "DDRIII";
		if(res.equals("cache") || res.equals("L2 cache")|| res.equals("L2 shared cache")|| res.equals("Third level cache"))return "Cache";
		if(res.equals("GPU"))return "GPU";
		if(res.equals("Hard drive"))return "Disk";
		return res;
	}

	private void loadStats() {
		stats = new ArrayList<Duple<String,ArrayList<Duple<String,Integer>>>>();
		
		Duple<String,ArrayList<Duple<String,Integer>>> all = new Duple<String,ArrayList<Duple<String,Integer>>>();
		Duple<String,ArrayList<Duple<String,Integer>>> typeCounts;
		Duple<String,Integer> add;
		int index,allIndex;
		all.x = "all";
		all.y = new ArrayList<Duple<String,Integer>>();
		stats.add(all);
		
		for(Duple<String,ArrayList<ArrayList<String[]>>> type : fileData) {
			typeCounts = new Duple<String,ArrayList<Duple<String,Integer>>>();
			typeCounts.y = new ArrayList<Duple<String,Integer>>();
			typeCounts.x = type.x;
			stats.add(typeCounts);
			
			for(ArrayList<String[]> entries : type.y) {
				for(String[] pair : entries) {
					index = index(pair[0],typeCounts.y);
					if(index == -1) {
						add = new Duple<String,Integer>();
						add.x = pair[0];
						add.y = 1;
						typeCounts.y.add(add);
					}else {
						typeCounts.y.get(index).y++;
					}
					allIndex = index(pair[0],all.y);
					if(allIndex == -1) {
						add = new Duple<String,Integer>();
						add.x = pair[0];
						add.y = 1;
						all.y.add(add);
					}else {
						all.y.get(allIndex).y++;
					}
				}
			}
		}
		
		for(Duple<String,ArrayList<Duple<String,Integer>>> type : stats) {
			if(type.x.equals("all"))continue;
			for(Duple<String,Integer> allStatEntry : stats.get(0).y) {
				if(index(allStatEntry.x,type.y)==-1) {
					add = new Duple<String,Integer>();
					add.x = allStatEntry.x;
					add.y = 0;
					type.y.add(add);
				}
			}
		}
		
		for(Duple<String,ArrayList<Duple<String,Integer>>> type : stats) {
			Collections.sort( type.y, new Comparator<Duple<String,Integer>>( ){
				public int compare(Duple<String, Integer> dup1, Duple<String, Integer> dup2) {
					return dup2.y - dup1.y;
				}});
		}
	}
	
	private int index(String val, ArrayList<Duple<String,Integer>> list) {
		if(list.isEmpty())return -1;
		int index = 0;
		for(Duple<String,Integer> pair : list) {
			if(pair.x.equals(val))
				return index;
			index++;
		}
		return -1;
	}
	
	private void outputStats() {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter("stats.txt"));
			
			for(Duple<String,ArrayList<Duple<String,Integer>>> type : stats) {
				writer.write(type.x+"\n\n");
				for(Duple<String,Integer> entry : type.y) {
					writer.write(String.format("%-40s=%25d\n",entry.x,entry.y));
				}
				writer.write("\n\n");
			}
			
			writer.close();
			
		}catch(Exception e) {e.printStackTrace();}
	}
	
	public boolean runStats() {
		if(!doneStats) {
			loadStats();
			outputStats();
			doneStats = true;
		}
		return doneStats;
	}
	
	public Ontology getOntology() {
		return ontology;
	}
	
}
