package one.two.one.majorTry;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tp {
	
	public static void main(String[] args) {
	//	String s="(S (S (NP (PRP$ My) (NN name)) (VP (VP (VBZ is) (NP (NNS paras))) (CC and) (VP (VBP am) (VP (VBG living) (PP (IN in) (NP (NNP Dublin))))))) (ADVP (RB now)))"; 
		String s = "(S (S (NP (PRP$ My) (NN name)) (VP (VBZ is) (NP (NNP Paras)))) (CC and) (S (NP (PRP I)) (VP (VBP am) (VP (VBG living) (PP (IN in) (NP (NNP Dublin)))))))";
		Pattern p1 = Pattern.compile("\\(S.*\\(NP.*\\(NN (\\w+)\\).*\\(VP");
	    Matcher m1 = p1.matcher(s);
	   
	    if (m1.find())
	    {
	      String group1 = m1.group(1);
	      System.out.println(group1);
	    }
	    
	    Pattern p2 = Pattern.compile("\\(S.*\\(VP.*\\(VP.*\\(NP.*\\(NN\\S? (\\w+)\\)");
	    Matcher m2 = p2.matcher(s);
	    if (m2.find())
	    {
	      String group1 = m2.group(1);
	      System.out.println(group1);
	    }
	}
}
