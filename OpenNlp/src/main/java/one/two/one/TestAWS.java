package one.two.one;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import edu.smu.tspell.wordnet.*;

public class TestAWS {
	
	String firstColumn;
	String secondColumn;
	String thirdColumn;
	String question;

	
	public TestAWS(String firstColumn, String secondColumn, String thirdColumn, String question) {
		super();
		
		this.firstColumn = firstColumn;
		this.secondColumn = secondColumn;
		this.thirdColumn = thirdColumn;
		this.question = question;
	}
	
	
	private static List<ExcelObject> createExcelInstances() {
		// Reading the values from the triple store and creating instances dynamically.
		List<ExcelObject> listOfRow=new ArrayList<ExcelObject>();
		try {
		//File file = new File("D:\\Subjects\\dissertation\\DATASET\\tripleStore.csv"); 
		CSVReader reader = new CSVReader(new FileReader("D:\\Subjects\\dissertation\\DATASET\\tripleStore.csv"), ',' , '"' , 1);
	    String[] nextLine;
	    
		while ((nextLine = reader.readNext()) != null) {
			     if (nextLine != null) {
			    	 listOfRow.add(new ExcelObject(nextLine[0], nextLine[1], nextLine[2],nextLine[3]));
			     }
			}
		} catch (Exception e) {e.printStackTrace();}
	    return listOfRow;
		
	}
	
	public static void generateSynonymCsv(List<ExcelObject> excelList) {
		System.out.println("size"+excelList.size());
		File file = new File("D:\\Subjects\\dissertation\\DATASET\\tripleStore_withsyn.csv"); 
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
	
	public static void main(String[] args) {
		
		System.setProperty("wordnet.database.dir", "D:\\11\\Wordnet_2.1\\dict");
	//	File file = new File("D:\\Subjects\\dissertation\\DATASET\\tripleStore_withsyn.csv");
		List<ExcelObject> listOfRow=createExcelInstances();
		List<ExcelObject> listOfSynonymRow=new ArrayList<ExcelObject>();
		for(ExcelObject row:listOfRow) {
			String question=row.question;
			Set<String> subjectSynSet=getDef(row.firstColumn);
			Set<String> predicateSynSet=getDef(row.secondColumn);
			Set<String> objectSynSet=getDef(row.thirdColumn);
			for(String subject:subjectSynSet) {
				for(String predicate:predicateSynSet) {
					for(String object:objectSynSet) {
						listOfSynonymRow.add(new ExcelObject(subject, predicate, object, question));
					}
				}
			}
			//break;
		}
		generateSynonymCsv(listOfSynonymRow);
		
		/*
		 * List<String>lists = new LinkedList<String>(); lists.add("I");
		 * lists.add("fire");
		 * 
		 * 
		 * 
		 * if (lists.size() > 0) {
		 * 
		 * //StringBuffer buffer = new StringBuffer(); for (int i = 0; i < lists.size();
		 * i++) { System.out.println(getDef(lists.get(i)));
		 * 
		 * 
		 * } } else { System.err.println("You must specify " +
		 * "a word form for which to retrieve synsets."); }
		 */
		 
	}
	
	public static Set<String> getDef(String wordForm) {
		
        //Creating a set
		wordForm=wordForm.trim();
		
		Set<String>simSet = new HashSet<String>();
		simSet.add(wordForm);
		if(wordForm.split(" ").length>1) return simSet; //if word form contains more than 1 word
		
		
      //  String wordForm = buffer.toString();
        //  Get the synsets containing the wrod form
        WordNetDatabase database = WordNetDatabase.getFileInstance();
        Synset[] synsets = database.getSynsets(wordForm);
        //  Display the word forms and definitions for synsets retrieved
        if (synsets.length > 0)
        {
            System.out.println("The following synsets contain '" +
                    wordForm + "' or a possible base form " +
                    "of that text:");
            for (int i = 0; i < synsets.length; i++)
            {
               // System.out.println("");
                String[] wordForms = synsets[i].getWordForms();
                for (int j = 0; j < wordForms.length; j++)
                {
					/*
					 * System.out.print((j > 0 ? ", " : "") + wordForms[j]);
					 */
                    if(j>0) simSet.add(wordForms[j]);
                }
              //  System.out.println(": " + synsets[i].getDefinition());
            }
        }
        else
        {
            System.err.println("No synsets exist that contain " +
                    "the word form '" + wordForm + "'");
        }
        
        return simSet;
	}
}
