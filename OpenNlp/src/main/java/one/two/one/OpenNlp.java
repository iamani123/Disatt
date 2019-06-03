package one.two.one;

import java.awt.RenderingHints.Key;
import java.io.FileInputStream;
import java.io.IOException;

import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.Parser;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;
import opennlp.tools.util.InvalidFormatException;

public class OpenNlp {
	
	public static StringBuffer show1(String text) {
	    StringBuffer sb = new StringBuffer(text.length()*4);
	    return sb;
	  }
	public static void IE(Parse parse) {
		String NP = "", VP = "", PD = "", obj = "";
		
		StringBuffer bf=new StringBuffer(parse.getText());
		parse.show(bf);
	//	System.out.println("OK this is "+bf);
		if (parse.getType().startsWith("S")) {
			Parse[] sent = parse.getChildren();
			System.out.println(sent.toString());
			for (Parse child : sent) {
				System.out.println(child.getLabel());
				if (child.getChildren().length != 0) {
					if (child.getType().equals("NP")) {
						// get that particular token using coveredtext function
						NP = child.getCoveredText();
						System.out.print("Sub: ");
						key(child.getChildren());

					} else if (child.getType().equals("VP")) {
						System.out.println("in line 32" + child.getType());
						obj = key(child.getChildren());
						VP = child.getCoveredText();
						System.out.println("\nPredicate: ");
						PD = getRel(child.getChildren());
						System.out.println("\nObject: ");

						System.out.println("Subject=" + NP + " \t" + "\t" + "Predicate= " + PD + "\t Object =" + obj);
					}

				}
			}
		}

	}

	public static String key(Parse[] parse) {

		String obj = "";
		for (Parse parsekey : parse) {
			if (parsekey.getType().matches("\\w*NN\\w*")) {
				obj = parsekey.getCoveredText();
				System.out.print(parsekey.getCoveredText() + " ");
				System.out.println(obj + "in 59");
			}
			// else if (parsekey.getChildCount() == 0) {
			// return ;
			// }
			else {
				key(parsekey.getChildren());
				obj = parsekey.getCoveredText();
			}

		}
		return obj;

	}

	public static String getRel(Parse[] parses) {
		String predicate = "";
		for (Parse parse : parses) {
			if (parse.getType().matches("\\w*V\\w*")) {
				// System.out.print(parse.getCoveredText());
				predicate = parse.getCoveredText();

			}
		}
		return predicate;
	}

	public static void main(String[] args) throws InvalidFormatException, IOException {
		FileInputStream modelUse = new FileInputStream("D:\\Subjects\\research\\Tools\\en-parser-chunking.bin");
		try {
			ParserModel model = new ParserModel(modelUse);
			Parser parser = ParserFactory.create(model);
			//String sentence = "Is there a difficulty in getting a visa to Middle East countries if I have gone to Israel?";
			String sentence = "My name is Paras and I am living in Dublin";
			Parse topParse[] = ParserTool.parseLine(sentence, parser, 1);
			for (Parse parse : topParse) {
				IE(parse.getChildren()[0]);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (modelUse != null) {
				try {
					modelUse.close();
				} catch (IOException e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		}
	}
}
