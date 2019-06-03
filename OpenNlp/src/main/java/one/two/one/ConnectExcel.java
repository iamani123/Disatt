package one.two.one;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;


public class ConnectExcel {

	@SuppressWarnings("null")
	public ArrayList<String> extractQuestions( ) throws EncryptedDocumentException, InvalidFormatException, IOException {
		
		ArrayList<String> questions = new ArrayList<String>();
		
		try(Workbook wb = WorkbookFactory.create(
				new File("D:\\Subjects\\dissertation\\DATASET\\"
						+ "test.xlsx"))){
			Sheet sheet = wb.getSheetAt(0);
			
			int firstRowIdentification = sheet.getFirstRowNum();
			int lastRowIdentification = sheet.getLastRowNum();
			
			for(int i=firstRowIdentification+1; i<lastRowIdentification; i++) {
				Row row = sheet.getRow(i);
				for(int j=row.getFirstCellNum()+3; j<row.getLastCellNum()-2;j++) {
					Cell cell = row.getCell(j);
					questions.add (cell.getStringCellValue());
					
				}
	} return questions; 
}
		
	}
}
