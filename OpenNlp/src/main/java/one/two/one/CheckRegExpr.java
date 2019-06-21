package one.two.one;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckRegExpr {
	
	public static void main(String[] args) {
		String st = "('step', ['the', 'by step guide'])";
		String stsub ="";
		String withinCircularBraces ="";
		String firstWord ="";
		
		if(st.startsWith("(")==true && st.endsWith(")")) {
			for(int i=0; i<st.length()-1;i++) {
				withinCircularBraces = st.substring(st.indexOf('(')+1,st.lastIndexOf(')'));
				firstWord = st.substring(st.indexOf('(')+1,st.lastIndexOf('['));
				stsub = withinCircularBraces.substring(st.indexOf('['),withinCircularBraces.lastIndexOf(']'));
			} System.out.println(stsub); System.out.println(firstWord);
		}
		
	}

}
