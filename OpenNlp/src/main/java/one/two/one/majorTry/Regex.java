package one.two.one.majorTry;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Regex  {
	
	String pattern = "\\W";
	
	public static void main(String[] args) {
		Pattern p = Pattern.compile("\\W");
		Matcher m = p.matcher("#");
		boolean b = m.matches();
		System.out.println(b);
	}

}
