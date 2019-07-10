package one.two.one;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import java.awt.Color;

import org.apache.commons.lang.StringUtils;

import com.sun.xml.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;

public class TakeInputFromUser {
	static final int SPO_SCORE=100;
	static final int SYNSPO_SCORE =70;
	static final int SUBJSYNP_OBJ_SCORE =50;
	static final int ONE_IN_SYN_SCORE =20;
	static final int THRESHOLD_SCORE =70;
	
	public static void main(String[] args) throws Exception {

		Scanner sc = new Scanner(System.in);
		String stt = "Ok";
		System.out.println("Enter your question:");
		
		
		
		
		String question = sc.nextLine();

		generateTriples(question);
	}

	public static void generateTriples(String questionString) throws Exception {
		Map<String, Integer>map = new HashMap<String, Integer>();
		boolean addQuestionToStore=false;
		
		List<ExcelObject> tripleToAdd=new ArrayList<ExcelObject>();
		int count =1;int confidenceScore=1;
		Document doc = new Document(questionString);
		// Iterate over the sentences in the document
		for (Sentence sent : doc.sentences()) {
			// Iterate over the triples in the sentence
			for (RelationTriple triple : sent.openieTriples()) {
				try {
					OntologyFactory ontFact = new OntologyFactory();
					String subject = ontFact.processStrings(triple.subjectLemmaGloss());
					String predicate = ontFact.processStrings(triple.relationLemmaGloss());
					String object = ontFact.processStrings(triple.objectLemmaGloss());
					
					tripleToAdd.add(new ExcelObject(subject, predicate, object,questionString));
					System.out.println("Subject: "+subject+"\t"+"Predicate: "+predicate+"\t"+"Object: "+"\t"+object);
					evaluateTriple(subject,predicate,object,map);

				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

		boolean foundExactSimilar=false;
		for(Map.Entry<String, Integer>sMap: map.entrySet()) { 
			 
			System.out.println("question: "+sMap.getKey()+"\nscore: "+sMap.getValue()+"\n");
			
			if(questionString.equalsIgnoreCase(sMap.getKey()+"?"))foundExactSimilar=true;
			if(!addQuestionToStore) {
				System.out.println("Do you find this question similar? (y/n)");
				String opinion=new Scanner(System.in).next();
				if(opinion.equalsIgnoreCase("y")) addQuestionToStore=true;
			}
		} 
		
		//if no semantic questions found 
		if(map.entrySet().size()==0) addQuestionToStore=true; 
		
		addQuestionToStore=true;
		//add to triple store
		if(addQuestionToStore && (!foundExactSimilar)) addTriplesToStore(tripleToAdd);
		
	}


	private static void addTriplesToStore(List<ExcelObject> tripleToAdd) throws Exception {
	//	System.out.println("before finding synonym\n"+tripleToAdd);
		
		for(ExcelObject triple:tripleToAdd) {
			List<ExcelObject> oneTripleToAdd=new ArrayList<ExcelObject>();
			oneTripleToAdd.add(triple);
			List<ExcelObject> listOfSynonymRow=TestAWS.getListOfSynonymRow(oneTripleToAdd);
			for(ExcelObject synonymObject:listOfSynonymRow)
				new QueryOntology().insertQuestions(triple.firstColumn,triple.secondColumn,triple.thirdColumn, triple.question,synonymObject);
		}
	}

	private static void evaluateTriple(String subject, String predicate, String object,Map<String, Integer>map) throws FileNotFoundException {
		int score=0;
		//1st level --entire URI match for all subject, predicate, object.
		
		String question = ReadOntology.query(new QueryOntology().interactSPO(subject, predicate, object));
		if(determineBoost(question, map, SPO_SCORE)) return;

		//2nd level --- subject predicate object in Synonyms.
		
		question = ReadOntology.query(new QueryOntology().interactSPOSyn(subject,predicate,object));
		if(determineBoost(question, map, SYNSPO_SCORE)) return;

		
		//3rd Level --pred in synonyms; subject,object,outer.
		
		question = ReadOntology.query(new QueryOntology().interactSIOSyn(subject,predicate,object));
		if(determineBoost(question, map, SUBJSYNP_OBJ_SCORE)) return;
		
		//4th level --subj in synonyms; predicate and object outer.
		
		question = ReadOntology.query(new QueryOntology().interactSSIOSyn(subject,predicate,object));
		if(determineBoost(question, map, ONE_IN_SYN_SCORE)) return;
		
		//5th Level --obj in synonyms rest outside
		question = ReadOntology.query(new QueryOntology().interactSIObSyn(subject,predicate,object));
		if(determineBoost(question, map, ONE_IN_SYN_SCORE)) return;
		
	}

	private static boolean determineBoost(String question, Map<String, Integer> map, int score) {
		String gotCleanQuestion[] = getNLPQuestion(question);
		if(! (gotCleanQuestion[0].trim()==null || gotCleanQuestion[0].trim().equals(""))) {
			for(String cleanQuestion:gotCleanQuestion) {
				cleanQuestion=cleanQuestion.trim();
				if(map.get(cleanQuestion)==null) map.put(cleanQuestion,score);
				else map.put(cleanQuestion, (map.get(cleanQuestion)+score) );
			}
			return true;
		} else { System.out.println("No questions");}
		return false;
	}

	public static String[] getNLPQuestion(String quest) {

		String extractOnlyquestion = quest.replaceAll("question", "").replaceAll("@en", "");
		String cleanString = extractOnlyquestion.replaceAll("[^a-zA-Z0-9\\s+]", "").trim();
	//	String cleanString = StringUtils.substringAfter(extractOnlyquestion, "").trim();
		String strArray[] = cleanString.split("\r");
		return strArray;
	}


}

class Triple{
	String subject;
	String predicate;
	String object;
	public Triple(String subject, String predicate, String object) {
		super();
		this.subject = subject;
		this.predicate = predicate;
		this.object = object;
	}
	
}