package one.two.one;

import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.collections4.map.HashedMap;

import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;

public class TakeInputFromUser {

	public static void main(String[] args) {
		
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter your question:");
		String question = sc.nextLine();
		
		generateTriples(question);
		
	}
	
	public static void generateTriples(String s) {
		Map<String, Integer>map = new HashedMap<String, Integer>();
		int count =0;
		  Document doc = new Document(s);
		    // Iterate over the sentences in the document
		    for (Sentence sent : doc.sentences()) {
		      // Iterate over the triples in the sentence
		      for (RelationTriple triple : sent.openieTriples()) {
		        // Print the triple
				
				/*
				 * System.out.println(triple.confidence + "\t" + triple.subjectLemmaGloss() +
				 * "\t" + triple.relationLemmaGloss() + "\t" + triple.objectLemmaGloss());
				 */
				 
		        
		        try {
					
					
					OntologyFactory ontFact = new OntologyFactory();
					String subject = ontFact.processStrings(triple.subjectLemmaGloss());
					String predicate = ontFact.processStrings(triple.relationLemmaGloss());
					String object = ontFact.processStrings(triple.objectLemmaGloss());
					
					
					  System.out.println(subject); System.out.println(predicate);
					  System.out.println(object);
					 
					
					String question = ReadOntology.query(new QueryOntology()
							.interactSPO(subject, predicate, object));
					
				//	System.out.println(question);
					
			//		System.out.println(getNLPQuestion(question));
					
					String gotCleanQuestion[] = getNLPQuestion(question);
					
					for(int i=0; i<gotCleanQuestion.length;i++) {
						
					
				
					if(map.get(gotCleanQuestion[i])==null) {
						map.put(gotCleanQuestion[i], count);
						count++;
					}
					
					}
					
					
					
					
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		      }
		      
		    }
		    
		    for(Map.Entry<String, Integer>sMap: map.entrySet()) {
				if(sMap.getValue()==1) {
					System.out.println(sMap.getKey());
				}
			}
	}
	
	public static String[] getNLPQuestion(String quest) {
		
		String extractOnlyquestion = quest.replaceAll("question", "").replaceAll("@en", "");
		String cleanString = extractOnlyquestion.replaceAll("[^a-zA-Z0-9\\s+]", "").trim();
		String strArray[] = cleanString.split("\r");
		return strArray;
	}
	
	
}
