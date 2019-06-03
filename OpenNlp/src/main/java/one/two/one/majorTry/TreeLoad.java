package one.two.one.majorTry;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import edu.stanford.nlp.trees.LabeledScoredTreeFactory;
import edu.stanford.nlp.trees.PennTreeReader;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeFactory;
import edu.stanford.nlp.trees.TreeReader;

public class TreeLoad {

	public static void printSubTrees(Tree t) {
        if (t.isLeaf())
            return;
        System.out.println(t);
        for (Tree subTree : t.children()) {
            printSubTrees(subTree);
        }
    }
	
	public static void main(String[] args) throws IOException, 
	FileNotFoundException, UnsupportedEncodingException {
		
		TreeFactory tf = new LabeledScoredTreeFactory();
		Reader r = new BufferedReader(new InputStreamReader(System.in));
		TreeReader tr = new PennTreeReader(r, tf);
		//new Penn
        Tree t = tr.readTree();
        printSubTrees(t);
	}
}
