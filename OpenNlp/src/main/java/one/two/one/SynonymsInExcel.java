package one.two.one;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

import com.opencsv.CSVWriter;





public class SynonymsInExcel {
	
	public void writeSynToExcel(Set<TestAWS>excelList) {
		try {
			FileWriter outputfile = new FileWriter("D:\\Subjects\\dissertation\\DATASET\\synonyms.csv",false);
			 String[] header = { "subject", "predicate", "object", "question" };
			  CSVWriter writer = new CSVWriter(outputfile);
			  writer.writeNext(header); 
			  
			  for(TestAWS excelObject:excelList) {
		        	String[] data = { excelObject.firstColumn,  excelObject.secondColumn,excelObject.thirdColumn, excelObject.question};
		        	writer.writeNext(data);
		        }
		  
		        writer.close(); 
			  
			  
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		finally {
					
		}
	}

	

}
