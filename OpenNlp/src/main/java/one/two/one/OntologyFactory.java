package one.two.one;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.ontology.DatatypeProperty;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.ontology.Ontology;
import org.apache.jena.rdf.model.Model;
//import org.apache.jena.ontology
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.XSD;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;


public class OntologyFactory{
	public static String link="http://dissertation/ontology";
	public static String base="http://dissertation/ontology#";
	public static String polychrest_ontology="resource\\dissertation_ontology.ttl";
	public static String owlForm="resource\\ontology_owl.owl";
	
	public static OntModel model;
	public static Ontology ontology;
	public static OntClass subject, object;
	public static ObjectProperty predicate;
    public static Individual subjectIndividual, predicateIndividual, objectIndividual;
    
    public static void main(String[] args) throws Exception{
		OntologyFactory ontology=new OntologyFactory();
		ontology.createOntology();
		ontology.createExcelInstances();
		ontology.writeOntology();
	}

	public void createOntology() throws Exception {
		model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
		model.setNsPrefix("base", base);
		ontology = model.createOntology(link);
        ontology.addLabel("Dissertation Ontology", "en");
        ontology.addProperty(DCTerms.description, "Dissertation Ontology");
        ontology.addProperty(DCTerms.contributor, "Anirban");
        
        /*--------------------- Define Classes ---------------------*/
        subject = model.createClass(base + "subject");
        subject.addLabel("subject", "en");
        
        object = model.createClass(base + "object");
        object.addLabel("object", "en");
        
        
	}
	
	@SuppressWarnings("deprecation")
	private void createExcelInstances() {
		// Reading the values from the triple store and creating instances dynamically.
		try {
		File file = new File("D:\\Subjects\\dissertation\\DATASET\\tripleStore.csv"); 
		CSVReader reader = new CSVReader(new FileReader("D:\\Subjects\\dissertation\\DATASET\\tripleStore.csv"), ',' , '"' , 1);
	    String[] nextLine;
	    
	    Map<String, SynSet> synsetMap=getSynSet();
	    int c=0;
		while ((nextLine = reader.readNext()) != null) {
			     if (nextLine != null) {
			    	 createInstances(nextLine[0], nextLine[1], nextLine[2],nextLine[3],synsetMap);
			     }
			  //   if(c++>10) break;
			}
		} catch (Exception e) {e.printStackTrace();}
	    
		
	}
	
	public void createInstances(String subjectString, String predicateString, String objectString,String question, Map<String, SynSet> synsetMap) {
		SynSet synset=synsetMap.get(question);
		
		//processing to remove special chars
		subjectString=processStrings(subjectString);
		predicateString=processStrings(predicateString);
		objectString=processStrings(objectString);
		
		
		
		/*
		 * predicateString= predicateString.trim().replaceAll(" ", "_").replaceAll("%",
		 * "percent") .replaceAll("#", "hash").replaceAll("<", "").replaceAll("=", "eq")
		 * .replaceAll("^", " "); subjectString= subjectString.trim().replaceAll(" ",
		 * "_").replaceAll("%", "percent") .replaceAll("#", "hash").replaceAll("<",
		 * "").replaceAll("=", "eq") .replaceAll("^", " "); objectString=
		 * objectString.trim().replaceAll(" ", "_").replaceAll("%", "percent")
		 * .replaceAll("#", "hash").replaceAll("<", "").replaceAll("=", "eq")
		 * .replaceAll("^", " ");
		 */
		 
		 
		
		if( predicateString.equals(null)||predicateString.equals(" ") || predicateString.equals(""))  
		{ return; }
		
		else {
		//Setting predicate dynamically.
		predicate = model.createObjectProperty(base + predicateString);
        predicate.setDomain(subject);
        predicate.setRange(object);
        predicate.addLabel(question,"en");
        
        if(synset!=null)
	        for(String s: synset.predicateSet) {
	        	predicate.addComment(s, "en");
	        }
        
        
		}
	
        if(subjectString.equals(null)) {
        	
        	return; 
        }
        
        else if (subjectString.equals("")|| subjectString.equals(" ")) {
        	
        	subjectString="bn";
        }
        
        else {
        	
        //URI creation for subject and annotating the question.
		subjectIndividual=subject.createIndividual(base+subjectString);
		subjectIndividual.addLabel(question,"en");
		
		if(synset!=null)
			for(String s: synset.subjectSet) {
	        	subjectIndividual.addComment(s, "en");
	        }
		
      } 
        
        
        if( objectString.equals(null)||objectString.equals(" ") ||objectString.equals(""))   {
        	return;
        }
        
        else {
		//URI creation for object and annotating it with the question.
		objectIndividual=object.createIndividual(base+objectString);
		objectIndividual.addLabel(question,"en");
		
		if(synset!=null)
			for(String s: synset.objectSet) {
	        	objectIndividual.addComment(s, "en");
	        }
		
        }
		
		//This is where the RDF graph is formed dynamically.
		subjectIndividual.addProperty(predicate, objectIndividual);
		
	}
	
