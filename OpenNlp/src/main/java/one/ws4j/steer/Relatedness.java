package one.ws4j.steer;

import one.ws4j.util.WS4JConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class Relatedness {

    private static final Logger LOGGER = LoggerFactory.getLogger(Relatedness.class);

    private double score;
    private StringBuffer trace;
    private StringBuffer error;

    public Relatedness(double score) {
        this.score = score;
        this.trace = new StringBuffer();
        this.error = new StringBuffer();
    }

    public Relatedness(double score, String trace, String error) {
        this.score = score;
        this.trace = new StringBuffer(trace == null ? "" : trace);
        this.error = new StringBuffer(error == null ? "" : error);
        if (WS4JConfiguration.getInstance().useTrace() && !this.trace.toString().equals("")) {
            for (String str : Objects.requireNonNull(trace).split("\\R")) LOGGER.info(str);
        }
        if (WS4JConfiguration.getInstance().useTrace() && !this.error.toString().equals("")) LOGGER.error(error);
    }

    public String getTrace() {
        return trace.toString();
    }

    void appendTrace(String trace) {
        this.trace.append(trace);
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getError() {
        return error.toString();
    }

    public void appendError(String error) {
        this.error.append(error);
    }
}
