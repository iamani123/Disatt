package stanford.related.extraction;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import stanford.related.extraction.*;

import opennlp.tools.parser.Parse;
import opennlp.tools.util.Span;

public class TripleGenerator implements Runnable {

	private final Set<Triple> triple;
	private String[] sentences;
	private Span[][] tokens;
	private String text;
	
	public TripleGenerator(Set<Triple>triple, String t) {
		this.triple=triple;
		text =t;
	}
	protected static Parse getType(Parse p, String type) {
		if (p == null)
			return null;

		if (p.getType().equals(type))
			return p;

		for (Parse kid : p.getChildren()) {
			Parse np = getType(kid, type);
			if (np != null)
				return np;
		}
		return null;
	}
	
	private void findNarrowerAndBroaderTriplesFromNP(Parse np) {
		// we only care about leaf NP
		for (Parse c : np.getChildren()) {
			if (is(c, "NP")) {
				return;
			}
		}
		
		Parse[] children = np.getChildren();
		Parse main = children[children.length - 1];
		for (int i = 0; i < children.length - 1; i++) {
			Parse c = children[i];
			if (!isNoun(c))
				continue;
			Triple t = Triple.narrower(main.toString(), c.toString() + ' ' + main.toString());
			Triple tt = t.inversion();
			synchronized (triple) {
				triple.add(tt);
				triple.add(t);
			}
		}
		
	}
	private void findRelatedTriples(Parse cc) {
		Parse parent = cc.getParent();
//		parent.show();
		List<Parse> related = new ArrayList<Parse>();
		for (Parse sibiling : parent.getChildren()) {
			if (isNoun(sibiling)) {
				related.add(sibiling);
			}
			else if (is(sibiling, "NP")) {
				Parse n = getNounGroupFromNP(sibiling);
				if (n != null) {
					related.add(n);
				}
			}
		}
		for (Parse a : related) {
			for (Parse b : related) {
				if (a.equals(b))
					continue;
				Triple t = Triple.related(a.toString(), b.toString());
				synchronized (triple) {
					triple.add(t);
				}
			}
		}
	}
	
	private static boolean isNoun(Parse node) {
		return node != null && node.getType().startsWith("NN");
	}

	private static boolean isAdjective(Parse node) {
		return node != null && node.getType().startsWith("JJ");
	}

	private static Parse getFirstNoun(Parse root) {
		if (isNoun(root))
			return root;
		for (Parse c : root.getChildren()) {
			Parse v = getFirstNoun(c);
			if (v != null)
				return v;
		}
		return null;
	}
	
	private static Parse getNounGroupFromNP(Parse root) {
		if (isNoun(root))
			return root;
		boolean all_nouns = true;
		for (Parse c : root.getChildren()) {
			if (!isNoun(c)) {
				all_nouns = false;
			}
		}
		if (all_nouns)
			return root;
		for (Parse c : root.getChildren()) {
			Parse v = getFirstNoun(c);
			if (v != null)
				return v;
		}
		return null;
	}

	private static Parse getFirstAdjective(Parse root) {
		if (isAdjective(root))
			return root;
		for (Parse c : root.getChildren()) {
			Parse v = getFirstAdjective(c);
			if (v != null)
				return v;
		}
		return null;
	}

	private static boolean isVerb(Parse node) {
		return node != null && node.getType().startsWith("VB");
	}
	private void traverse(Parse p) {
//		p.show();
		if (is(p, "NP")) {
			findNarrowerAndBroaderTriplesFromNP(p);
		} else if (is(p, "CC")) {
			findRelatedTriples(p);
		} else if (is(p, "S")) {
			getSubjectVerbObjectTriples(p);
		}

		for (Parse child : p.getChildren()) {
			traverse(child);
		}
	}

	private static class Pair {
		public Parse tree;
		public int depth;

		public Pair(Parse p, int d) {
			tree = p;
			depth = d;
		}
	}

	private static Pair getDeepestVerb(Parse p) {
		Pair result = new Pair(p, 0);
		for (Parse c : p.getChildren()) {
			Pair childResult = getDeepestVerb(c);
			if (childResult.depth + 1 > result.depth && isVerb(childResult.tree)) {
				result.depth = 1 + childResult.depth;
				result.tree = childResult.tree;
			}
		}
		return result;
	}
	