	public String processStrings(String words) {
		try {
		words = words.trim().replaceAll("[^a-zA-Z0-9\\s+]", "").replaceAll(" ", "_").replaceAll("%", "percent")
				.replaceAll("#", "hash").replaceAll("<", "").replaceAll("=", "eq")
				.replaceAll("^", "").replaceAll("'", "").replaceAll("\\+", "plus");
		}catch(Exception e) {
			System.out.println(words+"\n");
			e.printStackTrace();
		}
		return words;
	}
	/*
	 * private SynSet getSynSet(String question) { SynSet synSet=new SynSet(); try {
	 * CSVReader reader = new CSVReader(new
	 * FileReader("D:\\Subjects\\dissertation\\DATASET\\tripleStore_withsyn.csv"),
	 * ',' , '"' , 1); String[] nextLine;
	 * 
	 * while ((nextLine = reader.readNext()) != null) { if (nextLine != null) {
	 * String readquestion=nextLine[3]; if(readquestion.equals(question)) {
	 * synSet.subjectSet.add(nextLine[0]); synSet.predicateSet.add(nextLine[1]);
	 * synSet.objectSet.add(nextLine[2]); } } } } catch (Exception e)
	 * {e.printStackTrace();} return synSet; }
	 */
	private Map<String, SynSet> getSynSet() {
		Map<String, SynSet> map=new HashMap<String, SynSet>();
		try {
			CSVReader reader = new CSVReader(new FileReader("D:\\Subjects\\dissertation\\DATASET\\tripleStore_withsyn.csv"), ',' , '"' , 1);
		    String[] nextLine;
		    
			while ((nextLine = reader.readNext()) != null) {
				     if (nextLine != null) {
				    	 String readquestion=nextLine[3];
				    	 SynSet synSet=null;
				    	 if(map.get(readquestion)==null) {
				    		 synSet=new SynSet();
				    	 }
				    	 else {
				    		 synSet=map.get(readquestion);
				    	 }
				    	 synSet.subjectSet.add(nextLine[0]);
			    		 synSet.predicateSet.add(nextLine[1]);
			    		 synSet.objectSet.add(nextLine[2]);
			    		 map.put(readquestion, synSet);
				     }
				}
			} catch (Exception e) {e.printStackTrace();}
		return map;
	}
	public void writeOntology() throws Exception {
		//Ontology completed -- write to file
        model.write(new FileWriter(polychrest_ontology,false), "TURTLE");
        model.write(new FileOutputStream(new File(owlForm)),"RDF/XML-ABBREV");
       
	}	
}

class SynSet{
	Set<String> subjectSet=new HashSet<String>();
	Set<String> predicateSet=new HashSet<String>();
	Set<String> objectSet=new HashSet<String>();
}