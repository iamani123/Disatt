package one.two.one;


import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.naturalli.NaturalLogicAnnotations;
import edu.stanford.nlp.util.CoreMap;


import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.BasicConfigurator;

import com.opencsv.CSVWriter;

/**
 * A demo illustrating how to call the OpenIE system programmatically.
 */
public class OpenIEDemo {

	public static void main(String[] args) throws Exception {
		BasicConfigurator.configure();
		// Create the Stanford CoreNLP pipeline
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize,ssplit,pos,lemma,depparse,natlog,openie");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		
		//MyCode Starts here
		ArrayList<String>gotQuestion = new ConnectExcel().extractQuestions();
	//	Iterate this ArrayList to get questions one by one
		List<ExcelObject> excelList=new ArrayList<ExcelObject>();
		for (int i=0; i<gotQuestion.size();i++) {
			System.out.println(gotQuestion.get(i));
			Document docu = new Document(gotQuestion.get(i));
			
			
			
			Annotation doc = new Annotation(gotQuestion.get(i));
			System.out.println(doc.toString());
			pipeline.annotate(doc);
		
		
		//Till here it's mine

		// Annotate an example document.
	//	Annotation doc = new Annotation("How to take admission in Trinity? ");
		pipeline.annotate(doc);
		
		// Loop over sentences in the document
		for (CoreMap sentence : doc.get(CoreAnnotations.SentencesAnnotation.class)) {
			// Get the OpenIE triples for the sentence
			Collection<RelationTriple> triples = sentence.get(NaturalLogicAnnotations.RelationTriplesAnnotation.class);
			// Print the triples
			for (RelationTriple triple : triples) {
				System.out.println(triple.confidence + "\t" + triple.subjectLemmaGloss() + "\t"
						+ triple.relationLemmaGloss() + "\t" + triple.objectLemmaGloss());
				ExcelObject excelObject=new ExcelObject(triple.subjectLemmaGloss(),triple.relationLemmaGloss(),triple.objectLemmaGloss(),doc.toString());
				excelList.add(excelObject);
			}
		}
		
	}
	generateUserInterestCsv(excelList);
		
		
	}
	
	public static void generateUserInterestCsv(List<ExcelObject> excelList) {
		System.out.println("size"+excelList.size());
		File file = new File("D:\\Subjects\\dissertation\\DATASET\\tripleStore.csv"); 
	    try { 
	        FileWriter outputfile = new FileWriter(file,false); 
	        CSVWriter writer = new CSVWriter(outputfile); 
	  
	        // adding header to csv 
	        String[] header = { "subject", "predicate", "object", "question" }; 
	        writer.writeNext(header); 
	  
	        // add data to csv
	        for(ExcelObject excelObject:excelList) {
	        	String[] data = { excelObject.firstColumn,  excelObject.secondColumn,excelObject.thirdColumn, excelObject.question};
	        	writer.writeNext(data);
	        }
	  
	        writer.close(); 
	    } 
	    catch (Exception e) { e.printStackTrace(); } 
	}
}

class ExcelObject{
	
	public ExcelObject(String firstColumn, String secondColumn, String thirdColumn, String question) {
		super();
		this.firstColumn = firstColumn.replace(" ", "_");
		this.secondColumn = secondColumn.replace(" ", "_");
		this.thirdColumn = thirdColumn.replace(" ", "_");
		this.question = question;
	}
	String firstColumn;
	String secondColumn;
	String thirdColumn;
	String question;
}