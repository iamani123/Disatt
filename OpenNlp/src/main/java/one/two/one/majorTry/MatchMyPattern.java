package one.two.one.majorTry;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;

public class MatchMyPattern {

	public static void main(String[] args) {
		String patternToParse ="(SBARQ (WHNP (WP What)) (SQ (VBZ does) (NP (NN manipulation))) (. mean?))";
		String pattern = ("/^S/ < ^W");
		/*
		//Create a pattern object
		Pattern seePatternObj = Pattern.compile(pattern);
		
		//Creating a matcher object
		Matcher match = seePatternObj.matcher(patternToParse);
		if(match.find()) {
			System.out.println(match.group(0));
			System.out.println(match.group(1));
		} else {
			System.out.println("No Match");
		}
	}*/
		
		
		TregexPattern patternW = TregexPattern.compile(pattern);
	//	TregexMatcher matcher = patternW.matcher(t);
		
}
}
