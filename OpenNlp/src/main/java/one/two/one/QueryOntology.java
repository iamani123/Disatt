package one.two.one;

import java.io.FileNotFoundException;

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
	
	
	
}