	private void makeTriplesFromModifiers(List<Parse> attributes, Parse main) {
		for (Parse a : attributes) {
			Triple t = Triple.narrower(main.toString(), a.toString() + ' ' + main.toString());
			Triple tt = t.inversion();
			synchronized (triple) {
				triple.add(t);
				triple.add(tt);
			}
		}
	}

	private void getSubjectVerbObjectTriples(Parse s) {
		Parse np = null;
		Parse vp = null;
		for (Parse child : s.getChildren()) {
			if (child.getType().equals("NP"))
				np = child;
			else if (child.getType().equals("VP"))
				vp = child;
		}
		if (vp == null || np == null)
			return;

		Parse subject = getSubject(np);
		if (subject == null)
			return;

		Parse verb = getDeepestVerb(vp).tree;
		if (verb == null)
			return;

		// get object, which are siblings of verb tree
		vp = verb.getParent();
		Parse object = null;
		for (Parse sibiling : vp.getChildren()) {
			String t = sibiling.getType();
			if (t.equals("PP") || t.equals("NP")) {
				object = getFirstNoun(sibiling);
				break;
			} else if (t.equals("ADJP")) {
				object = getFirstAdjective(sibiling);
				break;
			}
		}
		if (object == null)
			return;
		
		List<Parse> s_att = getAttributes(subject);
		makeTriplesFromModifiers(s_att, subject);
		List<Parse> v_att = getAttributes(verb);
		makeTriplesFromModifiers(v_att, verb);
		List<Parse> o_att = getAttributes(object);
		makeTriplesFromModifiers(o_att, object);

		Triple t = Triple.lii(subject.toString(), object.toString(), verb.toString());
		if (t.predicate.isEmpty())
			return;
		synchronized (triple) {
			triple.add(t);
		}
	}

	private static Parse getSubject(Parse root) {
		if (isNoun(root))
			return root;
		for (Parse c : root.getChildren()) {
			Parse subject = getSubject(c);
			if (subject != null)
				return subject;
		}
		return null;
	}
	
	private static List<Parse> getAttributes(Parse word) {
		List<Parse> result = new ArrayList<Parse>();
		
		// search siblings
		Parse[] sibilings = word.getParent().getChildren();
		
		if (isAdjective(word)) {
			for (Parse s : sibilings) {
				if (is(s, "RB")) {
					result.add(s);
				}
			}
		} else if (isNoun(word)) {
			for (Parse s : sibilings) {
				if (is(s, "PRP$") || is(s, "POS") || is(s, "JJ") || is(s, "CD") || is(s, "ADJP") || is(s, "QP") || is(s, "NP")) {
					result.add(s);
				}
			}
		} else if (isVerb(word)) {
			for (Parse s : sibilings) {
				if (is(s, "ADVP")) {
					result.add(s);
				}
			}
		}
		
		// search uncles
		Parse[] uncles = word.getParent().getParent().getChildren();
		if (isNoun(word) || isAdjective(word)) {
			for (Parse uncle : uncles) {
				if (is(uncle, "PP")) {
					result.add(uncle);
				}
			}
		} else if (isVerb(word)) {
			for (Parse uncle : uncles) {
				if (isVerb(uncle)) {
					result.add(uncle);
				}
			}
		}
		
		return result;
	}

	private static boolean is(Parse p, String type) {
		return p != null && p.getType().equals(type);
	}

	@Override
	public void run() {
	//	text = Preprocessor.preprocessText(text);
//		System.out.println(text);
		sentences = OpenNLPPipeline.getInstance().getSentences(text);
		tokens = OpenNLPPipeline.getInstance().getTokens(sentences);
		for (int i = 0; i < sentences.length; i++) {
			String sentence = sentences[i];
			Span[] spans = tokens[i];
			Parse[] parses = OpenNLPPipeline.getInstance().parseSentence(sentence, spans, 4);
			
			for (Parse p : parses) {
				if (!is(p.getChildren()[0], "S"))
					continue;
//				System.out.println(sentence);
//				p.show();
				traverse(p);
				break;
			}
		}

	}

}