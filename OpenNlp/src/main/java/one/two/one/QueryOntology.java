package one.two.one;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.tdb.TDB;
import org.apache.jena.update.GraphStore;
import org.apache.jena.update.GraphStoreFactory;
import org.apache.jena.update.UpdateAction;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateRequest;

public class QueryOntology {
	public static void main(String[] args) {
		
		try {
			String s = ReadOntology.query(queryString());
			System.out.println(s);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		
	}
	
	public static String queryString() {
		
		String query = "PREFIX rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n" + 
				"PREFIX owl:   <http://www.w3.org/2002/07/owl#>\r\n" + 
				"PREFIX xsd:   <http://www.w3.org/2001/XMLSchema#>\r\n" + 
				"PREFIX rdfs:  <http://www.w3.org/2000/01/rdf-schema#>\r\n" + 
				"PREFIX base:  <http://dissertation/ontology#>\r\n" + 
				"\r\n" + 
				"SELECT ?question \r\n" + 
				"WHERE {\r\n" + 
				"		base:Motorola a base:subject;\r\n" + 
				"		rdfs:label ?question.\r\n" + 
				"		base:can_hack a owl:ObjectProperty;\r\n" + 
				"		rdfs:label ?question.\r\n" + 
				"		base:my_Charter_Motorolla_dcx3400 a base:object;\r\n" + 
				"		rdfs:label ?question.\r\n" + 
				"	}";
		
		return query;
	}
	
	//based on subject predicate object
	public String interactSPO(String subject, String predicate, String obj) {
		String query = "PREFIX rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n" + 
				"PREFIX owl:   <http://www.w3.org/2002/07/owl#>\r\n" + 
				"PREFIX xsd:   <http://www.w3.org/2001/XMLSchema#>\r\n" + 
				"PREFIX rdfs:  <http://www.w3.org/2000/01/rdf-schema#>\r\n" + 
				"PREFIX base:  <http://dissertation/ontology#>\r\n" + 
				"\r\n" + 
				"SELECT ?question \r\n" + 
				"WHERE {\r\n" + 
				"		base:"+subject+" a base:subject;\r\n" + 
				"		rdfs:label ?question.\r\n" + 
				"		base:"+predicate+" a owl:ObjectProperty;\r\n" + 
				"		rdfs:label ?question.\r\n" + 
				"		base:"+obj+" a base:object;\r\n" + 
				"		rdfs:label ?question.\r\n" + 
				"	}";
		
		return query;
	}
	
	//based on synonyms in SPO order
	public String interactSPOSyn(String subject, String predicate, String obj) {
		String query = "PREFIX rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n" + 
				"PREFIX owl:   <http://www.w3.org/2002/07/owl#>\r\n" + 
				"PREFIX xsd:   <http://www.w3.org/2001/XMLSchema#>\r\n" + 
				"PREFIX rdfs:  <http://www.w3.org/2000/01/rdf-schema#>\r\n" + 
				"PREFIX base:  <http://dissertation/ontology#>\r\n" + 
				"\r\n" + 
				"SELECT DISTINCT ?question \r\n" + 
				"WHERE {\r\n" + 
				"		?x a base:subject;\r\n" + 
				"		rdfs:comment \""+subject+"\"@en;\r\n" + 
				"		rdfs:label ?question.\r\n" + 
				"		?y a owl:ObjectProperty;\r\n" + 
				"		rdfs:comment \""+predicate+"\"@en;\r\n" + 
				"		rdfs:label ?question.\r\n" + 
				"		?z a base:object;\r\n" + 
				"		rdfs:comment \""+obj+"\"@en;\r\n" + 
				"		rdfs:label ?question.\r\n" + 
				"	}";
		
		return query;
	
	}
	
	//predicate in synonym
	public String interactSIOSyn(String subject, String predicate, String obj) {
		String query = "PREFIX rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n" + 
				"PREFIX owl:   <http://www.w3.org/2002/07/owl#>\r\n" + 
				"PREFIX xsd:   <http://www.w3.org/2001/XMLSchema#>\r\n" + 
				"PREFIX rdfs:  <http://www.w3.org/2000/01/rdf-schema#>\r\n" + 
				"PREFIX base:  <http://dissertation/ontology#>\r\n" + 
				"\r\n" + 
				"SELECT DISTINCT ?question \r\n" + 
				"WHERE {\r\n" + 
				"		base:"+subject+" a base:subject;\r\n" + 
				"		rdfs:label ?question.\r\n" + 
				"		?y a owl:ObjectProperty;\r\n" + 
				"		rdfs:comment \""+predicate+"\"@en;\r\n" + 
				"		rdfs:label ?question.\r\n" + 
				"		base:"+obj+" a base:object;\r\n" + 
				"		rdfs:label ?question.\r\n" + 
				"	}";
		
		return query;
	
	}
	
	//subject in synonym
	public String interactSSIOSyn(String subject, String predicate, String obj) {
		String query = "PREFIX rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n" + 
				"PREFIX owl:   <http://www.w3.org/2002/07/owl#>\r\n" + 
				"PREFIX xsd:   <http://www.w3.org/2001/XMLSchema#>\r\n" + 
				"PREFIX rdfs:  <http://www.w3.org/2000/01/rdf-schema#>\r\n" + 
				"PREFIX base:  <http://dissertation/ontology#>\r\n" + 
				"\r\n" + 
				"SELECT DISTINCT ?question \r\n" + 
				"WHERE {\r\n" + 
				"		?x a base:subject;\r\n" + 
				"		rdfs:comment \""+subject+"\"@en;\r\n" + 
				"		rdfs:label ?question.\r\n" + 
				"		base:"+predicate+" a owl:ObjectProperty;\r\n" + 
				"		rdfs:label ?question.\r\n" + 
				"		base:"+obj+" a base:object;\r\n" + 
				"		rdfs:label ?question.\r\n" + 
				"	}";
		return query;
	
	}
	
	//object in synonym
	public String interactSIObSyn(String subject, String predicate, String obj) {
		String query = "PREFIX rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n" + 
				"PREFIX owl:   <http://www.w3.org/2002/07/owl#>\r\n" + 
				"PREFIX xsd:   <http://www.w3.org/2001/XMLSchema#>\r\n" + 
				"PREFIX rdfs:  <http://www.w3.org/2000/01/rdf-schema#>\r\n" + 
				"PREFIX base:  <http://dissertation/ontology#>\r\n" + 
				"\r\n" + 
				"SELECT DISTINCT ?question \r\n" + 
				"WHERE {\r\n" + 
				"		base:"+subject+" a base:subject;\r\n" + 
				"		rdfs:label ?question.\r\n" + 
				"		base:"+predicate+" a owl:ObjectProperty;\r\n" + 
				"		rdfs:label ?question.\r\n" + 
				"		?z a base:object;\r\n" + 
				"		rdfs:comment \""+obj+"\"@en;\r\n" + 
				"		rdfs:label ?question.\r\n" + 
				"	}";
		return query;
	
	}
	
	//insert questions along with it's triples when question not found.
	/*
	 * public void insertQuestions(String subject, String predicate, String object,
	 * String questionString, ExcelObject synonymObject) throws Exception {
	 * 
	 * String query =
	 * "PREFIX rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n" +
	 * "PREFIX owl:   <http://www.w3.org/2002/07/owl#>\r\n" +
	 * "PREFIX xsd:   <http://www.w3.org/2001/XMLSchema#>\r\n" +
	 * "PREFIX rdfs:  <http://www.w3.org/2000/01/rdf-schema#>\r\n" +
	 * "PREFIX base:  <http://dissertation/ontology#>\r\n" + "\r\n" +
	 * "INSERT { \r\n" + "		base:"+subject+" a base:subject;\r\n" +
	 * "		rdfs:label \""+questionString+"\"@en.\r\n" +
	 * "		base:"+predicate+" a owl:ObjectProperty;\r\n" +
	 * "		rdfs:label \""+questionString+"\"@en.\r\n" +
	 * "		base:"+object+" a base:object;\r\n" +
	 * "		rdfs:label \""+questionString+"\"@en.\r\n" + "		}\r\n" +
	 * "		WHERE {}";
	 * 
	 * InsertData(query);
	 * 
	 * }
	 */
	
	
	
	
	/*
	 * public void insertQuestions(String subject, String predicate, String object,
	 * String questionString, ExcelObject synonymObject) throws Exception {
	 */
	/*
	 * String query =
	 * "PREFIX rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n" +
	 * "PREFIX owl:   <http://www.w3.org/2002/07/owl#>\r\n" +
	 * "PREFIX xsd:   <http://www.w3.org/2001/XMLSchema#>\r\n" +
	 * "PREFIX rdfs:  <http://www.w3.org/2000/01/rdf-schema#>\r\n" +
	 * "PREFIX base:  <http://dissertation/ontology#>\r\n" + "\r\n" +
	 * "INSERT { \r\n" + "		base:"+subject+" a base:subject;\r\n" +
	 * "		rdfs:label \""+questionString+"\"@en;\r\n" +
	 * "		rdfs:comment \""+synonymObject.firstColumn+"\"@en.\r\n" +
	 * "		base:"+predicate+" a owl:ObjectProperty;\r\n" +
	 * "		rdfs:label \""+questionString+"\"@en;\r\n" +
	 * "		rdfs:comment \""+synonymObject.secondColumn+"\"@en.\r\n" +
	 * "		base:"+object+" a base:object;\r\n" +
	 * "		rdfs:comment \""+synonymObject.thirdColumn+"\"@en;\r\n" +
	 * "		rdfs:label \""+questionString+"\"@en.\r\n" + "		}\r\n" +
	 * "		WHERE {}";
	 * 
	 * InsertData(query);
	 * 
	 * }
	 */
			 
		  public void insertQuestions(String subject, String predicate, String object,
				  String questionString) throws Exception {
			  String query =
					  "PREFIX rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n" +
					  "PREFIX owl:   <http://www.w3.org/2002/07/owl#>\r\n" +
					  "PREFIX xsd:   <http://www.w3.org/2001/XMLSchema#>\r\n" +
					  "PREFIX rdfs:  <http://www.w3.org/2000/01/rdf-schema#>\r\n" +
					  "PREFIX base:  <http://dissertation/ontology#>\r\n" + "\r\n" +
					  "INSERT { \r\n" + "		base:"+subject+" a base:subject;\r\n" +
					  "		rdfs:label \""+questionString+"\"@en.\r\n" +
					  "		base:"+predicate+" a owl:ObjectProperty;\r\n" +
					  "		rdfs:label \""+questionString+"\"@en.\r\n" +
					  "		base:"+object+" a base:object;\r\n" +
					  "		rdfs:label \""+questionString+"\"@en.\r\n" + "		}\r\n" +
					  "		WHERE {}";
					  
					  InsertData(query);
					  
					  }
	 
	
	
	public static void InsertData (String query123) throws Exception {
		OntModel m = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
	    m.read(new FileInputStream(OntologyFactory.owlForm),null);
	    
		GraphStore graphStore = GraphStoreFactory.create(m) ;
		UpdateRequest request = UpdateFactory.create(query123);
		UpdateAction.execute(request, graphStore) ;
		TDB.sync(graphStore);
	    //return resultTxt;
		try {
			m.write(new FileWriter(OntologyFactory.polychrest_ontology,false), "TURTLE");
	        m.write(new FileOutputStream(new File(OntologyFactory.owlForm)),"RDF/XML-ABBREV");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	
	
}
