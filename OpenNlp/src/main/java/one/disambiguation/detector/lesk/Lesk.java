package one.disambiguation.detector.lesk;

import one.lexical.db.ILexicalDatabase;
import one.lexical.db.data.Concept;
import one.lexical.db.item.POS;
import one.ws4j.util.GlossFinder;
import one.ws4j.util.OverlapFinder;
import one.ws4j.util.WS4JConfiguration;
import one.ws4j.steer.Relatedness;
import one.ws4j.steer.RelatednessCalculator;

import java.util.ArrayList;
import java.util.List;

/**
 * This class calculates Lesk similarity score between two synsets through
 * the Banerjee and Pedersen (2002) method.
 * <p>
 * From WS package:
 * <p>
 * Lesk (1985) proposed that the relatedness of two words is proportional
 * to the extent of overlaps of their dictionary definitions. Banerjee and
 * Pedersen (2002) extended this notion to use WordNet as the dictionary
 * for the word definitions. This notion was further extended to use the rich
 * network of relationships between concepts present is WordNet. This adapted
 * lesk measure has been implemented in this module.

 */
public class Lesk extends RelatednessCalculator {

    private static double min = 0.0D;
    private static double max = Double.MAX_VALUE;

    private static List<POS[]> POSPairs = new ArrayList<POS[]>() {{
        add(new POS[]{POS.ADJECTIVE, POS.ADJECTIVE});
        add(new POS[]{POS.ADJECTIVE, POS.ADVERB});
        add(new POS[]{POS.ADJECTIVE, POS.NOUN});
        add(new POS[]{POS.ADJECTIVE, POS.VERB});

        add(new POS[]{POS.ADVERB, POS.ADJECTIVE});
        add(new POS[]{POS.ADVERB, POS.ADVERB});
        add(new POS[]{POS.ADVERB, POS.NOUN});
        add(new POS[]{POS.ADVERB, POS.VERB});

        add(new POS[]{POS.NOUN, POS.ADJECTIVE});
        add(new POS[]{POS.NOUN, POS.ADVERB});
        add(new POS[]{POS.NOUN, POS.NOUN});
        add(new POS[]{POS.NOUN, POS.VERB});

        add(new POS[]{POS.VERB, POS.ADJECTIVE});
        add(new POS[]{POS.VERB, POS.ADVERB});
        add(new POS[]{POS.VERB, POS.NOUN});
        add(new POS[]{POS.VERB, POS.VERB});
    }};

    private GlossFinder glossFinder;

    private StringBuilder overlapLog;
    private StringBuilder overlapLogMax;

    public Lesk(ILexicalDatabase db) {
        super(db, min, max);
        glossFinder = new GlossFinder(db);
    }

    @Override
    protected Relatedness calcRelatedness(Concept concept1, Concept concept2) {
        StringBuilder tracer = new StringBuilder();
        if (concept1 == null || concept2 == null) return new Relatedness(min);
        List<GlossFinder.SuperGloss> glosses = glossFinder.getSuperGlosses(concept1, concept2);
        int score = 0;
        for (GlossFinder.SuperGloss gloss : glosses) {
            double functionsScore = calcFromSuperGloss(gloss.getGloss1(), gloss.getGloss2());
            functionsScore *= gloss.getWeight();
            if (WS4JConfiguration.getInstance().useTrace() && functionsScore > 0) {
                tracer.append("LESK(").append(concept1).append(", ").append(concept2).append(")\n");
                tracer.append("Functions: ").append(gloss.getLink1().trim()).append(" - ")
                        .append(gloss.getLink2().trim()).append(" : ").append(functionsScore).append("\n");
                tracer.append(overlapLogMax).append("\n");
            }
            score += functionsScore;
        }
        return new Relatedness(score, tracer.toString(), null);
    }

    private double calcFromSuperGloss(List<String> glosses1, List<String> glosses2) {
        double max = 0;
        overlapLogMax = new StringBuilder();
        for (String gloss1 : glosses1) {
            for (String gloss2 : glosses2) {
                double score = calcFromSuperGloss(gloss1, gloss2);
                if (max < score) {
                    overlapLogMax = overlapLog;
                    max = score;
                }
            }
        }
        return max;
    }

    private double calcFromSuperGloss(String gloss1, String gloss2) {
        OverlapFinder.Overlaps overlaps = OverlapFinder.getOverlaps(gloss1, gloss2);
        double functionsScore = 0;
        if (WS4JConfiguration.getInstance().useTrace()) overlapLog = new StringBuilder("Overlaps: ");
        for (String key : overlaps.getOverlapsHash().keySet()) {
            String[] tempArray = key.split("\\s+");
            int value = (tempArray.length) * (tempArray.length) * overlaps.getOverlapsHash().get(key);
            functionsScore += value;
            if (WS4JConfiguration.getInstance().useTrace()) overlapLog.append(overlaps.getOverlapsHash().get(key))
                    .append(" x \"").append(key).append("\" ");
        }
        if (WS4JConfiguration.getInstance().useTrace()) overlapLog = new StringBuilder();
        if (WS4JConfiguration.getInstance().useLeskNormalizer()) {
            int denominator = overlaps.getLength1() + overlaps.getLength2();
            if (denominator > 0) functionsScore /= (double) denominator;
            if (WS4JConfiguration.getInstance().useTrace()) overlapLog.append("Normalized by dividing the score with ")
                    .append(overlaps.getLength1()).append(" and ").append(overlaps.getLength2()).append("\n");
        }
        return functionsScore;
    }

    @Override
    public List<POS[]> getPOSPairs() {
        return POSPairs;
    }
}
