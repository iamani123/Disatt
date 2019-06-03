package one.similarity.check;

import one.disambiguation.detector.lesk.Lesk;
import one.lexical.db.ILexicalDatabase;
import one.lexical.db.MITWordNet;
import one.ws4j.steer.RelatednessCalculator;
import one.ws4j.util.WS4JConfiguration;

import java.util.Arrays;

public class SimilarityCalculationDemo {

    private static RelatednessCalculator[] rcs;

    static {
        WS4JConfiguration.getInstance().setMemoryDB(false);
        WS4JConfiguration.getInstance().setMFS(true);
        //Bringing in the wordnet from remote fit repo
        ILexicalDatabase db = new MITWordNet();
        rcs = new RelatednessCalculator[]{
               new Lesk(db)
        };
    }

    public static void main(String[] args) {
        long t = System.currentTimeMillis();
        Arrays.asList(rcs).forEach(rc -> System.out.println(rc.getClass().getName() + "\t" +
                rc.calcRelatednessOfWords("see", "eye")));
        System.out.println("\nDone in " + (System.currentTimeMillis() - t) + " msec.");
    }
}