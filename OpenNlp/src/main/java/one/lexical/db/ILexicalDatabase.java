package one.lexical.db;

import java.util.List;

import one.lexical.db.data.Concept;
import one.lexical.db.item.Link;
import one.lexical.db.item.POS;

public interface ILexicalDatabase {

	  Concept getConcept(String lemma, POS pos, int sense);

	    List<Concept> getAllConcepts(String lemma, POS pos);

	    List<Concept> getLinkedSynsets(Concept concept, Link link);

	    List<String> getWords(Concept concept);

	    List<String> getGloss(Concept concept, Link link);
}
